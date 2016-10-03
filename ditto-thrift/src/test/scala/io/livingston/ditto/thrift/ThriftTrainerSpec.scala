package io.livingston.ditto.thrift

import com.twitter.finagle.Thrift
import com.twitter.util.Await
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}

class ThriftTrainerSpec extends WordSpec with Matchers with BeforeAndAfter {

  val server = new ThriftTestServer()
  val trainer = new ThriftTrainer()
  val client = Thrift.client.withSessionPool.maxSize(2).newIface[EchoService.FutureIface](":8080")

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
      Await.result(client.echoInt(1))
      Await.result(client.echoInt(42))
      Await.result(client.echoString("test"))
      Await.result(client.echoString("woohoo"))
      println(trainer.conf)

      trainer.conf should be("")
    }
  }
}
