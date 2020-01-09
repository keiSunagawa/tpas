package me.kerfume.ypsctem.internal

import cats.syntax.traverse._
import cats.instances.list._
import cats.instances.either._
import io.circe._
import JsonUtil._

object ArgsParser {
  def parse(args: List[String]): Either[ParsError, Args] = {
    for {
      parsedArgs <- args
        .traverse { a =>
          val kv = a.split("=").filter(_.nonEmpty)
          Either.cond(kv.size == 2, kv(0) -> kv(1), InvalidArgFormat(a))
        }
        .map(_.toMap)
      projectName = parsedArgs.getOrElse("prj", "root")
      scope = parsedArgs.getOrElse("scp", "main")
      codeType = parsedArgs.getOrElse("ctp", "scala")
      dest <- parsedArgs.get("dst").toRight(DestRequire())
      templateName <- parsedArgs.get("tmp").toRight(TemplateRequire())
      valuesJson <- parsedArgs.get("val") match {
        case None => Right(emptyJson)
        case Some(jsonStr) =>
          parser.parse(jsonStr).left.map { ValuesJsonParseError }
      }
    } yield {
      Args(
        projectName = projectName,
        scopeName = scope,
        codeType = codeType,
        _dest = dest,
        templateName = templateName,
        valuesJson = valuesJson
      )
    }
  }

  case class Args(
      projectName: String,
      scopeName: String,
      codeType: String,
      _dest: String,
      templateName: String,
      valuesJson: Json
  ) {
    def dest: Dest = Dest(_dest)
  }

  sealed abstract class ParsError(errorMsg: String, cause: Throwable = null)
      extends RuntimeException(errorMsg, cause)
  case class InvalidArgFormat(invalid: String)
      extends ParsError(
        s"invalid arg format. valid format is key=value. invalid value: ${invalid}"
      )
  case class DestRequire()
      extends ParsError(
        s"dest param is required! e.g. dst=com.example.Hello"
      )
  case class TemplateRequire()
      extends ParsError(
        s"template param is required! e.g. tmp=HelloWorld.tpl.scala"
      )
  case class ValuesJsonParseError(cause: ParsingFailure)
      extends ParsError(s"invalid val param. json decode error.", cause)
}
