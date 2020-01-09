package me.kerfume.ypsctem.internal

import cats.effect.IO

object Main {
  def run(args: Seq[String], state: sbt.State): IO[Unit] = IO.suspend {
    for {
      parsedArgs <- IO.fromEither { ArgsParser.parse(args.toList) }
      baseDir <- SbtModule.getProjectDir(parsedArgs.projectName, state)
      template <- FileModule.getTemplate(parsedArgs.templateName)
      applied <- MustacheModule.applyTemplate(template, parsedArgs.valuesJson)
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
}
