package dev.iwhammy.gauge.annotations.driver

import dev.iwhammy.gauge.annotations.OutputPort

class StandardOutDriver() : OutputPort {
    override fun output(steps: List<String>) {
        steps.forEach { println(it) }
    }
}