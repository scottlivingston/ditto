package io.livingston.ditto

import io.livingston.ditto.responses.{Latency, Responses}
import io.livingston.ditto.responses.http.{HttpEndpoint, HttpServerConfig}
import org.scalatest.{Matchers, WordSpec}

class YamlParseSpec extends WordSpec with Matchers {
  val obj =
    Responses(List(
      HttpServerConfig(8081, List(
        HttpEndpoint("/get", 200, "OK", Latency(10, 50)),
        HttpEndpoint("/bad", 500, "BAD", Latency(10, 50))
      ))
    ))


  "Valid Yaml structure" should {
    "unserialize correctly" in {
      import io.livingston.ditto.responses.ResponsesProtocol._
      import net.jcazevedo.moultingyaml._

      val str = """---
                  |http:
                  |- port: 8081
                  |  endpoints:
                  |  - uri: "/get"
                  |    status: 200
                  |    body: "OK"
                  |    latency:
                  |      min: 10
                  |      max: 50
                  |
                  |  - uri: "/bad"
                  |    status: 500
                  |    body: "BAD"
                  |    latency:
                  |      min: 10
                  |      max: 50
                  |""".stripMargin

      val r = str.parseYaml.convertTo[Responses]
      r should be(obj)
    }
  }
}
