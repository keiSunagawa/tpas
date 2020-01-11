import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

enablePlugins(Tpas)

lazy val root = (project in file("."))
  .settings(
    name := "example",
    libraryDependencies += scalaTest % Test
  )

import me.kerfume.tpas.dsl._

defTpasTask("tpasTest").setParser { arg =>
  val json =
    s"""{
       |  "bar": "${arg}"
       |}""".stripMargin
  minimum(
    dest = "com.example.FooImpl",
    template = "Foo.tpl.scala",
    valuesJson = json
  )
}.build

InputKey[Unit]("t") := {
  import sbt.complete.DefaultParsers._
  val a = (Space ~ any.*).parsed
  println(a)
}