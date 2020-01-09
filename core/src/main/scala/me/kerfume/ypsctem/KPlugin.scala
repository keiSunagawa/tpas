package me.kerfume.ypsctem

import java.io.{StringReader, StringWriter}

import sbt._
import Keys._
import _root_.io.circe._
import com.github.mustachejava.{DefaultMustacheFactory}

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
          val valuesJson = parsedArgs
            .getOrElse("val", """{"name": "Hello", "fooStr": "hello!" }""")
          println(valuesJson)

          import collection.JavaConverters._
          val folder = new _root_.io.circe.Json.Folder[Any] {
            def onNull: Any = null
            def onBoolean(value: Boolean): Any = value
            def onNumber(value: JsonNumber): Any = value.toLong.get
            def onString(value: String): Any = value
            def onArray(value: Vector[Json]): Any = value.map(_.foldWith(this))
            def onObject(value: JsonObject): Any =
              value.toIterable
                .map { case (k, v) => k -> v.foldWith(this) }
                .toMap
                .asJava
          }
          val values =
            parser.parse(valuesJson).toOption.get.foldWith(folder)
          println(values)

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
          //val out = new OutputStreamWriter()
          val mf = new DefaultMustacheFactory()
          val m = mf.compile(
            new StringReader(
              s"""class {{ name }} {
                |  def foo: String = "{{ fooStr }}"
                |}
               |""".stripMargin
            ),
            "scalaFile"
          )

          val sw = new StringWriter()
          m.execute(sw, values)

          val out = sw.toString

          val content =
            s"""package ${pkg.mkString(".")}
             |
             |${out}
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
}
