package dev.iwhammy.gauge.annotations.domain

import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path

fun List<Path>.retrieveClassNames(): List<String> {
    return this.map { it.retrieveClassNames() }.flatten()
}

fun Path.retrieveClassNames(): List<String> {
    try {
        Files.newDirectoryStream(this).use { stream ->
            return stream.flatMap { path ->
                Files.walk(path)
                    .filter { it.toString().endsWith("class") }
                    .map { classFilePath ->
                        classFilePath.toClassName()
                    }.toList()
            }
        }
    } catch (e: NoSuchFileException) {
        println(e.message)
        return emptyList()
    }
}

fun Path.toClassName() =
    this.toString()
        .removePrefix(this.toString())
        .removeSuffix(".class")
        .replace("/", ".").replaceFirst(".", "")