package dev.iwhammy.gauge.annotations.driver

import dev.iwhammy.gauge.annotations.domain.Report
import dev.iwhammy.gauge.annotations.usecase.port.OutputPort
import jakarta.json.Json
import jakarta.json.JsonObject

class JsonStandardOutputDriver() : OutputPort {
    override fun output(gaugeUsageReport: Report.GaugeUsageReport) {
        gaugeUsageReport.usage.steps().also { println(GaugeUsageOutput(it).toJson()) }
    }

    data class GaugeUsageOutput(val steps: List<String>)
}

fun JsonStandardOutputDriver.GaugeUsageOutput.toJson(): JsonObject {
    val arrayBuilder = Json.createArrayBuilder()
    steps.forEach {
        arrayBuilder.add(it)
    }

    return Json.createObjectBuilder()
        .add("steps", arrayBuilder.build())
        .build()
}
