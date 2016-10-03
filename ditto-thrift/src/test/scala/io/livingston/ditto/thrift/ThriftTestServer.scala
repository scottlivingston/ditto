package io.livingston.ditto.thrift
import com.twitter.finagle.{ListeningServer, Thrift}
import com.twitter.util.Future

class ThriftTestServer {
  var server: Option[ListeningServer] = None
  def start() = server = Option(Thrift.server.serveIface(":8081", new TestImpl()))
  def close() = server.map(_.close())
}

class TestImpl extends EchoService.FutureIface {
  override def echoInt(i: Int): Future[Int] = Future(i)
  override def echoString(str: String): Future[String] = Future(str)
}
