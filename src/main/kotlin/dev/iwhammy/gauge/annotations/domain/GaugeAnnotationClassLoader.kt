package dev.iwhammy.gauge.annotations.domain

import com.thoughtworks.gauge.Step
import java.net.URLClassLoader
import kotlin.reflect.KClass

class GaugeAnnotationClassLoader(private val urlClassLoader: URLClassLoader) {
    fun collectAnnotationValues(classNames: List<String>, collectingTargetClass: KClass<*> = Step::class): List<GaugeUsage> {
        val gaugeUsages = mutableListOf<GaugeUsage>()
        classNames.forEach { className ->
            try {
                val clazz = urlClassLoader.loadClass(className)
                gaugeUsages.add(clazz.loadGaugeUsage())
            } catch (e: ClassNotFoundException) {
                println("$e ${e.message}")
            }
        }
        return gaugeUsages
    }
}

class GaugeAnnotationClassLoaderFactory {
    fun create(
        compileClasspaths: List<CompileClasspath>,
        mavenRepositoryPath: MavenRepositoryPath
    ): GaugeAnnotationClassLoader {
        return compileClasspaths.map { it.urlOf() }
            .plus(mavenRepositoryPath.mavenDependentJarUrls())
            .toTypedArray()
            .let { GaugeAnnotationClassLoader(URLClassLoader(it)) }
    }
}