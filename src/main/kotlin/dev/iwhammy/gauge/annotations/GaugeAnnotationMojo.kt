package dev.iwhammy.gauge.annotations

import dev.iwhammy.gauge.annotations.domain.*
import dev.iwhammy.gauge.annotations.driver.JsonStandardOutputDriver
import dev.iwhammy.gauge.annotations.usecase.StepCollectUseCase
import dev.iwhammy.gauge.annotations.usecase.port.OutputPort
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import java.net.URLClassLoader

@Mojo(name = "gauge-annotation", defaultPhase = LifecyclePhase.PACKAGE)
class GaugeAnnotationMojo() : AbstractMojo() {

    @Parameter(property = "project")
    private lateinit var project: MavenProject

    private val mavenRepositoryPath = "${System.getProperty("user.home")}/.m2/repository"

    private val mavenRepositoryPathFactory: MavenRepositoryPathFactory = MavenRepositoryPathFactory()
    private val gaugeAnnotationClassLoaderFactory: GaugeAnnotationClassLoaderFactory =
        GaugeAnnotationClassLoaderFactory()
    private val outputPort: OutputPort = JsonStandardOutputDriver()

    override fun execute() {
        val compileClasspaths = project.compileClasspathElements.map { CompileClasspath(it) }
        StepCollectUseCase(
            outputPort,
            compileClasspaths,
            gaugeAnnotationClassLoaderFactory.create(compileClasspaths,
                mavenRepositoryPathFactory.create(mavenRepositoryPath))
        ).execute()
    }
}
