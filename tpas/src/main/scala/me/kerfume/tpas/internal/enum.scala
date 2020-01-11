package me.kerfume.tpas.internal

object enum {
  sealed trait CodeType {
    def value: String
  }
  object CodeType {
    sealed trait ScalaCode
    case object Scala extends CodeType with ScalaCode {
      val value = "scala"
    }
    case class ScalaWithVersion(version: String)
        extends CodeType
        with ScalaCode {
      val value = s"${ScalaWithVersion.prefix}${version}"
    }
    object ScalaWithVersion {
      val prefix = s"${Scala.value}-"
    }
    case object Java extends CodeType {
      val value = "java"
    }
  }

  sealed trait Scope {
    def value: String
  }
  object Scope {
    case object Main extends Scope {
      val value = "main"
    }
    case object Test extends Scope {
      val value = "test"
    }
  }
}
