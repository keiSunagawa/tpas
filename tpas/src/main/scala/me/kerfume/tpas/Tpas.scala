package me.kerfume.tpas

import sbt._
import Keys._
import me.kerfume.tpas.internal.{Settings, Main}

object Tpas extends AutoPlugin {
  import complete.DefaultParsers._

  object autoImport {
    val tpasDefaultProject = settingKey[String]("default target project.")
    val tpasDefaultScope =
      settingKey[String]("default target scope. i.e. main or test")
    val tpasDefaultCodeType = settingKey[String]("default target code type.")
    val tpasTemplateDir =
      settingKey[String]("template file dir relative path.")

    val tpas = inputKey[Unit]("expand template code in target project.")
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] =
    Def.settings(
      (tpasDefaultProject in ThisBuild) := "root",
      (tpasDefaultScope in ThisBuild) := "main",
      (tpasDefaultCodeType in ThisBuild) := "scala",
      (tpasTemplateDir in ThisBuild) := "templates",
      tpas := {
        val args: Seq[String] = spaceDelimited("<arg>").parsed
        val s = state.value
        val sts = Settings(
          defaultProjectName = (tpasDefaultProject in ThisBuild).value,
          defaultScopeName = (tpasDefaultScope in ThisBuild).value,
          defaultCodeType = (tpasDefaultCodeType in ThisBuild).value,
          templateDir = (tpasTemplateDir in ThisBuild).value
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
