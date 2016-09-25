package io.livingston.ditto

import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.{Http, Service}
import com.twitter.util.{Await, Duration, Future, Stopwatch}

object DittoServer extends App with DittoConfig {

  val http = httpServers map { case (port, endpoints) =>
    val service = new Service[Request, Response] {
      def apply(request: Request): Future[Response] = {
        endpoints.get(request.uri).map { e =>
          val response = Response(request.version, Status(e.status))
          response.contentString = e.body
          Thread.sleep(e.latency.sleepTime)
          Future.value(response)
        }.getOrElse {
          request.close()
          Future.exception(new Exception("Invalid Request"))
        }
      }
    }
    Http.serve(s":$port", service)
  }

  Await.ready(http.head)
}
