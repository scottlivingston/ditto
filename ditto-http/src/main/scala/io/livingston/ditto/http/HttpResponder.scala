package io.livingston.ditto.http

import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.{Http, Service}
import com.twitter.util.Future
import com.typesafe.scalalogging.LazyLogging
import io.livingston.ditto.Responder

class HttpResponder extends Responder with LazyLogging {
  val protocol: String = "http"
  private var _serverConfigs = Seq.empty[HttpServerConfig]

  def announce(): Unit = {
    _serverConfigs.foreach { server =>
      super.announce(server.port, server.endpoints.map(_.uri))
    }
  }

  def apply(yaml: String): Unit = {
    import HttpResponsesProtocol._
    super.parse[HttpResponses](yaml, _.convertTo[HttpResponses]) { responses =>
      _serverConfigs = responses.http

      val httpServers = responses.http.foldLeft(Map.empty[Int, Map[String, HttpEndpoint]]) { (serverMap, server) =>
        serverMap + (server.port -> server.endpoints.foldLeft(Map.empty[String, HttpEndpoint]) { (endpointMap, endpoint) =>
          endpointMap + (endpoint.uri -> endpoint)
        })
      }

      val http = httpServers.map { case (port, endpoints) =>
        val service = new Service[Request, Response] {
          def apply(request: Request): Future[Response] = {
            endpoints.get(request.uri).map { e =>
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
}
