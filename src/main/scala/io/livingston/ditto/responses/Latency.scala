package io.livingston.ditto.responses

case class Latency(min: Int, max: Int) {
  import scala.util.Random
  def sleepTime: Long = {
    Random.nextInt(max + 1 - min) + min
  }
}

trait ServerConfig{
  val port: Int
  val endpoints: List[Endpoint]
}

trait Endpoint
