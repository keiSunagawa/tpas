package me.kerfume.tpas

import sbt.internal.util.complete.{Parser => SParser}

object Parser {
  import sbt.complete.DefaultParsers._

  def constC(c: Char) = SParser.charClass(_ == c, s"const ${c}")
  val alphanumSP = SParser.charClass(alphanum, "alphanum")
  val alphanumValue = alphanumSP.+.string
  val all: SParser[String] = (Space ~> any.*).string
  val dquoteValue: SParser[String] =
    (DQuoteClass ~> (NotDQuoteSpaceClass | (BackslashChar ~> DQuoteClass)).* <~ DQuoteClass).string
  val values: SParser[String] = alphanumValue | dquoteValue
  val keyValue
      : SParser[(String, String)] = (alphanumValue ~ (constC('=') ~> values))
  val keyValues: SParser[Seq[(String, String)]] =
    ((keyValue <~ Space.+).* ~ keyValue).map { case (hs, t) => hs :+ t }
}
