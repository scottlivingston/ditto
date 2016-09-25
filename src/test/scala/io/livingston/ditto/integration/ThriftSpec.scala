package io.livingston.ditto.integration

import com.twitter.finagle.Thrift
import com.twitter.util.{Await, Future}
import org.scalatest.{Matchers, WordSpec}

class ThriftSpec extends WordSpec with Matchers {
  val port = 8081


  "Thrift config" should {
    "respond correctly to thrift requests" in {
      val client = Thrift.client.newIface[Test[Future]](":port")
      Await.result(client.test())
    }
  }
}
