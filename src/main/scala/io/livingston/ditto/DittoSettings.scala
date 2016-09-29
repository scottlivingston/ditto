package io.livingston.ditto

import com.typesafe.config.ConfigFactory

trait DittoSettings {

  private[this] val rootConfig = ConfigFactory.load()
  private[this] val dittoConfig = rootConfig.getConfig("ditto")
  private[this] val configFile = dittoConfig.getString("config-file")
  private[this] val lines = scala.io.Source.fromFile(configFile).mkString
  type Protocol = String
  type Yaml = String

  val yamlConfigs: Map[Protocol, Yaml] = lines.split("---").tail.map { s =>
    s.trim.split(":").head -> ("---\n" + s.trim)
  }.toMap
}
