package dev.iwhammy.gauge.annotations.driver

import dev.iwhammy.gauge.annotations.domain.GaugeProjectUsage
import dev.iwhammy.gauge.annotations.usecase.OutputPort
import jakarta.json.Json
import jakarta.json.JsonObject
import java.nio.file.Path

class JsonStandardOutputDriver() : OutputPort {
    override fun output(gaugeProjectUsage: GaugeProjectUsage, destination: Path) {
        gaugeProjectUsage.steps().also { println(GaugeUsageOutput(it).toJson()) }
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
}