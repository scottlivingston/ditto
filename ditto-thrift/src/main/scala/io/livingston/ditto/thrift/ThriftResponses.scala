package io.livingston.ditto.thrift

import io.livingston.ditto.{Latency, Responses}
import net.jcazevedo.moultingyaml.DefaultYamlProtocol

object ThriftResponsesProtocol extends DefaultYamlProtocol {
  implicit val thriftLatencyFormat = yamlFormat2(Latency)
  implicit val thriftEndpointFormat = yamlFormat3(ThriftEndpoint)
  implicit val thriftServerConfigFormat = yamlFormat2(ThriftServerConfig)
  implicit val thriftResponseFormat = yamlFormat1(ThriftResponses)
}

case class ThriftResponses(thrift: List[ThriftServerConfig]) extends Responses
case class ThriftServerConfig(port: Int, endpoints: List[ThriftEndpoint])
case class ThriftEndpoint(msg: String, body: String, latency: Latency)
