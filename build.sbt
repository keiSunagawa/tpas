organization in ThisBuild := "me.kerfume"
version in ThisBuild := "0.2.0"

scalafmtOnCompile in ThisBuild := true

lazy val tpas = Tpas.tpas
lazy val root = (project in file("."))
  .aggregate(tpas)
