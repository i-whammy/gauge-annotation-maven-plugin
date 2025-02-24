package dev.iwhammy.gauge.annotations.usecase

import dev.iwhammy.gauge.annotations.domain.CompileClasspath
import dev.iwhammy.gauge.annotations.domain.GaugeAnnotationClassLoader
import dev.iwhammy.gauge.annotations.domain.Report
import dev.iwhammy.gauge.annotations.domain.collectClassNamesInPath
import dev.iwhammy.gauge.annotations.usecase.port.OutputPort

class StepCollectUseCase(
    private val outputPort: OutputPort,
) {
    fun execute(
        compileClasspaths: List<CompileClasspath>,
        gaugeAnnotationClassLoader: GaugeAnnotationClassLoader,
    ) {
        val classNames = compileClasspaths.collectClassNamesInPath()
        val report = gaugeAnnotationClassLoader
            .collectProjectAnnotations(classNames)
            .let { Report.of(it) }
        when (report) {
            is Report.GaugeUsageReport -> outputPort.output(report)
            else -> return
        }
    }
}