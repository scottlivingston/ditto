package io.livingston.ditto

import com.twitter.finagle.http.Status
import net.jcazevedo.moultingyaml.DefaultYamlProtocol

object ResponsesProtocol extends DefaultYamlProtocol {
  implicit val latencyFormat = yamlFormat2(Latency)
  implicit val endpointFormat = yamlFormat4(Endpoint)
  implicit val httpFormat = yamlFormat2(Server)
  implicit val responsesFormat = yamlFormat1(Responses)
}


case class Responses(http: Option[List[Server]])
case class Server(port: Int, endpoints: Option[List[Endpoint]] = None)
case class Endpoint(uri: String, status: Int, body: String, latency: Latency)
case class Latency(min: Int, max: Int)

case class HttpResponse(statusCode: Status, headers: Map[String, String], latency: Latency, body: String)
