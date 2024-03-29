package dev.iwhammy.gauge.annotations.domain

import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream

class MavenRepositoryPathFactory {
    fun create(path: String): MavenRepositoryPath {
        return runCatching { Path.of(path) }
            .fold(
                { p: Path -> MavenRepositoryPath(p) },
                { e: Throwable -> throw MavenRepositoryPathCreation(e) }
            )
    }
}

data class MavenRepositoryPath(private val path: Path) {
    fun mavenDependentJarUrls(): List<URL> {
        return runCatching { Files.walk(this.path) }
            .fold(
                { stream: Stream<Path> ->
                    stream
                        .filter { it.toString().endsWith("jar") }
                        .map { it.toUri().toURL() }.toList()
                },
                { e: Throwable ->
                    throw RuntimeException(e)
                }
            )
    }
}

class MavenRepositoryPathCreation(e: Throwable) : Exception()