package io.livingston.ditto

import com.twitter.util.{Await, Future}
import com.typesafe.scalalogging.LazyLogging

object Ditto extends App with DittoSettings with LazyLogging {

  yamlConfigs.foreach { case (protocol, yaml) =>
    LoadResponder.load(protocol, yaml)
  }

  Await.ready(Future.never)
}
