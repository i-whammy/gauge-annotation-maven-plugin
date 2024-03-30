package dev.iwhammy.gauge.annotations.driver

import dev.iwhammy.gauge.annotations.domain.GaugeProjectUsage
import dev.iwhammy.gauge.annotations.usecase.OutputPort
import org.apache.maven.plugin.logging.Log
import java.nio.file.Path

class MavenLogOutputDriver(private val logger: Log) : OutputPort {
    override fun output(gaugeProjectUsage: GaugeProjectUsage, destination: Path) {
        gaugeProjectUsage.steps().forEach { logger.info(it) }
    }
}
