package me.kerfume.tpas.dsl

import DefTask._
import me.kerfume.tpas.Parser
import sbt.internal.util.complete.{Parser => SParser}
import me.kerfume.tpas.internal.enum._

case class DefTask[S <: State](
    private val taskName: String,
    private val paramsGen: S#ParamGen,
    private val settings: Seq[sbt.Def.Setting[_]],
    private val updateParams: Params => Params = identity
) {
  def setParser(
      parser: String => Params
  )(implicit ev: S =:= Init): DefTask[WithFunctionParser] = {
    copy[WithFunctionParser](paramsGen = parser)
  }
  def setParser(
      params: SParser[Params]
  )(implicit ev: S =:= Init): DefTask[WithParser] = {
    copy[WithParser](paramsGen = params)
  }

  def setParams(
      params: Params
  )(implicit ev: S =:= Init): DefTask[WithConst] = {
    copy[WithConst](paramsGen = params)
  }

  def setSettings(ss: Seq[sbt.Def.Setting[_]]): DefTask[S] = {
    copy[S](settings = ss)
  }

  def setProject(
      projectName: String
  ): DefTask[S] = {
    copy[S](
      updateParams = updateParams.compose(
        _.copy(
          project = Some(projectName)
        )
      )
    )
  }

  def setScope(
      scope: Scope
  ): DefTask[S] = {
    copy[S](
      updateParams = updateParams.compose(
        _.copy(
          scope = Some(scope)
        )
      )
    )
  }

  def setCodeType(
      codeType: CodeType
  ): DefTask[S] = {
    copy[S](
      updateParams = updateParams.compose(
        _.copy(
          codeType = Some(codeType)
        )
      )
    )
  }

  def buildForTask(
      implicit ev: S =:= WithConst,
      ev2: S#ParamGen =:= Params
  ): sbt.Def.Setting[sbt.Task[Unit]] = {
    val completeParams = updateParams(ev2(paramsGen))
    Builder.buildTask(taskName, completeParams)(settings)
  }

  def buildForInputTaskWithFParser(
      implicit ev: S =:= WithFunctionParser,
      ev2: =:=[S#ParamGen, String => Params]
  ): sbt.Def.Setting[sbt.InputTask[Unit]] = {
    val parser = Parser.all.map(ev2(paramsGen))
    Builder.buildInputTask(taskName, parser, updateParams)(settings)
  }
  def buildForInputTaskWithSParser(
      implicit ev: S =:= WithParser,
      ev2: =:=[S#ParamGen, SParser[Params]]
  ): sbt.Def.Setting[sbt.InputTask[Unit]] = {
    val parser = sbt.complete.DefaultParsers.Space ~> (ev2(paramsGen))
    Builder.buildInputTask(taskName, parser, updateParams)(settings)
  }
}

object DefTask {
  sealed trait State {
    type ParamGen
  }
  trait Init extends State {
    type ParamGen = Unit
  }
  final abstract class WithConst extends State {
    override type ParamGen = Params
  }
  final abstract class WithFunctionParser extends State {
    override type ParamGen = String => Params
  }
  final abstract class WithParser extends State {
    override type ParamGen = SParser[Params]
  }

  def apply(taskName: String): DefTask[Init] =
    new DefTask[Init](taskName, (), Nil)
}

case class Params(
    dest: String,
    template: String,
    valuesJson: String,
    project: Option[String],
    scope: Option[Scope],
    codeType: Option[CodeType]
)
