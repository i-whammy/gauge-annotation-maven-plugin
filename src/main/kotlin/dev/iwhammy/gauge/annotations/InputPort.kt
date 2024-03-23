package dev.iwhammy.gauge.annotations

import java.nio.file.Path

interface InputPort {
    fun collectMavenDependentJarPaths(mavenRepositoryPath: Path): List<Path>
}