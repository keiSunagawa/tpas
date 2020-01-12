import {{ domainName }}._

abstract class {{ ctx.name }}[F[_]] {
  def findByKey(key: Key): F[List[{{ domainName }}]]
  def store(models: List[{{domainName}}]): F[Unit]
}