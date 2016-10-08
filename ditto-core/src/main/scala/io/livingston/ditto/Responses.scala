package io.livingston.ditto

trait Responses

trait ServerConfig{
  val port: Int
  val endpoints: List[Endpoint]
}

trait Endpoint

case class Latency(min: Int = 0, max: Int = 0) {
  import scala.util.Random
  def sleepTime: Long = {
    Random.nextInt(max + 1 - min) + min
  }
}
