package io.livingston.ditto.responses.thrift

import io.livingston.ditto.responses.Latency
import net.jcazevedo.moultingyaml.DefaultYamlProtocol

object ThriftResponsesProtocol extends DefaultYamlProtocol {
  implicit val thriftLatencyFormat = yamlFormat2(Latency)
  implicit val thriftEndpointFormat = yamlFormat3(ThriftEndpoint)
  implicit val thriftServerConfigFormat = yamlFormat2(ThriftServerConfig)
  implicit val thriftResponseFormat = yamlFormat1(ThriftResponses)
}

case class ThriftResponses(thrift: List[ThriftServerConfig])
case class ThriftServerConfig(port: Int, endpoints: List[ThriftEndpoint])
case class ThriftEndpoint(msg: String, body: String, latency: Latency)
