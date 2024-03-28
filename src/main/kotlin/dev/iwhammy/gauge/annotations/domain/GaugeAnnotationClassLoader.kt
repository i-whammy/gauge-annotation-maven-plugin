package dev.iwhammy.gauge.annotations.domain

import com.thoughtworks.gauge.Step
import java.net.URLClassLoader
import kotlin.reflect.KClass

class GaugeAnnotationClassLoader(private val urlClassLoader: URLClassLoader) {
    fun collectAnnotationValues(classNames: List<String>, collectingTargetClass: KClass<*> = Step::class): List<String> {
        val steps = mutableListOf<String>()
        classNames.forEach { className ->
            try {
                val clazz = urlClassLoader.loadClass(className)
                val targetAnnotation = clazz.classLoader.loadClass(collectingTargetClass.qualifiedName)
                clazz.declaredMethods
                    .filter { method -> method.annotations.any { it.annotationClass.qualifiedName == collectingTargetClass.qualifiedName } }
                    .map { method -> method.getDeclaredAnnotation(targetAnnotation as Class<out Annotation>) }
                    .map { (targetAnnotation.getMethod("value").invoke(it) as Array<*>) }
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