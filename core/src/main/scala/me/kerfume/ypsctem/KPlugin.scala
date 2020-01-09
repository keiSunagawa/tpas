package me.kerfume.ypsctem

import sbt._
import Keys._

object KPlugin extends AutoPlugin {
  import complete.DefaultParsers._

  object autoImport {
    val sctem = inputKey[Unit]("expand template code in target project.")
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] =
    super.projectSettings ++ Def.settings(sctem := {
      val args: Seq[String] = spaceDelimited("<arg>").parsed
      val s = state.value
      me.kerfume.ypsctem.internal.Main
        .run(args, s)
        .handleErrorWith { e =>
          cats.effect.IO {
            e.printStackTrace()
          }
        }
        .unsafeRunSync()
    })
}
