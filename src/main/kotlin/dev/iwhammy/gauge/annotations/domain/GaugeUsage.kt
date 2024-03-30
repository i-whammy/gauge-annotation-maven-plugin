package dev.iwhammy.gauge.annotations.domain

import com.thoughtworks.gauge.Step

data class GaugeProjectUsage(val classes: List<GaugeClassUsage>) {
    fun hasUsage() = this.steps().isNotEmpty()
    fun steps() = this.classes.flatMap { it.gaugeUsedMethods.flatMap { it.entries.flatMap { it.annotationValues } } }
}

data class GaugeClassUsage(val className: String, val gaugeUsedMethods: List<GaugeUsedMethod>)
data class GaugeUsedMethod(val methodName: String, val entries: List<GaugeAnnotationEntry>)
data class GaugeAnnotationEntry(val gaugeAnnotationClass: Class<*>, val annotationValues: List<String>)

fun <T> Class<T>.loadGaugeUsage(): GaugeClassUsage {
    val target = this.classLoader.loadClass(Step::class.java.name)
    return this.declaredMethods
        .filter { method -> method.annotations.any { it.annotationClass.qualifiedName == target.canonicalName } }
        .map { method ->
            val annotationValues = method.getDeclaredAnnotation(target as Class<out Annotation>)
                .let { target.getMethod("value").invoke(it) as Array<String> }.toList()
            GaugeUsedMethod(
                method.name, listOf(
                    GaugeAnnotationEntry(target, annotationValues)
                )
            )
        }
        .let { GaugeClassUsage(this.name, it) }
}


private fun List<GaugeClassUsage>.filterUsed() = this.filter { it.gaugeUsedMethods.isNotEmpty() }

fun GaugeProjectUsage.filterUsed() = this.classes.filterUsed().let { GaugeProjectUsage(it) }