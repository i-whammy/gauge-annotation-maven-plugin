package dev.iwhammy.gauge.annotations.driver

import dev.iwhammy.gauge.annotations.usecase.OutputPort
import java.nio.file.Files
import java.nio.file.Path

class MarkDownOutDriver(private val filePath: Path) : OutputPort {
    override fun output(steps: List<String>) {
        Files.newBufferedWriter(filePath).use { writer ->
            steps.forEach {
                writer.write(" - $it")
                writer.newLine()
            }
        }
    }
}