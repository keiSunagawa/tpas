package me.kerfume.tpas

import sbt._

object ClientSupport {
  import Tpas.autoImport._

  def runTpas(
      dest: String,
      template: String,
      valuesJson: String
  ): Def.Initialize[Task[Unit]] = {
    val escapedJson = valuesJson.replace("\"", "\\\"")
    tpas.toTask(s""" dst=${dest} tmp=${template} "val=${escapedJson}"""")
  }
}
