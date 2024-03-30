package dev.iwhammy.gauge.annotations.driver

import dev.iwhammy.gauge.annotations.domain.GaugeProjectUsage
import dev.iwhammy.gauge.annotations.usecase.OutputPort
import java.nio.file.Files
import java.nio.file.Path

class MarkDownOutputDriver : OutputPort {
    override fun output(gaugeProjectUsage: GaugeProjectUsage, destination: Path) {
        Files.newBufferedWriter(destination).use { writer ->
            gaugeProjectUsage.steps().forEach {
                writer.write(" - $it")
                writer.newLine()
            }
        }
    }
}