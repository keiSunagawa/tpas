package me.kerfume.ypsctem

import sbt._
import Keys._

object KPlugin extends AutoPlugin {
  import complete.DefaultParsers._

  object autoImport {
    val sctem = inputKey[Unit]("expand template code in target project.")
  }

  import autoImport._

  //override def trigger = allRequirements

  override def trigger = allRequirements
  override def projectSettings: Seq[Def.Setting[_]] =
    super.projectSettings ++ Def.settings(
      sctem := {
        try {
          val args: Seq[String] = spaceDelimited("<arg>").parsed
          val parsedArgs = args.map { a =>
            val kv = a.split("=")
            kv(0) -> kv(1)
          }.toMap
          // '=' でsplitして各値にパース
          val pj = parsedArgs.getOrElse("prj", "root")
          val scope = parsedArgs.getOrElse("scp", "main")
          val codeType = parsedArgs.getOrElse("ctp", "scala")
          val dest = parsedArgs.getOrElse("dst", "com.example.Hello")
          val template = parsedArgs.getOrElse("tmp", "noop")
          val valuesJson = parsedArgs.getOrElse("val", "{}")
          println(valuesJson)

          val pkg = dest.split("\\.").init
          val fileNameBase = dest.split("\\.").last

          // target projectのディレクトリ取得
          val targetPj = Project
            .extract(state.value)
            .structure
            .allProjectPairs
            .find {
              case (_, ref) =>
                ref.project == pj
            }
            .map(_._1.base)
            .get // TODO unsafe

          // template に valuesJson を apply TODO
          val content =
            s"""package ${pkg.mkString(".")}
             |class ${fileNameBase} {}
             |""".stripMargin

          // package ディレクトリ作成(main/scalaのみ)
          val dir = s"${targetPj}/src/${scope}/${codeType}/${pkg.mkString("/")}"
          IO.createDirectory(new File(dir))

          // 作成したpackageディレクトリにtemplateにvalueを適用した.scalaファイルを設置
          IO.write(new File(s"${dir}/${fileNameBase}.scala"), content, IO.utf8)
        } catch {
          case e: Throwable =>
            e.printStackTrace()
        }

      }
//    commands += Command.args("sctemWith", "") {
//      case (s, args) =>
//        val ns = Project
//          .extract(s)
//          .appendWithSession(
//            Def.settings(
//              (sctmProject in ThisBuild) := args(0),
//              (sctmPackage in ThisBuild) := args(1)
//            ),
//            s
//          )
//        Project.extract(ns).runTask(sctem in ThisBuild, ns)
//        s
//    }
    )

  //override def projectSettings: Seq[Def.Setting[_]] =
}
