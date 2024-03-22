package dev.iwhammy.gauge.annotations.driver

import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.mockkStatic
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class FilesDriverTest {
    @InjectMockKs
    private lateinit var filesDriver: FilesDriver

    init {
        MockKAnnotations.init()
    }

    @Test
    @Disabled
    fun testCollectMavenDependentJarPaths() {
        mockkStatic(Files::class) // In Java 17, there is a bug in mockk. https://github.com/mockk/mockk/issues/368#issuecomment-1223549420
        every { Files.walk(any<Path>()) } returns listOf(
            Path.of("foo.jar"),
            Path.of("bar.jar"),
            Path.of("this.is.not.jar.dummy")
        ).stream()
        filesDriver.collectMavenDependentJarPaths() shouldBe listOf(
            Path.of("foo.jar"),
            Path.of("bar.jar"),
        )
    }
}