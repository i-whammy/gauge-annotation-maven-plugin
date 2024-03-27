package dev.iwhammy.gauge.annotations.driver

import dev.iwhammy.gauge.annotations.usecase.OutputPort
import org.apache.maven.plugin.logging.Log
import java.nio.file.Path

class MavenLogOutputDriver(private val logger: Log) : OutputPort {
    override fun output(steps: List<String>, destination: Path) {
        steps.forEach { logger.info(it) }
    }
}