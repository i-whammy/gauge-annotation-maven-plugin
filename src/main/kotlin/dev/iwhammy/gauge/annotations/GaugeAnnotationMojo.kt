package dev.iwhammy.gauge.annotations

import dev.iwhammy.gauge.annotations.driver.FilesDriver
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import java.net.URLClassLoader
import java.nio.file.Path

@Mojo(name = "gauge-annotation")
class GaugeAnnotationMojo() : AbstractMojo() {

    @Parameter(property = "project")
    private lateinit var project: MavenProject

    private var filesDriver = FilesDriver()

    override fun execute() {
        val mavenDependentJarUrls = filesDriver.collectMavenDependentJarURLs()
        val classpathElements = project.compileClasspathElements.map { Path.of(it) }
        val loadedUrls = classpathElements.map { it.toUri().toURL() }.toTypedArray()
            .plus(mavenDependentJarUrls.toTypedArray())
        val urlClassLoader = URLClassLoader(loadedUrls)
        classpathElements.forEach { classpath -> filesDriver.collectStepValues(classpath, urlClassLoader) }
    }
}