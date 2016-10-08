package io.livingston.ditto.thrift

import java.util.Base64

import io.livingston.ditto.{Latency, Responses}
import net.jcazevedo.moultingyaml._

import scala.collection.immutable.Map

object ThriftResponsesProtocol extends DefaultYamlProtocol {
  implicit val thriftLatencyFormat = yamlFormat2(Latency)
  implicit object ThriftEndpointYamlFormat extends YamlFormat[ThriftEndpoint] {
    def write(e: ThriftEndpoint): YamlValue = {
      def encode(bytes: Array[Byte]): String = Base64.getEncoder.encodeToString(bytes)
      YamlObject(
        Map[YamlValue, YamlValue](
          YamlString("msg") -> YamlString(e.msg),
          YamlString("body") -> YamlString(encode(e.body.toArray)),
          YamlString("latency") -> thriftLatencyFormat.write(e.latency)
        )
      )
    }

    def read(value: YamlValue): ThriftEndpoint = {
      def decode(str: String): Array[Byte] = Base64.getDecoder.decode(str)
      value match {
        case YamlObject(map) =>
          val msg = map(YamlString("msg")) match {
            case YamlString(msg) => msg
            case _ => deserializationError("msg key expected to be a string")
          }
          val body = map(YamlString("body")) match {
            case YamlString(body) => decode(body).toList
            case _ => deserializationError("body key expected to be a string")
          }
          val latency = thriftLatencyFormat.read(map(YamlString("latency")))
          ThriftEndpoint(msg, body, latency)

        case _ => deserializationError("ThriftEndpoint expected")
      }
    }
  }
  implicit val thriftServerConfigFormat = yamlFormat2(ThriftServerConfig)
  implicit val thriftResponseFormat = yamlFormat1(ThriftResponses)
}

case class ThriftResponses(thrift: List[ThriftServerConfig]) extends Responses
case class ThriftServerConfig(port: Int, endpoints: List[ThriftEndpoint])
case class ThriftEndpoint(msg: String, body: List[Byte], latency: Latency)

