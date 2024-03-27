package dev.iwhammy.gauge.annotations.driver

import dev.iwhammy.gauge.annotations.usecase.OutputPort
import java.nio.file.Files
import java.nio.file.Path

class MarkDownOutDriver : OutputPort {
    override fun output(steps: List<String>, destination: Path) {
        Files.newBufferedWriter(destination).use { writer ->
            steps.forEach {
                writer.write(" - $it")
                writer.newLine()
            }
        }
    }
}