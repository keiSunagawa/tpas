organization in ThisBuild := "me.kerfume"
version in ThisBuild := "0.1.0-SNAPSHOT-7"
scalafmtOnCompile in ThisBuild := true
//resolvers in ThisBuild += "kerfume-util repository" at "https://keisunagawa.github.io/kerfume-scala-util/repo/"

lazy val ypsctem = Core.ypsctem
lazy val root = (project in file("."))
  .aggregate(ypsctem)
