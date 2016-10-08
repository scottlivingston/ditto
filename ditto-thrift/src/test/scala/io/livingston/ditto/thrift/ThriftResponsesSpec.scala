package io.livingston.ditto.thrift

import io.livingston.ditto.Latency
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}

class ThriftResponsesSpec extends WordSpec with Matchers with BeforeAndAfter {
  val original =
    ThriftResponses(
      List(
        ThriftServerConfig(
          123,
          List(
            ThriftEndpoint(
              "test",
              "weee".getBytes.toList,
              Latency(10, 100)
            ),
            ThriftEndpoint(
              "test2",
              "whooaa".getBytes.toList,
              Latency(200, 500)
            )
          )
        )
      )
    )

  "ThriftResponses Protocol" should {
    "be reversible" in {
      import ThriftResponsesProtocol._
      import net.jcazevedo.moultingyaml._
      val yaml = original.toYaml.prettyPrint
      val roundTrip = yaml.parseYaml.convertTo[ThriftResponses]

      roundTrip should be(original)
    }
  }
}
