package io.livingston.ditto.responses.http

import io.livingston.ditto.responses.{Endpoint, Latency, Responses, ServerConfig}
import net.jcazevedo.moultingyaml.DefaultYamlProtocol

object HttpResponsesProtocol extends DefaultYamlProtocol {
  implicit val latencyFormat = yamlFormat2(Latency)
  implicit val endpointFormat = yamlFormat4(HttpEndpoint)
  implicit val httpFormat = yamlFormat2(HttpServerConfig)
}

case class HttpServerConfig(port: Int, endpoints: List[HttpEndpoint]) extends ServerConfig
case class HttpEndpoint(uri: String, status: Int, body: String, latency: Latency) extends Endpoint

