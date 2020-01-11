package me.kerfume.tpas.internal

import cats.effect.IO
import me.kerfume.tpas.internal.ArgsParser.Args

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
        content = contentComplete(applied, dest)
        _ <- FileModule.createFile(
          baseDir = baseDir.toPath,
          scope = parsedArgs.scope,
          codeType = parsedArgs.codeType,
          dest = dest,
          content = content
        )
      } yield ()
    }

  private def contentComplete(content: String, dest: Dest): String = {
    s"""package ${dest.packagePath.mkString(".")}
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
