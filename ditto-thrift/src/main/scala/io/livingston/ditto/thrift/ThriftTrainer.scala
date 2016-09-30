package io.livingston.ditto.thrift

import com.twitter.finagle.thrift.{Protocols, ThriftClientRequest}
import com.twitter.finagle.{ListeningServer, Service, Thrift}
import com.twitter.util.{Await, Future}
import com.typesafe.scalalogging.LazyLogging
import org.apache.thrift.transport.TMemoryInputTransport

import scala.util.Try

object ThriftTrainer extends App with LazyLogging {
  val trainer = new ThriftTrainer()
  trainer.start()

  sys.addShutdownHook {
    println(trainer.conf)
    trainer.close()
  }

  Await.ready(trainer.server.get)
}

class ThriftTrainer {
  val port = 8080
  val client = Thrift.client.newClient(s":8081").toService

  var trainedResponses = Set.empty[(String, Array[Byte])]

  val service = new Service[Array[Byte], Array[Byte]] {
    def apply(request: Array[Byte]): Future[Array[Byte]] = {
      client(new ThriftClientRequest(request, false)).map { response =>
        val inputTransport = new TMemoryInputTransport(response)
        val thriftRequest = Protocols.binaryFactory().getProtocol(inputTransport)
        Try {
          val msg = thriftRequest.readMessageBegin()
          trainedResponses.synchronized {
            trainedResponses = trainedResponses + (msg.name -> response)
          }
        }
        response
      }
    }
  }

  var server: Option[ListeningServer] = None

  def start() = server = Option(Thrift.server.serve(s":$port", service))

  def conf: String = trainedResponses.foldLeft(Map.empty[String, Seq[String]]) { (acc, call) =>
    acc.get(call._1) match {
      case Some(list) => acc + (call._1 -> (list :+ call._2.map("%02X" format _).mkString))
      case None => acc + (call._1 -> Seq(call._2.map("%02X" format _).mkString))
    }
  }.toString

  def close(): Unit = Await.all(client.close(), server.get.close())
}
