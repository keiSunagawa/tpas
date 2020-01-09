import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "example",
    libraryDependencies += scalaTest % Test
  )

lazy val applyTemplate = taskKey[Unit]("call interactive console > applyTemplate hello")

//testT := {
//  ClientSupport.runSctem(
//    dest = "me.kerfume.example.CallFromSbt",
//    template = "Test.tpl.scala",
//    valuesJson = """{"world": "hahaha!"}"""
//  ).value
//}