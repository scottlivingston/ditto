package io.livingston.ditto

import com.twitter.util.Try
import com.typesafe.config.ConfigFactory
import io.livingston.ditto.responses.http.HttpEndpoint
import io.livingston.ditto.responses.thrift.ThriftEndpoint
import net.jcazevedo.moultingyaml._

trait DittoConfig {

  private[this] val rootConfig = ConfigFactory.load()
  private[this] val dittoConfig = rootConfig.getConfig("ditto")
  private[this] val configFile = dittoConfig.getString("config-file")



  private[this] val lines = scala.io.Source.fromFile(configFile).mkString

  lines.split("---").tail.foreach{s =>
    val doc = s.trim
    //make this a map of protocol to config yaml
    s
  }
  sys.exit()
//  private[this] val responses =
//    Try[Responses] {
//      lines.parseYaml.convertTo[Responses]
//    }.onFailure { case e: Exception =>
//        println("Config is not in a valid format.")
//        sys.exit()
//    }.get
//
//  val httpServers = for {
//    servers <- responses.http.toSeq
//    endpoints <- servers.foldLeft(Map.empty[Int, Map[String, HttpEndpoint]]) { (serverMap, server) =>
//      serverMap + (server.port -> server.endpoints.foldLeft(Map.empty[String, HttpEndpoint]) { (endpointMap, endpoint) =>
//        endpointMap + (endpoint.uri -> endpoint)
//      })
//    }
//  } yield endpoints
//
////  val httpServers = responses.http.foldLeft(Map.empty[Int, Map[String, HttpEndpoint]]) { (serverMap, server) =>
////    serverMap + (server.port -> server.endpoints.foldLeft(Map.empty[String, HttpEndpoint]) { (endpointMap, endpoint) =>
////      endpointMap + (endpoint.uri -> endpoint)
////    })
////  }
//
//  val thriftServers = for {
//    servers <- responses.thrift.toSeq
//    endpoints <- servers.foldLeft(Map.empty[Int, Map[String, ThriftEndpoint]]) { (serverMap, server) =>
//      serverMap + (server.port -> server.endpoints.foldLeft(Map.empty[String, ThriftEndpoint]) { (endpointMap, endpoint) =>
//        endpointMap + (endpoint.msg -> endpoint)
//      })
//    }
//  } yield endpoints
//
////  val thriftServers = responses.http.foldLeft(Map.empty[Int, Map[String, HttpEndpoint]]) { (serverMap, server) =>
////    serverMap + (server.port -> server.endpoints.foldLeft(Map.empty[String, HttpEndpoint]) { (endpointMap, endpoint) =>
////      endpointMap + (endpoint.uri -> endpoint)
////    })
////  }
}
