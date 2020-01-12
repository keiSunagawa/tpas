package me.kerfume.tpas

import me.kerfume.tpas.internal.enum._

package object dsl {
  def minimum(
      dest: String,
      template: String,
      valuesJson: String
  ): Params = Params(
    dest = dest,
    template = template,
    valuesJson = valuesJson,
    project = None,
    scope = None,
    codeType = None
  )

  def full(
      dest: String,
      template: String,
      valuesJson: String,
      project: Option[String],
      scope: Option[Scope],
      codeType: Option[CodeType]
  ): Params = Params(
    dest = dest,
    template = template,
    valuesJson = valuesJson,
    project = project,
    scope = scope,
    codeType = codeType
  )

  val mainScope = Scope.Main
  val testScope = Scope.Test

  val scalaCode = CodeType.Scala
  def scalaCode(version: String) = CodeType.ScalaWithVersion(version)
  val scalaCode_213 = scalaCode("2.13")
  val scalaCode_212 = scalaCode("2.12")
  val javaCode = CodeType.Java

  implicit class BuilderWithParam(t: DefTask[DefTask.WithConst]) {
    def build: sbt.Def.Setting[sbt.Task[Unit]] = t.buildForTask
  }
  implicit class BuilderWithFunctionParser(
      t: DefTask[DefTask.WithFunctionParser]
  ) {
    def build: sbt.Def.Setting[sbt.InputTask[Unit]] =
      t.buildForInputTaskWithFParser
  }
  implicit class BuilderWithParser(t: DefTask[DefTask.WithParser]) {
    def build: sbt.Def.Setting[sbt.InputTask[Unit]] =
      t.buildForInputTaskWithSParser
  }

  def defTpasTask(taskName: String): DefTask[DefTask.Init] = DefTask(taskName)
}
