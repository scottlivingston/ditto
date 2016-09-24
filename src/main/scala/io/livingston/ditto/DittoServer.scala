package io.livingston.ditto

import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.{Http, Service}
import com.twitter.server.TwitterServer
import com.twitter.util.{Await, Future}
import net.jcazevedo.moultingyaml._


object DittoServer extends TwitterServer {
  val service = new Service[Request, Response] {
    def apply(request: Request) = {
      request.uri
      val response = Response(request.version, Status.Ok)
      response.contentString = "hello"
      Future.value(response)
    }
  }




  def main() {
    import ResponsesProtocol._

    val lines = scala.io.Source.fromFile("config.yml").mkString
    val test = lines.parseYaml.convertTo[Responses]

    println(test)
//    val server = Http.serve(":8888", service)
//    onExit {
//      Await.result(server.close())
//    }
//    Await.ready(server)
  }
}
