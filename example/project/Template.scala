import me.kerfume.tpas.dsl._
import me.kerfume.tpas.Tpas.autoImport._
import sbt._
import sbt.complete.DefaultParsers._
import me.kerfume.tpas.Parser._

object Template {
  lazy val domainGenSettings = Def.settings(
    ThisBuild / tpasDefaultProject := "domain"
  )

  lazy val genTasks = Def.settings(
    /** > genSimple hoge */
    defTpasTask("genSimple").setParser { arg =>
      val json =
        s"""{
           |  "bar": "${arg}"
           |}""".stripMargin
      minimum(
        dest = "tpas_example.FooImpl",
        template = "Foo.tpl.scala",
        valuesJson = json
      )
    }.build,
    /** > genDomainModel group="authentication.user" name=User keyType=Long */
    defTpasTask("genDomainModel").setSettings(domainGenSettings).setParser {
      keyValues.flatMap {
        ps =>
          val psMap = ps.toMap

          val params = for {
            group <- psMap.get("group").toRight("""group required."""")
            name <- psMap.get("name").toRight("""name required.""")
            keyType <- psMap.get("keyType").toRight("""keyType required.""")
          } yield {
            val dest = s"tpas_example.domain.${group}.${name}"
            val json =
              s"""{
             |  "keyType": "${keyType}"
             |}""".stripMargin

            minimum(
              dest = dest,
              template = "DomainModel.tpl.scala",
              valuesJson = json
            )
          }

          params.fold(
            e => failure(e),
            ps => success(ps)
          )
      }
    }.build,
    /** > genRepository group="authentication.user" domainName=User */
    defTpasTask("genRepository").setSettings(domainGenSettings).setParser {
      keyValues.flatMap {
        ps =>
          val psMap = ps.toMap

          val params = for {
            group <- psMap.get("group").toRight("""group required."""")
            dname <- psMap.get("domainName").toRight("""domainName required.""")
          } yield {
            val dest = s"tpas_example.domain.${group}.${dname}Repository"
            val json =
              s"""{
               |  "domainName": "${dname}"
               |}""".stripMargin

            minimum(
              dest = dest,
              template = "DomainModelRepository.tpl.scala",
              valuesJson = json
            )
          }

          params.fold(
            e => failure(e),
            ps => success(ps)
          )
      }
    }.build,
    /** > genDomain group="authentication.user" name=User keyType=Long */
    Keys.commands += Command.make("genDomain") { state =>
      keyValues.fromInputKey.flatMap {
        ps =>
          val psMap = ps.toMap

          val result = for {
            group <- psMap.get("group").toRight("""group required."""")
            name <- psMap.get("name").toRight("""name required.""")
            keyType <- psMap.get("keyType").toRight("""keyType required.""")
          } yield {
            s"""genDomainModel group="${group}" name="${name}" keyType="${keyType}"""" ::
              s"""genRepository group="${group}" domainName="${name}"""" ::
              state
          }

          result.fold(
            e => failure(e),
            ns => success(() => ns)
          )
      }
    }
  )
}
