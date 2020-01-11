package me.kerfume.tpas.dsl

import DefTask._

case class DefTask[S <: State](
    private val taskName: String,
    private val params: Option[Params],
    private val willParse: Option[String => Params],
    private val settings: Seq[sbt.Def.Setting[_]]
) {
  def setParser(
      parse: String => Params
  )(implicit ev: S =:= Init): DefTask[WithParser] = {
    copy[WithParser](willParse = Some(parse))
  }
  def setParams(
      params: Params
  )(implicit ev: S =:= Init): DefTask[WithParams] = {
    copy[WithParams](params = Some(params))
  }

  def setSettings(ss: Seq[sbt.Def.Setting[_]]): DefTask[S] = {
    copy[S](settings = ss)
  }

  def buildForTask(
      implicit ev: S =:= WithParams
  ): sbt.Def.Setting[sbt.Task[Unit]] = {
    val ps = params.get

    Builder.buildTask(taskName, ps)(settings)
  }

  def buildForInputTask(
      implicit ev: S =:= WithParser
  ): sbt.Def.Setting[sbt.InputTask[Unit]] = {
    val parser = willParse.get
    Builder.buildInputTask(taskName, parser)(settings)
  }
}

object DefTask {
  sealed trait State
  trait Init extends State
  trait WithParams extends State
  trait WithParser extends State

  def apply(taskName: String): DefTask[Init] =
    new DefTask[Init](taskName, None, None, Nil)
}

case class Params(
    dest: String,
    template: String,
    valuesJson: String,
    project: Option[String],
    scope: Option[String],
    codeType: Option[String]
)

object Builder {
  import sbt._
  import me.kerfume.tpas.Tpas.autoImport._
  import complete.DefaultParsers.any

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
      parse: String => Params
  )(settings: Seq[Def.Setting[_]]): sbt.Setting[sbt.InputTask[Unit]] = {
    InputKey[Unit](taskName) := {
      val arg: String = any.*.parsed.tail.mkString
      val params = parse(arg)

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
