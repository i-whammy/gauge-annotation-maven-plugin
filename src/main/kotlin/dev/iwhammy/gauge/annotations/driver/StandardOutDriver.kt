package dev.iwhammy.gauge.annotations.driver

import dev.iwhammy.gauge.annotations.domain.GaugeUsage
import dev.iwhammy.gauge.annotations.usecase.OutputPort
import jakarta.json.Json
import jakarta.json.JsonObject
import java.nio.file.Path

class StandardOutDriver() : OutputPort {
    override fun output(gaugeUsages: List<GaugeUsage>, destination: Path) {
        gaugeUsages.forEach { println(it.toOutput().toJson()) }
    }

    data class GaugeUsageOutput(val steps: List<String>) {
        fun toJson(): JsonObject {
            val arrayBuilder = Json.createArrayBuilder()
            steps.forEach {
                arrayBuilder.add(it)
            }

            return Json.createObjectBuilder()
                .add("steps", arrayBuilder.build())
                .build()
        }
    }

    private fun GaugeUsage.toOutput() =
        this.gaugeUsedMethods.map { it.entries.flatMap { it.annotationValues } }.flatten().let(::GaugeUsageOutput)
}