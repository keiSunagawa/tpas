package me.kerfume.ypsctem.internal

import cats.effect.IO
import me.kerfume.ypsctem.internal.ArgsParser.Args

object Main {
  def run(args: Seq[String], state: sbt.State, settings: Settings): IO[Unit] =
    IO.suspend {
      for {
        parsedArgs <- IO.fromEither { ArgsParser.parse(args.toList, settings) }
        baseDir <- SbtModule.getProjectDir(parsedArgs.projectName, state)
        template <- FileModule.getTemplate(parsedArgs.templateName, settings)
        mergedJson = additionalEnvValues(parsedArgs)
        applied <- MustacheModule.applyTemplate(template, mergedJson)
        dest = parsedArgs.dest
        content = contentComplete(applied, dest)
        _ <- FileModule.createFile(
          baseDir = baseDir.toPath,
          scope = parsedArgs.scopeName,
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
  private def additionalEnvValues(args: Args): Json = {
    val envJson = Json.obj(
      "env" -> Json.obj(
        "name" -> Json.fromString(args.dest.itemName),
        "project" -> Json.fromString(args.projectName)
      )
    )

    args.valuesJson.deepMerge(envJson)
  }
}
