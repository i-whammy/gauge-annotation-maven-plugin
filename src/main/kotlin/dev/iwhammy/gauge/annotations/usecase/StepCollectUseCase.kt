package dev.iwhammy.gauge.annotations.usecase

import dev.iwhammy.gauge.annotations.MavenProjectConfig
import dev.iwhammy.gauge.annotations.domain.*

class StepCollectUseCase(
    private val outputPort: OutputPort,
    private val mavenProjectConfig: MavenProjectConfig,
    private val mavenRepositoryPathFactory: MavenRepositoryPathFactory,
    private val compileClasspathFactory: CompileClasspathFactory,
    private val gaugeAnnotationClassLoaderFactory: GaugeAnnotationClassLoaderFactory,
) {
    fun execute() {
        val mavenRepositoryPath = mavenRepositoryPathFactory.create(mavenProjectConfig.mavenRepositoryPath)
        val compileClasspaths = compileClasspathFactory.create(mavenProjectConfig.compileClasspaths)
        val classNames = compileClasspaths.collectClassNamesInPath()
        val report = mavenRepositoryPath
            .let { gaugeAnnotationClassLoaderFactory.create(compileClasspaths, it) }
            .collectProjectAnnotations(classNames)
            .let { Report.of(it) }
        when (report) {
            is Report.GaugeUsageReport -> outputPort.output(report)
            else -> return
        }
    }
}