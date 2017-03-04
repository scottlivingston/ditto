package io.livingston.ditto.scrooge

import nyaya.gen.Gen

object GenerationLanguageParser {
  object ThriftFields {
    val string = "string"
    val byte = "byte"
    val int = "i32"
    val short = "i16"
    val double = "i64"
    val binary = "binary"
    val bool = "bool"
    val map = "map"
    val list = "list"
    val set = "set"
  }
  def apply(fieldType: String, required: String, ann: String): Gen[_] = {

    Gen.int.option
  }

  def parseString(ann: String) = ???
  def parseNumeric(ann: String) = ???
  def parseBool(ann: String) = ???
  def parseContainer(ann: String) = ???
}

object GeneratorFunctions {

  object Range {
    val positive = "positive"
    val negative = "negative"
  }

  object Content {
    val alpha = "alpha"
    val numeric = "numeric"
    val alphaNumeric = "alphaNumeric"
    val _true = "true"
    val _false = "false"
  }

  object Style {
    val firstUpper = "firstUpper"
    val upper = "upper"
    val lower = "lower"
  }

  sealed trait Constraint
  sealed trait PrimitiveConstraint extends Constraint
  case class StringConstraints(content: String = Content.alpha,
                               length: Either[Int, (Int, Int)] = Right(8, 12),
                               style: String = Style.lower) extends PrimitiveConstraint
  case class NumericConstraints[A: Numeric](range: Either[String, (A, A)] = Left(Range.positive)) extends PrimitiveConstraint
  case class BooleanConstraints(content: String) extends PrimitiveConstraint
  case class BinaryConstraints(size: Either[Int, (Int, Int)] = Right(8, 12)) extends PrimitiveConstraint

  sealed trait ContainerConstraint extends Constraint
  case class MapConstraints(size: Int, keyConstraints: Constraint, vConstraints: Constraint) extends ContainerConstraint
  case class ListConstraints(size: Int, vConstraints: Constraint) extends ContainerConstraint
  case class SetConstraints(size: Int, vConstraints: Constraint) extends ContainerConstraint

  def string(constraints: StringConstraints): Gen[String] = {
    val g: Gen[Char] = constraints.content match {
      case Content.numeric => Gen.numeric
      case Content.alphaNumeric => Gen.alphaNumeric
      case Content.alpha => Gen.alpha
      case _ => Gen.alpha
    }
    val s = constraints.style match {
      case Style.upper => g.map(_.toUpper)
      case Style.firstUpper => g
      case Style.lower => g.map(_.toLower)
      case _ => g.map(_.toLower)
    }

    s.string {
      constraints.length match {
        case Left(i) => i
        case Right((l, h)) => l to h
      }
    }
  }

  def byte(): Gen[Byte] = {
    Gen.byte
  }
  def binary(constraints: BinaryConstraints): Gen[Vector[Byte]] = {
    Gen.byte.vector{
      constraints.size match {
        case Left(i) => i
        case Right((l, h)) => l to h
      }
    }
  }

  private def numeric[A: Numeric](range: Either[String, (A, A)], pos: Gen[A], neg: Gen[A], choose: (A, A) => Gen[A]): Gen[A] = {
    range match {
      case Left(s) => s match {
        case Range.negative => neg
        case Range.positive => pos
        case _ => pos
      }
      case Right((min, max)) => choose(min, max)
    }
  }

  def int(constraints: NumericConstraints[Int]): Gen[Int] = {
    numeric(constraints.range, Gen.positiveInt, Gen.negativeInt, Gen.chooseInt)
  }

  def short(constraints: NumericConstraints[Int]): Gen[Short] = {
    int(constraints).map(_.toShort)
  }

  def double(constraints: NumericConstraints[Double]): Gen[Double] = {
    numeric(constraints.range, Gen.positiveDouble, Gen.negativeDouble, Gen.chooseDouble)
  }

  def bool(constraints: BooleanConstraints): Gen[Boolean] = {
    constraints.content match {
      case Content._true => Gen.boolean.map(_ => true)
      case Content._false => Gen.boolean.map(_ => false)
      case _ => Gen.boolean
    }
  }
}
