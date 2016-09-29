package io.livingston.ditto.thrift

import com.twitter.finagle.thrift.Protocols
import com.twitter.finagle.{Service, Thrift}
import com.twitter.util.Future
import com.typesafe.scalalogging.LazyLogging
import io.livingston.ditto.Responder
import org.apache.thrift.transport.TMemoryInputTransport

import scala.util.Try

class ThriftResponder extends Responder with LazyLogging {
  val protocol: String = "thrift"
  private var _serverConfigs = Seq.empty[ThriftServerConfig]

  def announce(): Unit = {
    _serverConfigs.foreach { server =>
      super.announce(server.port, server.endpoints.map(_.msg))
    }
  }

  def apply(yaml: String): Unit = {
    import ThriftResponsesProtocol._
    super.parse[ThriftResponses](yaml, _.convertTo[ThriftResponses]) { responses =>
      _serverConfigs = responses.thrift

      val thriftServers = responses.thrift.foldLeft(Map.empty[Int, Map[String, ThriftEndpoint]]) { (serverMap, server) =>
        serverMap + (server.port -> server.endpoints.foldLeft(Map.empty[String, ThriftEndpoint]) { (endpointMap, endpoint) =>
          endpointMap + (endpoint.msg -> endpoint)
        })
      }

      val thrift = thriftServers.map { case (port, endpoints) =>
        val service = new Service[Array[Byte], Array[Byte]] {
          def apply(request: Array[Byte]): Future[Array[Byte]] = {
            val inputTransport = new TMemoryInputTransport(request)
            val thriftRequest = Protocols.binaryFactory().getProtocol(inputTransport)
            Try {
              val msg = thriftRequest.readMessageBegin()
              endpoints.get(msg.name).map { e =>
                val response = Array.empty[Byte]
                Thread.sleep(e.latency.sleepTime)
                Future.value(response)
              }.getOrElse {
                Future.exception(new Exception("Invalid Request"))
              }
            }.getOrElse(Future.exception(new Exception("")))
          }
        }
        Thrift.server.serve(s":$port", service)
      }
    }
  }
}
