package dev.iwhammy.gauge.annotations.domain

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.nio.file.Files
import java.nio.file.Path

@ExtendWith(MockKExtension::class)
class MavenRepositoryPathTest {

    @BeforeEach
    fun setup() {
        mockkStatic(Files::class)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testMavenDependentJarUrls() {
        val path = Path.of("for.testing")
        val expected = listOf(Path.of("some.jar").toUri().toURL())
        every { Files.walk(path) } returns listOf(Path.of("some.jar"), Path.of("some.jar.not")).stream()
        MavenRepositoryPath(path).mavenDependentJarUrls() shouldBe expected
    }
}