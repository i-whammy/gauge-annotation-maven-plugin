package dev.iwhammy.gauge.annotations.driver

import com.thoughtworks.gauge.Step
import org.apache.maven.plugin.logging.Log
import org.slf4j.LoggerFactory
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.util.stream.Stream

class FilesDriver(private val logger: Log) {

    fun collectMavenDependentJarPaths(path: Path = Path.of("${System.getProperty("user.home")}/.m2/repository")): List<Path> {
        return Files.walk(path).filter { it.toString().endsWith("jar") }.toList()
    }

    fun collectStepValues(classpaths: List<Path>, urlClassLoader: URLClassLoader): List<String> {
        val steps = mutableListOf<String>()
        classpaths.forEach { classpath ->
            try {
                Files.newDirectoryStream(classpath).use { stream ->
                    stream.forEach { path ->
                        if (Files.isDirectory(path)) {
                            Files.walk(path)
                                .filter { it.toString().endsWith("class") }
                                .forEach { classFilePath ->
                                    val className =
                                        classFilePath.toString()
                                            .removePrefix(classpath.toString())
                                            .removeSuffix(".class")
                                            .replace("/", ".").replaceFirst(".", "")
                                    val clazz = urlClassLoader.loadClass(className)
                                    steps.addAll(retrieveSteps(clazz))
                                }
                        }
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
