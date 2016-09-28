package io.livingston.ditto.http

import io.livingston.ditto.responses.{Endpoint, Latency, ServerConfig}
import net.jcazevedo.moultingyaml.DefaultYamlProtocol

object HttpResponsesProtocol extends DefaultYamlProtocol {
  implicit val httpLatencyFormat = yamlFormat2(Latency)
  implicit val httpEndpointFormat = yamlFormat4(HttpEndpoint)
  implicit val httpServerConfigFormat = yamlFormat2(HttpServerConfig)
  implicit val httpResponsesFormat = yamlFormat1(HttpResponses)
}

case class HttpResponses(http: List[HttpServerConfig])
case class HttpServerConfig(port: Int, endpoints: List[HttpEndpoint]) extends ServerConfig
case class HttpEndpoint(uri: String, status: Int, body: String, latency: Latency) extends Endpoint

