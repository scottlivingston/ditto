package io.livingston.ditto.thrift

import java.util.Base64

import com.twitter.finagle.thrift.{Protocols, ThriftClientRequest}
import com.twitter.finagle.{ListeningServer, Service, Thrift}
import com.twitter.util.{Await, Future}
import com.typesafe.scalalogging.LazyLogging
import io.livingston.ditto.Latency
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
  val trainer = new ThriftTrainer(8080, 8081)
  trainer.start()

  sys.addShutdownHook {
    println("Begin Config File")
    println("---\n" + trainer.conf)
    println("End Config File")
    trainer.close()
  }

  Await.ready(trainer.server.get)
}

class ThriftTrainer(servicePort: Int, listenPort: Int) {
  val client = Thrift.client.newClient(s":$servicePort").toService
  type Msg = String
  var trainedResponses = Map.empty[Msg, ThriftEndpoint]

  val service = new Service[Array[Byte], Array[Byte]] {
    def apply(request: Array[Byte]): Future[Array[Byte]] = {
      client(new ThriftClientRequest(request, false)).map { response =>
        val inputTransport = new TMemoryInputTransport(response)
        val thriftRequest = Protocols.binaryFactory().getProtocol(inputTransport)
        Try {
          val msg = thriftRequest.readMessageBegin()
          trainedResponses.synchronized {
            trainedResponses = trainedResponses + (msg.name -> ThriftEndpoint(msg.name, response.toList, Latency()))
          }
        }
        response
      }
    }
  }

  var server: Option[ListeningServer] = None

  def start() = server = Option(Thrift.server.serve(s":$listenPort", service))

  def conf: String = {
    import net.jcazevedo.moultingyaml._
    import ThriftResponsesProtocol._
    ThriftResponses(List(ThriftServerConfig(servicePort, trainedResponses.values.toList))).toYaml.prettyPrint
  }

  def close(): Unit = Await.all(client.close(), server.get.close())
}
