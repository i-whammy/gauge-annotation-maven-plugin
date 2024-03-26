package dev.iwhammy.gauge.annotations.domain

import java.net.URL
import java.nio.file.DirectoryStream
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path

data class CompileClasspath(private val classpath: String) {
    fun collectClassNamesInPath(): List<String> = Path.of(classpath).collectClassNames()
    fun urlOf(): URL = Path.of(this.classpath).toUri().toURL()
}

fun List<CompileClasspath>.collectClassNamesInPath(): List<String> = this.flatMap { it.collectClassNamesInPath() }

class CompileClasspathFactory {
    fun get(classpaths: List<String>) = classpaths.map { CompileClasspath(it) }
}

fun Path.collectClassNames(): List<String> {
    try {
        Files.newDirectoryStream(this).use { stream: DirectoryStream<Path> ->
            return stream.flatMap { path ->
                Files.walk(path)
                    .filter { it.toString().endsWith("class") }
                    .map { classFilePath ->
                        classFilePath.toString()
                            .removePrefix(this.toString())
                            .removeSuffix(".class")
                            .replace("/", ".").replaceFirst(".", "")
                    }.toList()
            }
        }
    } catch (e: NoSuchFileException) {
        println("$e")
        return emptyList()
    }
}
