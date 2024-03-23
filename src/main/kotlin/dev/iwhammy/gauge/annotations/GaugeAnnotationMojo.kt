package dev.iwhammy.gauge.annotations

import dev.iwhammy.gauge.annotations.driver.FilesDriver
import dev.iwhammy.gauge.annotations.driver.MavenLogOutputDriver
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

    private val mavenRepositoryPath = Path.of("${System.getProperty("user.home")}/.m2/repository")

    private var filesDriver = FilesDriver(log)

    private var outputPort = MavenLogOutputDriver(log)

    override fun execute() {
        val mavenDependentJarUrls = filesDriver.collectMavenDependentJarPaths(mavenRepositoryPath)
        val classpathElements = project.compileClasspathElements.map { Path.of(it) }
        val loadedUrls = classpathElements.plus(mavenDependentJarUrls).map { it.toUri().toURL() }.toTypedArray()
        val urlClassLoader = URLClassLoader(loadedUrls)
        filesDriver.collectStepValues(classpathElements, urlClassLoader)
            .run { outputPort.output(this) }
    }
}