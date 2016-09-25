package io.livingston.ditto

import com.twitter.util.Try
import com.typesafe.config.{Config, ConfigFactory}
import io.livingston.ditto.responses.Responses
import io.livingston.ditto.responses.http.HttpEndpoint
import net.jcazevedo.moultingyaml._

trait DittoConfig {
  import io.livingston.ditto.responses.ResponsesProtocol._

  private[this] val rootConfig = ConfigFactory.load()
  private[this] val dittoConfig = rootConfig.getConfig("ditto")
  val configFile = dittoConfig.getString("config-file")

  private[this] val lines = scala.io.Source.fromFile(configFile).mkString
  private[this] val responses =
    Try[Responses] {
      lines.parseYaml.convertTo[Responses]
    }.onFailure { case e: Exception =>
        println("Config is not in a valid format.")
        sys.exit()
    }.get

  val httpServers = responses.http.foldLeft(Map.empty[Int, Map[String, HttpEndpoint]]) { (serverMap, server) =>
    serverMap + (server.port -> server.endpoints.foldLeft(Map.empty[String, HttpEndpoint]) { (endpointMap, endpoint) =>
      endpointMap + (endpoint.uri -> endpoint)
    })
  }

  val thriftServers = responses.http.foldLeft(Map.empty[Int, Map[String, HttpEndpoint]]) { (serverMap, server) =>
    serverMap + (server.port -> server.endpoints.foldLeft(Map.empty[String, HttpEndpoint]) { (endpointMap, endpoint) =>
      endpointMap + (endpoint.uri -> endpoint)
    })
  }
}
