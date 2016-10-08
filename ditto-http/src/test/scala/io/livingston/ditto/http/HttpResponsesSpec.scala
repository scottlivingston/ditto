package io.livingston.ditto.http

import io.livingston.ditto.Latency
import org.scalatest.{Matchers, WordSpec}

class HttpResponsesSpec extends WordSpec with Matchers {
  val original =
    HttpResponses(
      List(
        HttpServerConfig(
          0,
          List(
            HttpEndpoint(
              "/get",
              "GET",
              200,
              "OK",
              Latency(0,0)
            )
          )
        )
      )
    )

  "HttpResponses Protocol" should {
    "be reversible" in {
      import HttpResponsesProtocol._
      import net.jcazevedo.moultingyaml._
      val yaml = original.toYaml.prettyPrint
      val roundTrip = yaml.parseYaml.convertTo[HttpResponses]

      roundTrip should be(original)
    }
  }
}
