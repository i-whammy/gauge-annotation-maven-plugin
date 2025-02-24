package dev.iwhammy.gauge.annotations

import dev.iwhammy.gauge.annotations.domain.*
import java.net.URLClassLoader
import java.nio.file.Path

object MavenRepositoryPathFactory {
    fun create(path: String): MavenRepositoryPath {
        return runCatching { Path.of(path) }
            .fold(
                { p: Path -> MavenRepositoryPath(p) },
                { e: Throwable -> throw MavenRepositoryPathCreation(e) }
            )
    }
}

object GaugeAnnotationClassLoaderFactory {
    fun create(
        compileClasspaths: List<CompileClasspath>,
        mavenRepositoryPath: MavenRepositoryPath
    ): GaugeAnnotationClassLoader {
        return compileClasspaths.map { it.urlOf() }
            .plus(mavenRepositoryPath.mavenDependentJarUrls())
            .toTypedArray()
            .let { GaugeAnnotationClassLoader(URLClassLoader(it)) }
    }
}