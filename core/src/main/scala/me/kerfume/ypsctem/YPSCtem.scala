package me.kerfume.ypsctem

import sbt._
import Keys._
import me.kerfume.ypsctem.internal.{Settings, Main}

object YPSCtem extends AutoPlugin {
  import complete.DefaultParsers._

  object autoImport {
    val sctemDefaultProject = settingKey[String]("default target project.")
    val sctemDefaultScope =
      settingKey[String]("default target scope. i.e. main or test")
    val sctemDefaultCodeType = settingKey[String]("default target code type.")
    val sctemTemplateDir =
      settingKey[String]("template file dir relative path.")

    val sctem = inputKey[Unit]("expand template code in target project.")
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] =
    Def.settings(
      (sctemDefaultProject in ThisBuild) := "root",
      (sctemDefaultScope in ThisBuild) := "main",
      (sctemDefaultCodeType in ThisBuild) := "scala",
      (sctemTemplateDir in ThisBuild) := "templates",
      sctem := {
        val args: Seq[String] = spaceDelimited("<arg>").parsed
        val s = state.value
        val sts = Settings(
          defaultProjectName = (sctemDefaultProject in ThisBuild).value,
          defaultScopeName = (sctemDefaultScope in ThisBuild).value,
          defaultCodeType = (sctemDefaultCodeType in ThisBuild).value,
          templateDir = (sctemTemplateDir in ThisBuild).value
        )

        Main
          .run(args, s, sts)
          .handleErrorWith { e =>
            cats.effect.IO {
              e.printStackTrace()
            }
          }
          .unsafeRunSync()
      }
    ) ++ super.projectSettings
}
