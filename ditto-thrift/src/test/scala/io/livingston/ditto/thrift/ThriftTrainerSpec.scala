package io.livingston.ditto.thrift

import java.net.ServerSocket

import com.twitter.finagle.Thrift
import com.twitter.util.Await
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}

class ThriftTrainerSpec extends WordSpec with Matchers with BeforeAndAfter {

  val ss = new ServerSocket(0)
  val ts = new ServerSocket(0)
  val serverPort = ss.getLocalPort
  val trainerPort = ts.getLocalPort
  ss.close()
  ts.close()

  val server = new ThriftTestServer(serverPort)
  val trainer = new ThriftTrainer(serverPort, trainerPort)
  val client = Thrift.client.withSessionPool.minSize(2).newIface[EchoService.FutureIface](s":$trainerPort")

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
      val one = Await.result(client.echoInt(1))
      val fourtyTwo = Await.result(client.echoInt(42))
      val test = Await.result(client.echoString("test"))
      val woohoo = Await.result(client.echoString("woohoo"))

      one should be(1)
      fourtyTwo should be(42)
      test should be("test")
      woohoo should be("woohoo")
      trainer.conf should not be("")
    }
  }
}
