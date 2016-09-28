package io.livingston.ditto.core

import com.twitter.app.LoadService
import com.typesafe.scalalogging.LazyLogging

import scala.util.control.NoStackTrace

trait Responder {
  val protocol: String
  val endpoints: Seq[String]
  def apply(yaml: String): Unit
}

class DuplicateRespondersException(responders: Map[String, Seq[Responder]]) extends Exception with NoStackTrace {
  override def getMessage = {
    val msgs = responders.map { case (protocol, r) =>
      "%s=(%s)".format(protocol, r.map(_.getClass.getName).mkString(", "))
    }.mkString(" ")
    "Duplicate Responders fount: %s".format(msgs)
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
      logger.info("Reponder[%s] responding to: %s".format(r.protocol, r.endpoints.mkString(", ")))
      acc + (r.protocol -> r)
    }
  }

  def load(yaml: String): Unit = {
    val protocol = yaml
    responders.get(protocol) match {
      case Some(responder) => responder("")
      case None => throw new Exception("")
    }
  }
}

object LoadResponder extends BaseResponder(() => LoadService[Responder]())
