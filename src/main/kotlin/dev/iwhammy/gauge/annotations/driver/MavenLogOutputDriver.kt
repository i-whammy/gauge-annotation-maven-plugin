package dev.iwhammy.gauge.annotations.driver

import dev.iwhammy.gauge.annotations.domain.GaugeUsage
import dev.iwhammy.gauge.annotations.usecase.OutputPort
import org.apache.maven.plugin.logging.Log
import java.nio.file.Path

class MavenLogOutputDriver(private val logger: Log) : OutputPort {
    override fun output(gaugeUsages: List<GaugeUsage>, destination: Path) {
        gaugeUsages.forEach { usage ->
            usage.gaugeUsedMethods.flatMap {method ->
                method.entries.map { it.annotationValues.joinToString(",") }
            }.forEach { logger.info(it) }
        }
    }
}