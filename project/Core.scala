import sbt._
import Keys._
import sbt.plugins.SbtPlugin
import Dependencies._

object Core {
  lazy val ypsctem = (project in file("core"))
    .enablePlugins(SbtPlugin)
    .settings(
      name := "ypsctem",
      sbtPlugin     := true,
      scalacOptions ++= Base.commonScalaOptions,
      libraryDependencies ++= Base.commonLibs ++ mustache ++ circe ++ catsEffect
    )
}
