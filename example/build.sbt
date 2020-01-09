import Dependencies._
import complete.DefaultParsers._
import me.kerfume.tpas.ClientSupport._

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


lazy val applyTemplate = inputKey[Unit]("call on interactive console > applyTemplate hello")

applyTemplate := Def.inputTaskDyn {
  val args: Seq[String] = spaceDelimited("<arg>").parsed
  Def.sequential{

    val z=  s"""{
               |  "bar": "${args.head}"
               |}""".stripMargin
    runTpas(
      dest = "com.example.FooImpl",
      template = "Foo.tpl.scala",
      valuesJson = z
    )
  }
}.evaluated