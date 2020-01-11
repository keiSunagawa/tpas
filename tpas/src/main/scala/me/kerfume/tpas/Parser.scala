package me.kerfume.tpas

import sbt.internal.util.complete.{Parser => SParser}

object Parser {
  import sbt.complete.DefaultParsers._

  val all: SParser[String] = (Space ~> any.*).string
}
