package me.kerfume.tpas

import sbt._
import Keys._
import me.kerfume.tpas.internal.{Main, Settings}
import me.kerfume.tpas.internal.enum.{CodeType, Scope => DestScope}
import Parser._

object Tpas extends AutoPlugin {

  object autoImport {
    val tpasDefaultProject = settingKey[String]("default target project.")
    val tpasDefaultScope =
      settingKey[DestScope]("default target scope.")
    val tpasDefaultCodeType = settingKey[CodeType]("default target code type.")
    val tpasTemplateDir =
      settingKey[String]("template file dir relative path.")

    val tpas = inputKey[Unit]("expand template code in target project.")
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] =
    Def.settings(
      (tpasDefaultProject in ThisBuild) := "root",
      (tpasDefaultScope in ThisBuild) := DestScope.Main,
      (tpasDefaultCodeType in ThisBuild) := CodeType.Scala,
      (tpasTemplateDir in ThisBuild) := "templates",
      tpas := {
        val logger = streams.value.log
        val args: Map[String, String] = keyValues.fromInputKey.parsed.toMap
        val s = state.value
        val sts = Settings(
          defaultProjectName = (tpasDefaultProject in ThisBuild).value,
          defaultScope = (tpasDefaultScope in ThisBuild).value,
          defaultCodeType = (tpasDefaultCodeType in ThisBuild).value,
          templateDir = (tpasTemplateDir in ThisBuild).value
        )

        Main
          .run(args, s, sts)
          .handleErrorWith { e =>
            cats.effect.IO {
              logger.err(e.getMessage)
            }
          }
          .unsafeRunSync()
      }
    ) ++ super.projectSettings
}
