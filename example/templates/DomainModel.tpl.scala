import {{ ctx.name }}._

case class {{ ctx.name }}(
  key: Key
)

object {{ ctx.name }} {
  type Key = {{ keyType }}
}