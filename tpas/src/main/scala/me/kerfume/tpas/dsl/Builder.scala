package me.kerfume.tpas.dsl

import sbt.internal.util.complete.{Parser => SParser}

object Builder {
  import sbt._
  import me.kerfume.tpas.Tpas.autoImport._
  import me.kerfume.tpas.Parser

  private[dsl] def buildTask(
      taskName: String,
      params: Params
  )(settings: Seq[sbt.Def.Setting[_]]): Def.Setting[Task[Unit]] =
    sbt.TaskKey[Unit](taskName) := {
      val escapedJson = params.valuesJson.replace("\"", "\\\"") // TODO parserいじっていらなくする

      val s = Keys.state.value
      val ns = Project.extract(s).appendWithSession(settings, s)
      val (_, res) = Project
        .extract(ns)
        .runInputTask(
          tpas,
          s""" dst=${params.dest} tmp=${params.template} "val=${escapedJson}"""",
          ns
        )

      res
    }

  def buildInputTask(
      taskName: String,
      parser: SParser[Params]
  )(settings: Seq[Def.Setting[_]]): sbt.Setting[sbt.InputTask[Unit]] = {
    InputKey[Unit](taskName) := {
      val params: Params = parser.parsed

      val escapedJson = params.valuesJson.replace("\"", "\\\"") // TODO parserいじっていらなくする

      val s = Keys.state.value
      val ns = Project.extract(s).appendWithSession(settings, s)
      val (_, res) = Project
        .extract(ns)
        .runInputTask(
          tpas,
          s""" dst=${params.dest} tmp=${params.template} "val=${escapedJson}"""",
          ns
        )

      res
    }
  }
}
