package dev.iwhammy.gauge.annotations.driver

import dev.iwhammy.gauge.annotations.domain.Report
import dev.iwhammy.gauge.annotations.usecase.port.OutputPort
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class JsonStandardOutputDriver() : OutputPort {
    override fun output(gaugeUsageReport: Report.GaugeUsageReport) {
        gaugeUsageReport.usage.steps().also { println(GaugeUsageOutput(it).toJson()) }
    }

    @Serializable
    data class GaugeUsageOutput(val steps: List<String>)
}

fun JsonStandardOutputDriver.GaugeUsageOutput.toJson(): String {
    return Json.encodeToString(this)
}
