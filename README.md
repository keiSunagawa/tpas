## What is tpas?  
tpasはsbt project上で動かせるテンプレートエンジンwrapperと  
生成ルール記述DSLを提供するsbt pluginです  

## Get Started  
### add settings and template in your sbt project.  
- project/build.properties  
```
sbt.version=1.3.6
```

- project/plugins.sbt  
```sbt
resolvers += "tpas repo" at "https://keisunagawa.github.io/tpas/repo/"
addSbtPlugin("me.kerfume" % "tpas" % "0.1.7")
```

- templates/Hello.tpl.scala (or your scala code template.)  
```scala
class {{ ctx.name }} {
  def hello(): Unit = println("hello, {{ message }}.")
}
```

- build.sbt, add gen template dsl  
```sbt
enablePlugins(me.kerfume.tpas.Tpas)

import me.kerfume.tpas.dsl._
defTpasTask("genHello").setParser { arg =>
val json =
  s"""{
      |  "message": "${arg}"
      |}""".stripMargin
  minimum(
    dest = "tpas_example.hello.Hello",
    template = "Hello.tpl.scala",
    valuesJson = json
  )
}.build

```

### run define your template gen task  
```shell
$ sbt "genHello world"
$ cat src/main/scala/tpas_example/hello/Hello.scala
package tpas_example.hello

class Hello {
  def hello(): Unit = println("hello, .")
}
```

other example see [example](./example)
