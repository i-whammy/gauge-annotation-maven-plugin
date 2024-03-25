package dev.iwhammy.gauge.annotations.usecase

import dev.iwhammy.gauge.annotations.MavenProjectConfig
import dev.iwhammy.gauge.annotations.domain.CompileClasspathFactory
import dev.iwhammy.gauge.annotations.domain.GaugeAnnotationClassLoaderFactory
import dev.iwhammy.gauge.annotations.domain.MavenRepositoryPathFactory
import dev.iwhammy.gauge.annotations.domain.collectClassNamesInPath

class StepCollectUseCase(
    private val outputPort: OutputPort,
    private val mavenProjectConfig: MavenProjectConfig,
    private val mavenRepositoryPathFactory: MavenRepositoryPathFactory,
    private val compileClasspathFactory: CompileClasspathFactory,
    private val gaugeAnnotationClassLoaderFactory: GaugeAnnotationClassLoaderFactory,
) {
    fun execute() {
        val compileClasspathElements = mavenProjectConfig.compileClasspathElements
        val mavenRepositoryPath = mavenRepositoryPathFactory.get(mavenProjectConfig.mavenRepositoryPath)
        val compileClasspaths = compileClasspathFactory.get(compileClasspathElements)
        val classNames = compileClasspaths.collectClassNamesInPath()
        val steps = mavenRepositoryPath
            .let { gaugeAnnotationClassLoaderFactory.get(compileClasspaths, it) }
            .collectAnnotationValues(classNames)
        outputPort.output(steps)
    }
}