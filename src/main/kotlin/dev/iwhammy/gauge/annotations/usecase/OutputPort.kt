package dev.iwhammy.gauge.annotations.usecase

import dev.iwhammy.gauge.annotations.domain.Report

interface OutputPort {
    fun output(gaugeUsageReport: Report.GaugeUsageReport)
}