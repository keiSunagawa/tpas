package me.kerfume.ypsctem.internal

case class Dest(
    packagePath: List[String],
    itemName: String
)

object Dest {
  def apply(destStr: String): Dest = {
    val xs = destStr.split("\\.").toList
    new Dest(
      xs.init,
      xs.last
    )
  }
}
