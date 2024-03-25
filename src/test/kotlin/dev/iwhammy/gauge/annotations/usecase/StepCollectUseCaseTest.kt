package dev.iwhammy.gauge.annotations.usecase

import dev.iwhammy.gauge.annotations.MavenProjectConfig
import dev.iwhammy.gauge.annotations.domain.*
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

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
        val compileClasspaths = mockk<List<CompileClasspath>>()
        val gaugeAnnotationClassLoader = mockk<GaugeAnnotationClassLoader>()

        every { mavenProjectConfig.mavenRepositoryPath } returns repoPath
        every { mavenProjectConfig.compileClasspathElements } returns compileClasspathElements
        every { compileClasspathFactory.get(compileClasspathElements) } returns compileClasspaths
        every { mavenRepositoryPathFactory.get(repoPath) } returns mavenRepositoryPath
        every {
            gaugeAnnotationClassLoaderFactory.get(
                compileClasspaths,
                mavenRepositoryPath
            )
        } returns gaugeAnnotationClassLoader
        mockkConstructor(CompileClasspath::class)
        every { anyConstructed<CompileClasspath>().collectClassNamesInPath() } returns classNames
        every { gaugeAnnotationClassLoader.collectAnnotationValues(classNames) } returns stepValues
        every { outputPort.output(stepValues) } just Runs
        stepCollectUseCase.execute()
        verify {
            compileClasspathFactory.get(compileClasspathElements)
            mavenRepositoryPathFactory.get(repoPath)
            gaugeAnnotationClassLoaderFactory.get(
                compileClasspaths,
                mavenRepositoryPath
            )
            outputPort.output(stepValues)
        }
    }
}