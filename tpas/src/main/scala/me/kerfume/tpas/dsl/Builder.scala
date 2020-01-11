package me.kerfume.tpas.dsl

import sbt.internal.util.complete.{Parser => SParser}

object Builder {
  import sbt._
  import me.kerfume.tpas.Tpas.autoImport._

  private def paramsToString(params: Params): String = {
    def inDQuotes(str: String): String = "\"" ++ str ++ "\""
    def inVDQuotes(str: String): String = "\"\"\"" ++ str ++ "\"\"\""

    val dstS = s"dst=${inDQuotes(params.dest)}"
    val tmpS = s"tmp=${inDQuotes(params.template)}"
    val valS = s"val=${inVDQuotes(params.valuesJson)}"

    val optionalArgs = List(
      params.project.fold("") { s =>
        s"prj=${inDQuotes(s)}"
      },
      params.scope.fold("") { s =>
        s"scp=${inDQuotes(s)}"
      },
      params.codeType.fold("") { s =>
        s"ctp=${inDQuotes(s)}"
      }
    )

    val useArgs = optionalArgs.filter(_.nonEmpty)

    val useArgsS = if (useArgs.isEmpty) "" else " " ++ useArgs.mkString(" ")

    (s" ${dstS} ${tmpS} ${valS}" ++ useArgsS)
      .replaceAll("\r\n|\r|\n", "")
  }
  private def runInternalTask(
      params: Params,
      s: State,
      settings: Seq[sbt.Def.Setting[_]]
  ): Unit = {

    val ns = Project.extract(s).appendWithSession(settings, s)
    val args = paramsToString(params)

    Project
      .extract(ns)
      .runInputTask(
        tpas,
        args,
        ns
      )
    ()
  }
  private[dsl] def buildTask(
      taskName: String,
      params: Params
  )(settings: Seq[sbt.Def.Setting[_]]): Def.Setting[Task[Unit]] =
    sbt.TaskKey[Unit](taskName) := {

      val s = Keys.state.value
      runInternalTask(params, s, settings)
    }

  def buildInputTask(
      taskName: String,
      parser: SParser[Params],
      updateParams: Params => Params
  )(settings: Seq[Def.Setting[_]]): sbt.Setting[sbt.InputTask[Unit]] = {
    InputKey[Unit](taskName) := {
      val params: Params = parser.parsed
      val completeParams = updateParams(params)

      val s = Keys.state.value
      runInternalTask(completeParams, s, settings)
    }
  }
}
