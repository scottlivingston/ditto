package io.livingston.ditto.http

import java.net.ServerSocket

import com.twitter.finagle.Http
import com.twitter.finagle.http.{Method, Request}
import com.twitter.util.Await
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}

class HttpResponderSpec extends WordSpec with Matchers with BeforeAndAfter {
  val s = new ServerSocket(0)
  val port = s.getLocalPort
  s.close()
  val yaml =
    s"""
       |---
       |http:
       |- port: $port
       |  endpoints:
       |  - uri: "/get"
       |    method: "GET"
       |    status: 200
       |    body: "OK"
       |    latency:
       |      min: 10
       |      max: 50
    """.stripMargin

  val server = new HttpResponder()

  before {
    server.apply(yaml)
  }

  after {
    server.close()
  }

  "Http config" should {
    "respond correctly to http requests" in {
      val client = Http.newService(s":$port")
      val response = Await.result(client(Request(Method.Get, "/get")))
      response.statusCode should be(200)
      response.contentString should be("OK")
    }
  }
}
