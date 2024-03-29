package dev.iwhammy.gauge.annotations.usecase

import dev.iwhammy.gauge.annotations.domain.GaugeUsage
import java.nio.file.Path

interface OutputPort {
    fun output(gaugeUsages: List<GaugeUsage>, destination: Path)
}