package me.kerfume.ypsctem

import sbt._

object ClientSupport {
  import YPSCtem.autoImport._

  def runSctem(
      dest: String,
      template: String,
      valuesJson: String
  ): Def.Initialize[Task[Unit]] = {
    val escapedJson = valuesJson.replace("\"", "\\\"")
    sctem.toTask(s""" dst=${dest} tmp=${template} "val=${escapedJson}"""")
  }
}
