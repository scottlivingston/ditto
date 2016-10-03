package io.livingston.ditto.thrift

import java.util.Base64

import com.twitter.finagle.thrift.{Protocols, ThriftClientRequest}
import com.twitter.finagle.{ListeningServer, Service, Thrift}
import com.twitter.util.{Await, Future}
import com.typesafe.scalalogging.LazyLogging
import org.apache.thrift.transport.TMemoryInputTransport

import scala.util.Try

case class CallRecord(msg: String, request: String, response: String)
object CallRecord {
  private def base64(bytes: Array[Byte]): String = Base64.getEncoder.encodeToString(bytes)

  def apply(msg: String, request: Array[Byte], response: Array[Byte]): CallRecord = {
    CallRecord(msg, base64(request), base64(response))
  }
}

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

  var trainedResponses = Set.empty[CallRecord]

  val service = new Service[Array[Byte], Array[Byte]] {
    def apply(request: Array[Byte]): Future[Array[Byte]] = {
      client(new ThriftClientRequest(request, false)).map { response =>
        val inputTransport = new TMemoryInputTransport(response)
        val thriftRequest = Protocols.binaryFactory().getProtocol(inputTransport)
        Try {
          val msg = thriftRequest.readMessageBegin()
          trainedResponses.synchronized {
            trainedResponses = trainedResponses + CallRecord(msg.name, request, response)
          }
        }
        response
      }
    }
  }

  var server: Option[ListeningServer] = None

  def start() = server = Option(Thrift.server.serve(s":$port", service))

  def conf: String = trainedResponses.foldLeft(Map.empty[String, Map[String, Set[String]]]) { (acc, call) =>
      val requests = acc.get(call.msg) match {
        case Some(rr) => rr
        case None => Map.empty[String, Set[String]]
      }
      val responses = requests.get(call.request) match {
        case Some(set) => set + call.response
        case None => Set(call.response)
      }
      acc + (call.msg -> (requests + (call.request -> responses)))
    }.toString

  def close(): Unit = Await.all(client.close(), server.get.close())
}
