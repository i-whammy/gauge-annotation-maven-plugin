package dev.iwhammy.gauge.annotations.usecase

import dev.iwhammy.gauge.annotations.domain.GaugeProjectUsage
import java.nio.file.Path

interface OutputPort {
    fun output(gaugeProjectUsage: GaugeProjectUsage, destination: Path)
}