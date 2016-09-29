package io.livingston.ditto

import com.twitter.app.LoadService
import com.typesafe.scalalogging.LazyLogging
import net.jcazevedo.moultingyaml._

import scala.util.Try
import scala.util.control.NoStackTrace

trait Responder extends LazyLogging {
  val protocol: String
  def announce(): Unit
  def announce(port: Int, endpoints: Seq[String]): Unit = {
    logger.info(s"\n$protocol:\n  port: $port\n${endpoints.map("    "+_).mkString("\n")}")
  }
  def apply(yaml: String): Unit

  def parse[T <: Responses](yaml: String, c: YamlValue => T)(f: T => Unit): Unit = {
    Try[T] {
      c(yaml.parseYaml)
    }.map { r =>
      f(r)
      announce()
    }.recover{ case e: Exception => logger.info(e.getMessage) }
  }
}

class ResponderNotFoundException(protocol: String) extends Exception with NoStackTrace {
  override def getMessage: String = {
    s"No Responder found for protocol: $protocol"
  }
}

class DuplicateRespondersException(responders: Map[String, Seq[Responder]]) extends Exception with NoStackTrace {
  override def getMessage: String = {
    val msgs = responders.map { case (protocol, r) =>
      s"$protocol=(${r.map(_.getClass.getName).mkString(", ")})"
    }.mkString(" ")
    s"Duplicate Responders fount: $msgs"
  }
}

abstract class BaseResponder(f: () => Seq[Responder]) extends LazyLogging {
  private[this] lazy val responders: Map[String, Responder] = {
    val responders = f()

    val duplicates = responders
      .groupBy(_.protocol)
      .filter { case (_, rs) => rs.size > 1 }

    if (duplicates.nonEmpty) throw new DuplicateRespondersException(duplicates)

    responders.foldLeft(Map.empty[String, Responder]) { (acc, r) =>
      logger.info(s"Responder[${r.protocol}] loaded.")
      acc + (r.protocol -> r)
    }
  }

  def load(protocol: String, yaml: String): Unit = {
    responders.get(protocol) match {
      case Some(responder) => responder(yaml)
      case None => throw new ResponderNotFoundException(protocol)
    }
  }
}

object LoadResponder extends BaseResponder(() => LoadService[Responder]())
