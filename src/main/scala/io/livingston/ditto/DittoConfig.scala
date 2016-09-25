package io.livingston.ditto

import com.twitter.util.Try
import net.jcazevedo.moultingyaml._

trait DittoConfig {
  import ResponsesProtocol._

  private[this] val lines = scala.io.Source.fromFile("config.yml").mkString
  private[this] val responses =
    Try[Responses] {
      lines.parseYaml.convertTo[Responses]
    }.onFailure { case e: Exception =>
        println("Config is not in a valid format.")
        sys.exit()
    }.get

  val httpServers = responses.http.foldLeft(Map.empty[Int, Map[String, Endpoint]]) { (serverMap, server) =>
    serverMap + (server.port -> server.endpoints.foldLeft(Map.empty[String, Endpoint]) { (endpointMap, endpoint) =>
      endpointMap + (endpoint.uri -> endpoint)
    })
  }
}
