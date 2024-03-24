package dev.iwhammy.gauge.annotations.driver

import dev.iwhammy.gauge.annotations.OutputPort
import org.apache.maven.plugin.logging.Log

class MavenLogOutputDriver(private val logger: Log) : OutputPort {
    override fun output(steps: List<String>) {
        steps.forEach { logger.info(it) }
    }
}