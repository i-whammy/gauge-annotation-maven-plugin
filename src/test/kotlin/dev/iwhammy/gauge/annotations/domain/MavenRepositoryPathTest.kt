package dev.iwhammy.gauge.annotations.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldNotBeInstanceOf
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.IOException
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

    @Disabled("Enable this test when mockkStatic is available in JDK21")
    @Test
    fun testMavenDependentJarUrls() {
        val path = Path.of("for.testing")
        val expected = listOf(Path.of("some.jar").toUri().toURL())
        every { Files.walk(path) } returns listOf(Path.of("some.jar"), Path.of("some.jar.not")).stream()
        MavenRepositoryPath(path).mavenDependentJarUrls() shouldBe expected
    }

    @Disabled("Enable this test when mockkStatic is available in JDK21")
    @Test
    fun testMavenDependentJarUrlsThrowsIOException() {
        val path = Path.of("for.testing.of.io.exception")
        every { Files.walk(path) } throws IOException("This is test.")
        shouldThrow<IOException> { MavenRepositoryPath(path).mavenDependentJarUrls() }
    }

    @Disabled("Enable this test when mockkStatic is available in JDK21")
    @Test
    fun testMavenDependentJarUrlsThrowsAnyOtherExceptions() {
        val path = Path.of("for.testing.of.some.exception")
        every { Files.walk(path) } throws RuntimeException("This is test.")
        val expected = shouldThrow<RuntimeException> { MavenRepositoryPath(path).mavenDependentJarUrls() }
        expected.cause?.shouldNotBeInstanceOf<IOException>()
    }
}