package io.livingston.ditto.thrift

import java.net.ServerSocket

import com.twitter.finagle.Thrift
import com.twitter.util.Await
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}

class ThriftResponderSpec extends WordSpec with Matchers with BeforeAndAfter {
  val s = new ServerSocket(0)
  val port = s.getLocalPort
  s.close()
  val yaml =
    s"""
      |---
      |thrift:
      |- port: $port
      |  endpoints:
      |  - msg: "echoString"
      |    body: "gAEAAgAAAAplY2hvU3RyaW5nAAAAAAsAAAAAAAR0ZXN0AA=="
      |    latency:
      |      min: 10
      |      max: 100
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
      val client = Thrift.client.newIface[EchoService.FutureIface](s":$port")
      val response = Await.result(client.echoString("test"))
      response should be("test")
    }
  }
}
