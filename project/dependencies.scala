import sbt._

object Versions {
  lazy val scalaTest = "3.1.0"
  lazy val cats = "2.1.0"
  lazy val catsEffect = "2.0.0"
  lazy val mustache = "0.9.6"
  lazy val circe = "0.12.3"
}
object Dependencies {
  lazy val testDep = Seq("org.scalatest" %% "scalatest" % Versions.scalaTest)
  lazy val cats = Seq("org.typelevel" %% "cats-core" % Versions.cats)
  lazy val catsEffect = Seq("org.typelevel" %% "cats-effect" % Versions.catsEffect)
  lazy val mustache = Seq("com.github.spullara.mustache.java" % "compiler" % Versions.mustache)
  lazy val circe = Seq(
    "io.circe" %% "circe-core" % Versions.circe,
    "io.circe" %% "circe-generic" % Versions.circe,
    "io.circe" %% "circe-parser" % Versions.circe,
    "io.circe" %% "circe-literal" % Versions.circe
  )
}
