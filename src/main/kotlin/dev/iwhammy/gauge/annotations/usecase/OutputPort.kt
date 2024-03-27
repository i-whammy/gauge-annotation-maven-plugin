package dev.iwhammy.gauge.annotations.usecase

import java.nio.file.Path

interface OutputPort {
    fun output(steps: List<String>, destination: Path)
}