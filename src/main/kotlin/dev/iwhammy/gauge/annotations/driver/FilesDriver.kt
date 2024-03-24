package dev.iwhammy.gauge.annotations.driver

import com.thoughtworks.gauge.Step
import dev.iwhammy.gauge.annotations.InputPort
import org.apache.maven.plugin.logging.Log
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path

class FilesDriver(private val logger: Log) : InputPort {

    override fun collectMavenDependentJarPaths(mavenRepositoryPath: Path): List<Path> {
        return Files.walk(mavenRepositoryPath).filter { it.toString().endsWith("jar") }.toList()
    }

    fun collectStepValues(classNames: List<String>, urlClassLoader: URLClassLoader): List<String> {
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
                println(e.message)
            }
        }
        return steps
    }
}
