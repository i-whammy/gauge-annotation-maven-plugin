package dev.iwhammy.gauge.annotations.driver

import dev.iwhammy.gauge.annotations.domain.Report
import dev.iwhammy.gauge.annotations.usecase.port.OutputPort

class MarkdownStandardOutputDriver : OutputPort {
    override fun output(gaugeUsageReport: Report.GaugeUsageReport) {
        val steps = gaugeUsageReport.usage.steps()
        println("# Gauge Annotations")
        println()
        println("## Steps")
        println()
        steps.forEach { step ->
            println("- `$step`")
        }
    }
}
