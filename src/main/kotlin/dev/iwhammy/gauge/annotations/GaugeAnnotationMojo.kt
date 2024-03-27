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
import java.nio.file.Path

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
        val mavenProjectConfig = MavenProjectConfig.of(mavenRepositoryPath, project)
        StepCollectUseCase(
            MarkDownOutDriver(mavenProjectConfig.basedir.resolve("steps.md")),
            mavenProjectConfig,
            mavenRepositoryPathFactory,
            compileClasspathFactory,
            gaugeAnnotationClassLoaderFactory
        ).execute()
    }
}

class MavenProjectConfig private constructor(
    val mavenRepositoryPath: String,
    val compileClasspaths: List<String>,
    val basedir: Path,
) {
    companion object {
        fun of(mavenRepositoryPath: String, project: MavenProject): MavenProjectConfig {
            return MavenProjectConfig(mavenRepositoryPath, project.compileClasspathElements, project.basedir.toPath())
        }
    }
}