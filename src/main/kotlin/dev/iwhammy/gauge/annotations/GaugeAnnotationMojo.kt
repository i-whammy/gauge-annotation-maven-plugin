package dev.iwhammy.gauge.annotations

import dev.iwhammy.gauge.annotations.domain.CompileClasspathFactory
import dev.iwhammy.gauge.annotations.domain.GaugeAnnotationClassLoaderFactory
import dev.iwhammy.gauge.annotations.domain.MavenRepositoryPathFactory
import dev.iwhammy.gauge.annotations.driver.StandardOutDriver
import dev.iwhammy.gauge.annotations.usecase.StepCollectUseCase
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject

@Mojo(name = "gauge-annotation")
class GaugeAnnotationMojo() : AbstractMojo() {

    @Parameter(property = "project")
    private lateinit var project: MavenProject

    private val mavenRepositoryPath = "${System.getProperty("user.home")}/.m2/repository"

    private val mavenRepositoryPathFactory: MavenRepositoryPathFactory = MavenRepositoryPathFactory()
    private val compileClasspathFactory: CompileClasspathFactory = CompileClasspathFactory()
    private val gaugeAnnotationClassLoaderFactory: GaugeAnnotationClassLoaderFactory =
        GaugeAnnotationClassLoaderFactory()
    private val outputPort = StandardOutDriver()

    override fun execute() {
        val mavenProjectConfig = MavenProjectConfig.also {
            it.mavenRepositoryPath = mavenRepositoryPath
            it.compileClasspathElements = project.compileClasspathElements
        }
        StepCollectUseCase(
            outputPort,
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