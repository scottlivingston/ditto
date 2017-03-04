package io.livingston.ditto.scrooge

import nyaya.gen.Gen

object GenerationLanguageParser {

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

  def string(content: String = Content.alpha,
             length: Either[Int, (Int, Int)] = Right(8, 12),
             style: String = Style.lower): Gen[String] = {

    val g: Gen[Char] = content match {
      case Content.numeric => Gen.numeric
      case Content.alphaNumeric => Gen.alphaNumeric
      case Content.alpha => Gen.alpha
      case _ => Gen.alpha
    }
    val s = style match {
      case Style.upper => g.map(_.toUpper)
      case Style.firstUpper => g
      case Style.lower => g.map(_.toLower)
      case _ => g.map(_.toLower)
    }

    s.string {
      length match {
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

  def int(range: Either[String, (Int, Int)] = Left(Range.positive)): Gen[Int] = {
    numeric(range, Gen.positiveInt, Gen.negativeInt, Gen.chooseInt)
  }

  def short(range: Either[String, (Int, Int)] = Left(Range.positive)): Gen[Short] = {
    int(range).map(_.toShort)
  }

  def double(range: Either[String, (Double, Double)] = Left(Range.positive)): Gen[Double] = {
    numeric(range, Gen.positiveDouble, Gen.negativeDouble, Gen.chooseDouble)
  }

  def bool(content: String): Gen[Boolean] = {
    content match {
      case Content._true => Gen.boolean.map(_ => true)
      case Content._false => Gen.boolean.map(_ => false)
      case _ => Gen.boolean
    }
  }
}
