package io.livingston.ditto.thrift

import com.twitter.finagle.Thrift
import com.twitter.util.{Await, Future}
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}

class ThriftResponderSpec extends WordSpec with Matchers with BeforeAndAfter {
  val port = 8081
  val yaml =
    """
      |---
      |thrift:
      |- port: 8082
      |  endpoints:
      |  - msg: "test"
      |    body: ""
      |    latency:
      |      min: 10
      |      max: 100
      |
    """.stripMargin

  val server = new ThriftResponder()

  before {
    server.apply(yaml)
  }

  after {
    server.close()
  }

  "Thrift config" should {
    "respond correctly to thrift requests" in {
      val client = Thrift.client.newIface[Test[Future]](s":$port")
      val response = Await.result(client.test())
    }
  }
}
