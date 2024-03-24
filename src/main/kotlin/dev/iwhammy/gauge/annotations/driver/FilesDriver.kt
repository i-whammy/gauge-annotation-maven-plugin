package dev.iwhammy.gauge.annotations.driver

import com.thoughtworks.gauge.Step
import dev.iwhammy.gauge.annotations.InputPort
import org.apache.maven.plugin.logging.Log
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import kotlin.streams.toList

class FilesDriver(private val logger: Log): InputPort {

    override fun collectMavenDependentJarPaths(mavenRepositoryPath: Path): List<Path> {
        return Files.walk(mavenRepositoryPath).filter { it.toString().endsWith("jar") }.toList()
    }

    fun collectStepValues(classpaths: List<Path>, urlClassLoader: URLClassLoader): List<String> {
        // List<Path> ... classpaths list
        // Path ... classpath
        // return all paths which end with .class
        // stream.forEach
        // path -> className -> Class
        // Class -> methods -> annotations

        val steps = mutableListOf<String>()
        classpaths.forEach { classpath ->
            try {
                Files.newDirectoryStream(classpath).use { stream ->
                    stream.forEach { path ->
                        Files.walk(path)
                            .filter { it.toString().endsWith("class") }
                            .map { classFilePath ->
                                val className =
                                    classFilePath.toString()
                                        .removePrefix(classpath.toString())
                                        .removeSuffix(".class")
                                        .replace("/", ".").replaceFirst(".", "")
                                val clazz = urlClassLoader.loadClass(className)
                                steps.addAll(retrieveSteps(clazz))
                            }.toList()
                    }
                }
            } catch (e: NoSuchFileException) {
                logger.warn(e.message) // TODO needs more consideration for multi module
            }
        }
        return steps
    }

    // TODO needs refactor
    private fun retrieveSteps(clazz: Class<*>): List<String> {
        val stepClass = clazz.classLoader.loadClass(Step::class.qualifiedName)
        return clazz.declaredMethods
            .filter { method -> method.annotations.any { it.annotationClass.qualifiedName == Step::class.qualifiedName } }
            .map { method -> method.getDeclaredAnnotation(stepClass as Class<out Annotation>) }
            .takeIf { it.isNotEmpty() }
            ?.map { (stepClass.getMethod("value").invoke(it) as Array<*>) }
            ?.map { it.joinToString(",") } ?: emptyList()
    }
}
