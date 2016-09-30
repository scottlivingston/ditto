package io.livingston.ditto.thrift

import com.twitter.finagle.Thrift
import com.twitter.util.Await
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}

class ThriftTrainerSpec extends WordSpec with Matchers with BeforeAndAfter {

  val server = new ThriftTestServer()
  val trainer = new ThriftTrainer()
  val client = Thrift.client.newIface[Test.FutureIface](":8080")

  before {
    server.start()
    trainer.start()
  }

  after {
    server.close()
    trainer.close()
  }

  "ThriftTrainer" should {
    "proxy requests" in {
      Await.result(client.rand())
      Await.result(client.randMax(10))
      println(trainer.conf)

      trainer.conf should not be ""
    }
  }
}
