package me.kerfume.tpas.internal

import java.io.File
import java.nio.file.{Files, Path, Paths}

import sbt.{IO => sIO}
import cats.effect.IO

object FileModule {
  def getTemplate(templateName: String, settings: Settings): IO[String] = {
    val templatePath = Paths.get(s"./${settings.templateDir}/${templateName}")

    for {
      isExists <- IO { Files.exists(templatePath) }
      template <- if (isExists) IO { sIO.read(templatePath.toFile, sIO.utf8) } else
        IO.raiseError(TemplateNotFound(templateName))
    } yield template
  }

  def createFile(
      baseDir: Path,
      scope: String,
      codeType: String,
      dest: Dest,
      content: String
  ): IO[Unit] = {
    val dir =
      s"${baseDir}/src/${scope}/${codeType}/${dest.packagePath.mkString("/")}"
    for {
      _ <- IO { sIO.createDirectory(new File(dir)) }
      _ <- IO {
        sIO.write(new File(s"${dir}/${dest.itemName}.scala"), content, sIO.utf8)
      }
    } yield ()
  }

  case class TemplateNotFound(templatetName: String)
      extends RuntimeException(
        s"template not found. name: ${templatetName}"
      )
}
