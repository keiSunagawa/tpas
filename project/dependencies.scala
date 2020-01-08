import sbt._

object Versions {
  lazy val scalaTest = "3.1.0"
  lazy val cats = "2.1.0"
  lazy val kerfumeUtil = "0.1.0"
}
object Dependencies {
  lazy val testDep = Seq("org.scalatest" %% "scalatest" % Versions.scalaTest)
  lazy val cats = Seq("org.typelevel" %% "cats-core" % Versions.cats)
  lazy val kerfumeUtil = Seq("me.kerfume" %% "kerfume-scala-util-core" % Versions.kerfumeUtil)
}
