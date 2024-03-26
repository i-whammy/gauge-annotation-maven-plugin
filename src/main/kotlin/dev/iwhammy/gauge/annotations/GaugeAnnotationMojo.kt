package dev.iwhammy.gauge.annotations

import dev.iwhammy.gauge.annotations.domain.CompileClasspathFactory
import dev.iwhammy.gauge.annotations.domain.GaugeAnnotationClassLoaderFactory
import dev.iwhammy.gauge.annotations.domain.MavenRepositoryPathFactory
import dev.iwhammy.gauge.annotations.driver.MarkDownOutDriver
import dev.iwhammy.gauge.annotations.usecase.StepCollectUseCase
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

    private val mavenRepositoryPathFactory: MavenRepositoryPathFactory = MavenRepositoryPathFactory()
    private val compileClasspathFactory: CompileClasspathFactory = CompileClasspathFactory()
    private val gaugeAnnotationClassLoaderFactory: GaugeAnnotationClassLoaderFactory =
        GaugeAnnotationClassLoaderFactory()

    override fun execute() {
        val mavenProjectConfig = MavenProjectConfig.also {
            it.mavenRepositoryPath = mavenRepositoryPath
            it.compileClasspathElements = project.compileClasspathElements
        }
        StepCollectUseCase(
            MarkDownOutDriver(project.basedir.toPath().resolve("steps.md")),
            mavenProjectConfig,
            mavenRepositoryPathFactory,
            compileClasspathFactory,
            gaugeAnnotationClassLoaderFactory
        ).execute()
    }
}

object MavenProjectConfig {
    lateinit var mavenRepositoryPath: String
    lateinit var compileClasspathElements: List<String>
}