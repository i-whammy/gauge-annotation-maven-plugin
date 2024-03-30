package dev.iwhammy.gauge.annotations.domain

sealed class Report {
    companion object {
        fun of(usage: GaugeProjectUsage): Report {
            return if (usage.hasUsage()) {
                GaugeUsageReport(usage.filterUsed())
            } else Nothing
        }
    }
    class GaugeUsageReport(val usage: GaugeProjectUsage): Report()
    data object Nothing: Report()
}