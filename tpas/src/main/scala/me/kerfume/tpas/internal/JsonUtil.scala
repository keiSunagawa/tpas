package me.kerfume.tpas.internal

import io.circe._
import collection.JavaConverters._

object JsonUtil {
  val emptyJson = Json.obj()

  def toJavaMap(json: Json): Any = {
    // actual return java.util.Map[String, Any]
    json.foldWith(javaMapFolder)
  }

  private val javaMapFolder = new Json.Folder[Any] {
    def onNull: Any = null
    def onBoolean(value: Boolean): Any = value
    def onNumber(value: JsonNumber): Any = value.toLong.get
    def onString(value: String): Any = value
    def onArray(value: Vector[Json]): Any = value.map(_.foldWith(this))
    def onObject(value: JsonObject): Any =
      value.toIterable
        .map { case (k, v) => k -> v.foldWith(this) }
        .toMap
        .asJava
  }
}
