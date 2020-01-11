package me.kerfume.tpas.internal

import io.circe._
import JsonUtil._
import enum._

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
      scope <- args.get("scp") match {
        case Some(scp) =>
          if (scp == Scope.Main.value) Right(Scope.Main)
          else if (scp == Scope.Test.value) Right(Scope.Test)
          else Left(InvalidScopeValue(scp))
        case None =>
          Right(settings.defaultScope)
      }
      codeType <- args.get("ctp") match {
        case Some(ctp) =>
          if (ctp == CodeType.Scala.value) Right(CodeType.Scala)
          else if (ctp.startsWith(CodeType.ScalaWithVersion.prefix) && (ctp.size > CodeType.Scala.value.size)) {
            val version = ctp.drop(CodeType.ScalaWithVersion.prefix.size)
            Right(CodeType.ScalaWithVersion(version))
          } else if (ctp == CodeType.Java.value) Right(CodeType.Java)
          else Left(InvalidCodeTypeValue(ctp))
        case None => Right(settings.defaultCodeType)
      }
    } yield {
      Args(
        projectName = projectName,
        scope = scope,
        codeType = codeType,
        _dest = dest,
        templateName = templateName,
        valuesJson = valuesJson
      )
    }
  }

  case class Args(
      projectName: String,
      scope: Scope,
      codeType: CodeType,
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

  case class InvalidScopeValue(invalid: String)
      extends ParsError(
        s"invalid scope. invalid value: ${invalid}"
      )

  case class InvalidCodeTypeValue(invalid: String)
      extends ParsError(
        s"invalid code type. invalid value: ${invalid}"
      )
}
