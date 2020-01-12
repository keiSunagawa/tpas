package me.kerfume.tpas.internal

import cats.effect.IO
import me.kerfume.tpas.internal.ArgsParser.Args
import me.kerfume.tpas.internal.`enum`.CodeType

object Main {
  def run(
      args: Map[String, String],
      state: sbt.State,
      settings: Settings
  ): IO[Unit] =
    IO.suspend {
      for {
        parsedArgs <- IO.fromEither { ArgsParser.parse(args, settings) }
        baseDir <- SbtModule.getProjectDir(parsedArgs.projectName, state)
        template <- FileModule.getTemplate(parsedArgs.templateName, settings)
        mergedJson = additionalCtxValues(parsedArgs)
        applied <- MustacheModule.applyTemplate(template, mergedJson)
        dest = parsedArgs.dest
        content = contentComplete(applied, dest, parsedArgs.codeType)
        _ <- FileModule.createFile(
          baseDir = baseDir.toPath,
          scope = parsedArgs.scope,
          codeType = parsedArgs.codeType,
          dest = dest,
          content = content
        )
      } yield ()
    }

  private def contentComplete(
      content: String,
      dest: Dest,
      codeType: CodeType
  ): String = {
    val delimiter = codeType match {
      case CodeType.Java => ";"
      case _             => ""
    }
    s"""package ${dest.packagePath.mkString(".")}${delimiter}
       |
       |${content}
       |""".stripMargin
  }

  import io.circe.Json
  private def additionalCtxValues(args: Args): Json = {
    val ctxJson = Json.obj(
      "ctx" -> Json.obj(
        "name" -> Json.fromString(args.dest.itemName),
        "project" -> Json.fromString(args.projectName)
      )
    )

    args.valuesJson.deepMerge(ctxJson)
  }
}
