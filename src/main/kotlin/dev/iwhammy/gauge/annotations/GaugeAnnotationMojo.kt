package dev.iwhammy.gauge.annotations

import dev.iwhammy.gauge.annotations.domain.CompileClasspath
import dev.iwhammy.gauge.annotations.driver.JsonStandardOutputDriver
import dev.iwhammy.gauge.annotations.usecase.StepCollectUseCase
import dev.iwhammy.gauge.annotations.usecase.port.OutputPort
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject

@Mojo(name = "gauge-annotation", defaultPhase = LifecyclePhase.PACKAGE)
class GaugeAnnotationMojo() : AbstractMojo() {

    @Parameter(property = "project")
    private lateinit var project: MavenProject

    private val mavenRepositoryPath = "${System.getProperty("user.home")}/.m2/repository"

    private val outputPort: OutputPort = JsonStandardOutputDriver()

    override fun execute() {
        val compileClasspaths = project.compileClasspathElements.map { CompileClasspath(it) }
        val parentClasspaths =
            if (project.parent != null) project.parent.compileClasspathElements.map { CompileClasspath(it) } else emptyList()
        val targetClasspaths = compileClasspaths.plus(parentClasspaths)
        StepCollectUseCase(
            outputPort,
        ).execute(
            targetClasspaths,
            GaugeAnnotationClassLoaderFactory.create(
                targetClasspaths,
                MavenRepositoryPathFactory.create(mavenRepositoryPath)
            )
        )
    }
}
