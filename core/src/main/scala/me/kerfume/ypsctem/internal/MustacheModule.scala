package me.kerfume.ypsctem.internal

import java.io.{StringReader, StringWriter}

import cats.effect.IO
import com.github.mustachejava.DefaultMustacheFactory

object MustacheModule {
  def applyTemplate(template: String, values: io.circe.Json): IO[String] = {
    for {
      writer <- IO { new StringWriter() }
      mf <- IO { new DefaultMustacheFactory() }
      m <- IO { mf.compile(new StringReader(template), "scalaFile") }
      valuesMap = JsonUtil.toJavaMap(values)
      _ <- IO { m.execute(writer, valuesMap) }
    } yield writer.toString
  }
}
