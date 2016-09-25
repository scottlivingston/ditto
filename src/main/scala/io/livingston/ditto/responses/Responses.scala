package io.livingston.ditto.responses

import io.livingston.ditto.responses.http.{HttpEndpoint, HttpServerConfig}
import net.jcazevedo.moultingyaml.DefaultYamlProtocol

object ResponsesProtocol extends DefaultYamlProtocol {
  import io.livingston.ditto.responses.http.HttpResponsesProtocol._
  implicit val responsesFormat = yamlFormat1(Responses)
}

case class Responses(http: List[HttpServerConfig])

trait ServerConfig{
  val port: Int
  val endpoints: List[Endpoint]
}

trait Endpoint

case class Latency(min: Int, max: Int) {
  import scala.util.Random
  def sleepTime: Long = {
    Random.nextInt(max + 1 - min) + min
  }
}
