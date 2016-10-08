package io.livingston.ditto.http

import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.{Http, ListeningServer, Service}
import com.twitter.util.{Await, Future}
import com.typesafe.scalalogging.LazyLogging
import io.livingston.ditto.Responder

import scala.collection.immutable.Iterable

class HttpResponder extends Responder with LazyLogging {
  val protocol: String = "http"
  private var _serverConfigs = Seq.empty[HttpServerConfig]
  private var _listeningServers = Iterable.empty[ListeningServer]


  def announce(): Unit = {
    _serverConfigs.foreach { server =>
      super.announce(server.port, server.endpoints.map(e => s"${e.method} => ${e.uri}"))
    }
  }

  def apply(yaml: String): Unit = {
    type MethodAndUri = (String, String)
    import HttpResponsesProtocol._
    super.parse[HttpResponses](yaml, _.convertTo[HttpResponses]) { responses =>
      _serverConfigs = responses.http

      val httpServers = responses.http.foldLeft(Map.empty[Int, Map[MethodAndUri, HttpEndpoint]]) { (serverMap, server) =>
        serverMap + (server.port -> server.endpoints.foldLeft(Map.empty[MethodAndUri, HttpEndpoint]) { (endpointMap, endpoint) =>
          endpointMap + ((endpoint.method -> endpoint.uri) -> endpoint)
        })
      }

      _listeningServers = httpServers.map { case (port, endpoints) =>
        val service = new Service[Request, Response] {
          def apply(request: Request): Future[Response] = {
            endpoints.get(request.method.toString -> request.uri).map { e =>
              val response = Response(request.version, Status(e.status))
              response.contentString = e.body
              Thread.sleep(e.latency.sleepTime)
              Future.value(response)
            }.getOrElse {
              Future.value(Response(request.version, Status.BadRequest))
            }
          }
        }
        Http.serve(s":$port", service)
      }
    }
  }

  def close(): Unit = {
    import com.twitter.conversions.time._
    Await.all( _listeningServers.map(_.close()).toSeq, 5.seconds )
  }
}
