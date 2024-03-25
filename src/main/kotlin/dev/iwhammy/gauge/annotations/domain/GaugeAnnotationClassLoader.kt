package dev.iwhammy.gauge.annotations.domain

import com.thoughtworks.gauge.Step
import java.net.URLClassLoader

class GaugeAnnotationClassLoader(private val urlClassLoader: URLClassLoader) {
    fun collectAnnotationValues(classNames: List<String>): List<String> {
        val steps = mutableListOf<String>()
        classNames.forEach { className ->
            try {
                val clazz = urlClassLoader.loadClass(className)
                val stepClass = clazz.classLoader.loadClass(Step::class.qualifiedName)
                clazz.declaredMethods
                    .filter { method -> method.annotations.any { it.annotationClass.qualifiedName == Step::class.qualifiedName } }
                    .map { method -> method.getDeclaredAnnotation(stepClass as Class<out Annotation>) }
                    .map { (stepClass.getMethod("value").invoke(it) as Array<*>) }
                    .map { it.joinToString(",") }
                    .forEach { steps.add(it) }
            } catch (e: ClassNotFoundException) {
                println("$e ${e.message}")
            }
        }
        return steps
    }
}

class GaugeAnnotationClassLoaderFactory {
    fun get(
        compileClasspaths: List<CompileClasspath>,
        mavenRepositoryPath: MavenRepositoryPath
    ): GaugeAnnotationClassLoader {
        return compileClasspaths.map { it.urlOf() }
            .plus(mavenRepositoryPath.mavenDependentJarUrls())
            .toTypedArray()
            .let { GaugeAnnotationClassLoader(URLClassLoader(it)) }
    }
}