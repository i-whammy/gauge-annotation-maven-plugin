package dev.iwhammy.gauge.annotations.usecase

interface OutputPort {
    fun output(steps: List<String>)
}