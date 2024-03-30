package dev.iwhammy.gauge.annotations.usecase

import dev.iwhammy.gauge.annotations.MavenProjectConfig
import dev.iwhammy.gauge.annotations.domain.*
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
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

    @Disabled("Enable this test when mockkStatic is available in JDK21")
    @Test
    fun testStepCollectionUseCase() {
        val repoPath = "path.for.repo"
        val compileClasspathElements = listOf("classpath.elements")
        val classNames = listOf("dummy.class")
        val stepValues = GaugeProjectUsage(
            listOf(
                GaugeClassUsage(
                    "some.class.used", listOf(
                        GaugeUsedMethod(
                            "someMethod", listOf(
                                mockk()
                            )
                        )
                    )
                ),
                GaugeClassUsage(
                    "some.class.not.used", listOf(
                    )
                ),
            )
        )
        val mavenRepositoryPath = mockk<MavenRepositoryPath>()
        val compileClasspaths = listOf(mockk<CompileClasspath>())
        val gaugeAnnotationClassLoader = mockk<GaugeAnnotationClassLoader>()

        every { mavenProjectConfig.mavenRepositoryPath } returns repoPath
        every { mavenProjectConfig.compileClasspaths } returns compileClasspathElements
        every { compileClasspathFactory.create(compileClasspathElements) } returns compileClasspaths
        every { mavenRepositoryPathFactory.create(repoPath) } returns mavenRepositoryPath
        every {
            gaugeAnnotationClassLoaderFactory.create(
                compileClasspaths, mavenRepositoryPath
            )
        } returns gaugeAnnotationClassLoader
        mockkStatic(List<CompileClasspath>::collectClassNamesInPath)
        every { compileClasspaths.collectClassNamesInPath() } returns classNames
        every { gaugeAnnotationClassLoader.collectProjectAnnotations(classNames) } returns stepValues
        every { outputPort.output(any<Report.GaugeUsageReport>()) } just Runs
        stepCollectUseCase.execute()
        verify {
            outputPort.output(any<Report.GaugeUsageReport>())
        }
    }
}