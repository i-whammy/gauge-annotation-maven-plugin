package dev.iwhammy.gauge.annotations.driver

import dev.iwhammy.gauge.annotations.usecase.OutputPort
import java.nio.file.Path

class StandardOutDriver() : OutputPort {
    override fun output(steps: List<String>, destination: Path) {
        steps.forEach { println(it) }
    }
}