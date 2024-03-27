package dev.iwhammy.gauge.annotations.domain

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path

class MavenRepositoryPathTest {

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    @Disabled
    fun testMavenDependentJarUrls() {
        val path = Path.of("for.testing")
        val expected = listOf(Path.of("some.jar").toUri().toURL())
        mockkStatic(Files::class)
        every { Files.walk(path) } returns listOf(Path.of("some.jar"), Path.of("some.jar.not")).stream()
        MavenRepositoryPath(path).mavenDependentJarUrls() shouldBe expected
    }
}