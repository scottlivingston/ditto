package io.livingston.ditto.thrift
import com.twitter.finagle.{ListeningServer, Thrift}
import com.twitter.util.Future

import scala.util.Random

class ThriftTestServer {
  var server: Option[ListeningServer] = None
  def start() = server = Option(Thrift.server.serveIface(":8081", new TestImpl()))
  def close() = server.map(_.close())
}

class TestImpl extends Test.FutureIface {

  override def test(): Future[Unit] = Future.Unit

  override def rand(): Future[Long] = Future {
    Random.nextLong()
  }

  override def randMax(max: Int): Future[Int] = Future {
    Random.nextInt(max)
  }
}
