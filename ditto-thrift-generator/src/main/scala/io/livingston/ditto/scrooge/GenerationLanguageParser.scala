package io.livingston.ditto.scrooge

import nyaya.gen.{Gen, GenSize}

object GenerationLanguageParser {

  def string(genString: String): Primitive[String] = {

    ???
  }

  case class StartsWith(s: String) {
    def unapply(arg: String): Option[String] = if (arg.startsWith(s)) Some(arg) else None
  }
  val length = StartsWith("length")
  val content = StartsWith("content")
  val style = StartsWith("style")
  val range = StartsWith("range")

  def parse(cs: List[Constraint], s: List[String]): List[Constraint] = {
    s match {
      case current :: remaining => current match {
        case length(_) => parse(cs :+ Length(current), remaining)
        case content(_) => parse(cs :+ Content(current), remaining)
        case style(_) => parse(cs :+ Style(current), remaining)
        case range(_) => parse(cs :+ Range(current), remaining)
      }

    }
  }

  def apply[T](gen: String): Primitive[T] = {
    val params = gen.split("\\s+")

    val c = Gen.alpha //content
    val s = c.map(_.toUpper)
    val l = s.string(5) //length
    l.withSeed(1)

    s.withSeed(1).samples(GenSize(1)).next()

    Gen.alpha.string(4).map(_.toUpperCase)
    Gen.boolean.withSeed(1).samples()
    ???
  }
}

sealed trait ContentType
sealed trait StringContentType extends ContentType
case object AlphaNumeric extends StringContentType
case object Alpha extends StringContentType
case object Numeric extends StringContentType

sealed trait BooleanContentType extends ContentType
case object True extends BooleanContentType
case object False extends BooleanContentType

sealed trait StyleType
case object FirstUpper extends StyleType
case object Upper extends StyleType
case object Lower extends StyleType

sealed trait RangeType
case object Positive extends RangeType
case object Negative extends RangeType


sealed trait Constraint
case class Length(min: Long, max: Long) extends Constraint with RangeType
case class Content(t: ContentType) extends Constraint
case class Style(t: StyleType) extends Constraint
case class Range(t: RangeType) extends Constraint

object Length {
  def apply(arg: String): Length = {
    if (arg.matches("length=\\d+-\\d+")) {
      val sub = arg.substring(7).split("-")
      Length(sub(0).toLong, sub(1).toLong)
    } else {
      Length(Long.MinValue, Long.MaxValue)
    }
  }
}
object Content {
  def apply(arg: String): Content = {
    if (arg.matches("content=(alpha|numeric|alphanumeric|true|false)")) {
      arg.substring(8)
      Content(sub(0).toLong, sub(1).toLong)
    } else {
      Content(Long.MinValue, Long.MaxValue)
    }
  }
}
object Style {
  def apply(arg: String): Style = {
    if (arg.matches("length=\\d+-\\d+")) {
      val sub = arg.substring(7).split("-")
      Style(sub(0).toLong, sub(1).toLong)
    } else {
      Style(Long.MinValue, Long.MaxValue)
    }
  }
}
object Range {
  def apply(arg: String): Range = {
    if (arg.matches("length=\\d+-\\d+")) {
      val sub = arg.substring(7).split("-")
      Range(sub(0).toLong, sub(1).toLong)
    } else {
      Range(Long.MinValue, Long.MaxValue)
    }
  }
}



case class Primitive[T](f: Gen[T]) {
  def apply(seed: Long): T = f.withSeed(seed).samples(GenSize(1)).next()
}
