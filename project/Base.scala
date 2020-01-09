import sbt._
import Keys._
import Dependencies._

object Base {
  val commonLibs = cats ++ testDep ++ mustache ++ circe

  val commonScalaOptions =
    Seq(
      "-deprecation",
      "-encoding",
      "utf-8",
      "-feature",
      "-Xlint",
      "-unchecked",
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard",
      "-Ywarn-unused",
      "-Ywarn-unused:-implicits",
      "-language:higherKinds",
      "-Ypatmat-exhaust-depth",
      "off"
    )

  lazy val strictScalacOptions = Seq(
    scalacOptions ++= Seq(
      "-Xfatal-warnings"
    )
  )
}
