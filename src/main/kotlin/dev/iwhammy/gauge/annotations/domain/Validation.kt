package dev.iwhammy.gauge.annotations.domain

fun <T> validate(target: T, fn: (T) -> Boolean) {
    if (!fn.invoke(target)) {
        throw NoValidationSet("")
    }
}

class NoValidationSet(override val message: String): RuntimeException("")