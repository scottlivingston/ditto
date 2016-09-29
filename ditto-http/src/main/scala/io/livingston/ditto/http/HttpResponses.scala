package io.livingston.ditto.http

import io.livingston.ditto.{Endpoint, Latency, Responses, ServerConfig}
import net.jcazevedo.moultingyaml.DefaultYamlProtocol

object HttpResponsesProtocol extends DefaultYamlProtocol {
  implicit val httpLatencyFormat = yamlFormat2(Latency)
  implicit val httpEndpointFormat = yamlFormat5(HttpEndpoint)
  implicit val httpServerConfigFormat = yamlFormat2(HttpServerConfig)
  implicit val httpResponsesFormat = yamlFormat1(HttpResponses)
}

case class HttpResponses(http: List[HttpServerConfig]) extends Responses
case class HttpServerConfig(port: Int, endpoints: List[HttpEndpoint]) extends ServerConfig
case class HttpEndpoint(uri: String, method: String, status: Int, body: String, latency: Latency) extends Endpoint

