package me.kerfume.tpas.internal

import me.kerfume.tpas.internal.enum._

case class Settings(
    defaultProjectName: String,
    defaultScope: Scope,
    defaultCodeType: CodeType,
    templateDir: String
)
