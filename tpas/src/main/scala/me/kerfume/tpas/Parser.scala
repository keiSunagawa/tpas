package me.kerfume.tpas

import sbt.internal.util.complete.{Parser => SParser}

object Parser {
  import sbt.complete.DefaultParsers._

  def constC(c: Char): SParser[Char] = SParser.charClass(_ == c, s"const ${c}")

  val alphanumSP = SParser.charClass(alphanum, "alphanum")

  val alphanumValue = alphanumSP.+.string

  val all: SParser[String] = (Space ~> any.*).string

  val value: SParser[String] = alphanumValue | StringEscapable | StringVerbatim

  /** e.g. a=b */
  val keyValue
      : SParser[(String, String)] = (alphanumValue ~ (constC('=') ~> value))

  /** e.g. pureKey=pureValue json="{ \"x\": \"y\"} }" ... */
  val keyValues: SParser[Seq[(String, String)]] =
    ((keyValue <~ Space.+).* ~ (keyValue <~ Space.*)).map {
      case (hs, t) => hs :+ t
    }

  implicit class EnrichSParser[X](val underlying: SParser[X]) extends AnyVal {
    def fromInputKey: SParser[X] = Space ~> underlying
  }
}
