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
        val args: Seq[String] = spaceDelimited("<arg>").parsed
        // '=' でsplitして各値にパース
        val pj = args(0)
        val pkg = args(1)
        val template = args(2)
        val valuesJson = args(3)


        // target projectのディレクトリ取得
        val targetPj = Project.extract(state.value).structure.allProjectPairs.find {case (_, ref) =>
          ref.project == pj
        }.map(_._1.base)

        // template に valuesJson を apply

        // package ディレクトリ作成(main/scalaのみ)

        // 作成したpackageディレクトリにtemplateにvalueを適用した.scalaファイルを設置

        println(args)
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
