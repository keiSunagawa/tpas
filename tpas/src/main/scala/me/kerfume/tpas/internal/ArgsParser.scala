package me.kerfume.tpas.internal

import io.circe._
import JsonUtil._

object ArgsParser {
  def parse(
      args: Map[String, String],
      settings: Settings
  ): Either[ParsError, Args] = {
    for {
      dest <- args.get("dst").toRight(DestRequire())
      templateName <- args.get("tmp").toRight(TemplateRequire())
      valuesJson <- args.get("val") match {
        case None => Right(emptyJson)
        case Some(jsonStr) =>
          parser.parse(jsonStr).left.map { ValuesJsonParseError }
      }
      projectName = args.getOrElse("prj", settings.defaultProjectName)
      scope = args.getOrElse("scp", settings.defaultScopeName)
      codeType = args.getOrElse("ctp", settings.defaultCodeType)
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
