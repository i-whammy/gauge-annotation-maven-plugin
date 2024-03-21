package dev.iwhammy.gauge.annotations

import com.thoughtworks.gauge.Step
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import java.net.URLClassLoader
import java.nio.file.Files
import java.nio.file.Path

@Mojo(name = "gauge-annotation")
class GaugeAnnotationMojo(): AbstractMojo() {
    @Parameter(property = "project")
    private lateinit var project: MavenProject

    override fun execute() {
        val m2DependencyJars = Files.walk(Path.of("${System.getProperty("user.home")}/.m2/repository")).filter { it.toString().endsWith("jar") }
        val classpathElements = project.compileClasspathElements.plus(project.artifacts.map { it.file.path })
        val urls = classpathElements.map { Path.of(it).toUri().toURL() }.toTypedArray()
            .plus(m2DependencyJars.map { it.toUri().toURL() }.toList().toTypedArray())
        val urlClassLoader = URLClassLoader(urls)
        classpathElements.forEach { classpath -> collectClassInfo(classpath, urlClassLoader) }

        log.info("debug zone end")
    }

    private fun collectClassInfo(classpath: String, classLoader: URLClassLoader) {
        val path = Path.of(classpath)
        Files.newDirectoryStream(path).use { stream ->
            stream.forEach { path ->
                if (Files.isDirectory(path)) {
                    Files.walk(path).filter { it.toString().endsWith("class") }
                        .forEach { classFilePath ->
                            val className = classFilePath.toString().removePrefix(classpath).removeSuffix(".class").replace("/", ".").replaceFirst(".","")
                            val clazz = classLoader.loadClass(className)
                            showClassInformation(clazz)
                        }
                }
            }
        }
    }

    private fun showClassInformation(clazz: Class<*>) {
        val stepClass = clazz.classLoader.loadClass(Step::class.qualifiedName)
        clazz.declaredMethods
            .filter { it.annotations.any { it.annotationClass.qualifiedName == Step::class.qualifiedName } }
            .mapNotNull { method -> method.getDeclaredAnnotation(stepClass as Class<out Annotation>) }
            .takeIf { it.isNotEmpty() }
            ?.let {
                log.info("Annotation Information")
                it }?.forEach { (stepClass.getMethod("value").invoke(it) as Array<*>).also { log.info(it.joinToString(",")) }}
//            .forEach { it.forEach { step -> log.info(step.value.toString()) } }
//        clazz.methods.flatMap { it.annotations.filter { it.javaClass == Step::class.java }}.forEach { log.info((it as Step).value.joinToString(",") )}
    }}