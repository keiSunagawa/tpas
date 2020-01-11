package me.kerfume.tpas

package object dsl {
  def minimum(
      dest: String,
      template: String,
      valuesJson: String
  ): Params = Params(
    dest = dest,
    template = template,
    valuesJson = valuesJson,
    project = None,
    scope = None,
    codeType = None
  )

  def full(
      dest: String,
      template: String,
      valuesJson: String,
      project: String,
      scope: String,
      codeType: String
  ): Params = Params(
    dest = dest,
    template = template,
    valuesJson = valuesJson,
    project = Some(project),
    scope = Some(scope),
    codeType = Some(codeType)
  )

  implicit class BuilderWithParam(t: DefTask[DefTask.WithParams]) {
    def build: sbt.Def.Setting[sbt.Task[Unit]] = t.buildForTask
  }
  implicit class BuilderWithParser(t: DefTask[DefTask.WithParser]) {
    def build: sbt.Def.Setting[sbt.InputTask[Unit]] = t.buildForInputTask
  }

  def defTpasTask(taskName: String): DefTask[DefTask.Init] = DefTask(taskName)
}
