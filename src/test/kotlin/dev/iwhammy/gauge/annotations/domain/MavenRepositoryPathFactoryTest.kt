package dev.iwhammy.gauge.annotations.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException
import java.nio.file.Path

class MavenRepositoryPathFactoryTest {

    @BeforeEach
    fun setup() {
        mockkStatic(Path::class)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testWhenInvalidPathWasGiven() {
        val invalidPath = "/somewhere/invalid"
        every { Path.of(invalidPath) } throws IOException("")
        val expected = shouldThrow<MavenRepositoryPathCreation> { MavenRepositoryPathFactory().create(invalidPath) }
        expected.cause?.shouldBeInstanceOf<IOException>()
    }
}