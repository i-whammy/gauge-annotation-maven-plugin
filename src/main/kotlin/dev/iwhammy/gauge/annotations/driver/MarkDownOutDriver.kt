package dev.iwhammy.gauge.annotations.driver

import dev.iwhammy.gauge.annotations.domain.GaugeUsage
import dev.iwhammy.gauge.annotations.usecase.OutputPort
import java.nio.file.Files
import java.nio.file.Path

class MarkDownOutDriver : OutputPort {
    override fun output(gaugeUsages: List<GaugeUsage>, destination: Path) {
        Files.newBufferedWriter(destination).use { writer ->
            gaugeUsages.forEach {
                writer.write(" - $it")
                writer.newLine()
            }
        }
    }
}