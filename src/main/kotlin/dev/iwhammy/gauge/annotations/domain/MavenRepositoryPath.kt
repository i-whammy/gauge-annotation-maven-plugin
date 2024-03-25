package dev.iwhammy.gauge.annotations.domain

import java.net.URL
import java.nio.file.Files
import java.nio.file.Path

class MavenRepositoryPathFactory {
    fun get(path: String) = MavenRepositoryPath(Path.of(path))
}

data class MavenRepositoryPath(val path: Path) {
    fun mavenDependentJarUrls(): List<URL> {
        return Files.walk(this.path).filter { it.toString().endsWith("jar") }.map { it.toUri().toURL() }.toList()
    }
}
