package dev.iwhammy.gauge.annotations.domain

import com.thoughtworks.gauge.Step
import java.net.URLClassLoader
import kotlin.reflect.KClass

class GaugeAnnotationClassLoader(private val urlClassLoader: URLClassLoader) {
    fun collectProjectAnnotations(classNames: List<String>, collectingTargetClass: KClass<*> = Step::class): GaugeProjectUsage {
        val gaugeClassUsages = mutableListOf<GaugeClassUsage>()
        classNames.forEach { className ->
            try {
                val clazz = urlClassLoader.loadClass(className)
                gaugeClassUsages.add(clazz.loadGaugeUsage())
            } catch (e: ClassNotFoundException) {
                println("$e ${e.message}")
            }
        }
        return GaugeProjectUsage(gaugeClassUsages)
    }
}
