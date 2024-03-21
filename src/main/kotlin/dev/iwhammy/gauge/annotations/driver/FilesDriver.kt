package dev.iwhammy.gauge.annotations.driver

import com.thoughtworks.gauge.Step
import org.slf4j.LoggerFactory
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path

class FilesDriver {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun collectMavenDependentJarURLs(path: Path = Path.of("${System.getProperty("user.home")}/.m2/repository")): List<URL> {
        return Files.walk(path).filter { it.toString().endsWith("jar") }.map { it.toUri().toURL() }.toList()
    }

    fun collectStepValues(classpath: Path, urlClassLoader: URLClassLoader) {
        Files.newDirectoryStream(classpath).use { stream ->
            stream.forEach { path ->
                if (Files.isDirectory(path)) {
                    Files.walk(path).filter { it.toString().endsWith("class") }
                        .forEach { classFilePath ->
                            val className =
                                classFilePath.toString().removePrefix(classpath.toString()).removeSuffix(".class")
                                    .replace("/", ".").replaceFirst(".", "")
                            val clazz = urlClassLoader.loadClass(className)
                            showClassInformation(clazz)
                        }
                }
            }
        }
    }

    private fun showClassInformation(clazz: Class<*>) {
        val stepClass = clazz.classLoader.loadClass(Step::class.qualifiedName)
        clazz.declaredMethods
            .filter { method -> method.annotations.any { it.annotationClass.qualifiedName == Step::class.qualifiedName } }
            .mapNotNull { method -> method.getDeclaredAnnotation(stepClass as Class<out Annotation>) }
            .takeIf { it.isNotEmpty() }
            ?.map { (stepClass.getMethod("value").invoke(it) as Array<*>) }
            ?.forEach { logger.info(it.joinToString(",")) }
    }
}
