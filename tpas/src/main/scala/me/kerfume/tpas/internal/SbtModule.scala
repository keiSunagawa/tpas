package me.kerfume.tpas.internal

import cats.effect.IO
import sbt.Project

object SbtModule {
  def getProjectDir(projectName: String, state: sbt.State): IO[java.io.File] =
    IO.suspend {
      val projectDirOpt = Project
        .extract(state)
        .structure
        .allProjectPairs
        .find {
          case (_, ref) =>
            ref.project == projectName
        }
        .map { case (res, _) => res.base }
      IO.fromEither { projectDirOpt.toRight(ProjectNotFound(projectName)) }
    }

  case class ProjectNotFound(projectName: String)
      extends RuntimeException(
        s"target project not found. name: ${projectName}"
      )
}
