package io.livingston.ditto

import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.thrift.Protocols
import com.twitter.finagle.{Http, Service, Thrift}
import com.twitter.util.{Await, Future, Try}
import org.apache.thrift.transport.TMemoryInputTransport

object Ditto extends App with DittoConfig {

//  val http = httpServers.map { case (port, endpoints) =>
//    val service = new Service[Request, Response] {
//      def apply(request: Request): Future[Response] = {
//        endpoints.get(request.uri).map { e =>
//          val response = Response(request.version, Status(e.status))
//          response.contentString = e.body
//          Thread.sleep(e.latency.sleepTime)
//          Future.value(response)
//        }.getOrElse {
//          Future.value(Response(request.version, Status.BadRequest))
//        }
//      }
//    }
//    Http.serve(s":$port", service)
//  }

//  val thrift = thriftServers.map { case (port, endpoints) =>
//    val service = new Service[Array[Byte], Array[Byte]] {
//      def apply(request: Array[Byte]): Future[Array[Byte]] = {
//        val inputTransport = new TMemoryInputTransport(request)
//        val thriftRequest = Protocols.binaryFactory().getProtocol(inputTransport)
//        Try {
//          val msg = thriftRequest.readMessageBegin()
//          endpoints.get(msg.name).map { e =>
//            val response = Array.empty[Byte]
//            Thread.sleep(e.latency.sleepTime)
//            Future.value(response)
//          }.getOrElse {
//            Future.exception(new Exception("Invalid Request"))
//          }
//        }.getOrElse(Future.exception(new Exception("")))
//      }
//    }
//    Thrift.server.serve(s":$port", service)
//  }

  Await.ready(Future.never)
}
