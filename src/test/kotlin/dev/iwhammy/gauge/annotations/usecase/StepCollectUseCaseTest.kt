package dev.iwhammy.gauge.annotations.usecase

import dev.iwhammy.gauge.annotations.MavenProjectConfig
import dev.iwhammy.gauge.annotations.domain.*
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Path

class StepCollectUseCaseTest {

    @InjectMockKs
    private lateinit var stepCollectUseCase: StepCollectUseCase

    @MockK
    private lateinit var outputPort: OutputPort

    @MockK
    private lateinit var mavenProjectConfig: MavenProjectConfig

    @MockK
    private lateinit var compileClasspathFactory: CompileClasspathFactory

    @MockK
    private lateinit var mavenRepositoryPathFactory: MavenRepositoryPathFactory

    @MockK
    private lateinit var gaugeAnnotationClassLoaderFactory: GaugeAnnotationClassLoaderFactory

    @BeforeEach
    fun initialize() {
        MockKAnnotations.init(this)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun testStepCollectionUseCase() {
        val repoPath = "path.for.repo"
        val compileClasspathElements = listOf("classpath.elements")
        val classNames = listOf("dummy.class")
        val stepValues = listOf("Some step")
        val mavenRepositoryPath = mockk<MavenRepositoryPath>()
        val compileClasspaths = listOf(mockk<CompileClasspath>())
        val basedir = mockk<Path>()
        val gaugeAnnotationClassLoader = mockk<GaugeAnnotationClassLoader>()

        every { mavenProjectConfig.mavenRepositoryPath } returns repoPath
        every { mavenProjectConfig.compileClasspaths } returns compileClasspathElements
        every { mavenProjectConfig.basedir } returns basedir
        every { compileClasspathFactory.create(compileClasspathElements) } returns compileClasspaths
        every { mavenRepositoryPathFactory.create(repoPath) } returns mavenRepositoryPath
        every {
            gaugeAnnotationClassLoaderFactory.create(
                compileClasspaths,
                mavenRepositoryPath
            )
        } returns gaugeAnnotationClassLoader
        mockkStatic(List<CompileClasspath>::collectClassNamesInPath)
        every { compileClasspaths.collectClassNamesInPath() } returns classNames
        every { gaugeAnnotationClassLoader.collectAnnotationValues(classNames) } returns stepValues
        every { outputPort.output(stepValues, basedir) } just Runs
        stepCollectUseCase.execute()
        verify {
            compileClasspathFactory.create(compileClasspathElements)
            mavenRepositoryPathFactory.create(repoPath)
            gaugeAnnotationClassLoaderFactory.create(
                compileClasspaths,
                mavenRepositoryPath
            )
            outputPort.output(stepValues, basedir)
        }
    }
}