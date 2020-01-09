import sbt._
import Keys._
import sbt.plugins.SbtPlugin
import Dependencies._

object Tpas {
  lazy val tpas = (project in file("tpas"))
    .enablePlugins(SbtPlugin)
    .settings(
      name := "ypsctem",
      sbtPlugin     := true,
      scalacOptions ++= Base.commonScalaOptions,
      libraryDependencies ++= Base.commonLibs ++ mustache ++ circe ++ catsEffect
    )
}