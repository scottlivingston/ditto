package io.livingston.ditto

import org.scalatest.{Matchers, WordSpec}

class YamlParseSpec extends WordSpec with Matchers {
  val obj =
    Responses(List(
      Server(8081, List(
        Endpoint("/get", 200, "OK", Latency(10, 50)),
        Endpoint("/bad", 500, "BAD", Latency(10, 50))
      ))
    ))


  "Valid Yaml structure" should {
    "unserialize correctly" in {
      import ResponsesProtocol._
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
