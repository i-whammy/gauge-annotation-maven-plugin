package dev.iwhammy.gauge.annotations.usecase

import dev.iwhammy.gauge.annotations.domain.*
import dev.iwhammy.gauge.annotations.usecase.port.OutputPort
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StepCollectUseCaseTest {

    @MockK
    private lateinit var outputPort: OutputPort

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
        val classNames = listOf("dummy.class")
        val stepValues = GaugeProjectUsage(
            listOf(
                GaugeClassUsage(
                    "some.class.used", listOf(
                        GaugeUsedMethod(
                            "someMethod", listOf(
                                GaugeAnnotationEntry(Any::class.java,
                                    listOf("some nice step"))
                            )
                        )
                    )
                ),
                GaugeClassUsage(
                    "some.class.not.used", emptyList()
                ),
            )
        )
        val compileClasspaths = listOf(mockk<CompileClasspath>())
        val gaugeAnnotationClassLoader = mockk<GaugeAnnotationClassLoader>()

        val stepCollectUseCase = StepCollectUseCase(outputPort)

        mockkStatic(List<CompileClasspath>::collectClassNamesInPath)
        every { compileClasspaths.collectClassNamesInPath() } returns classNames
        every { gaugeAnnotationClassLoader.collectProjectAnnotations(classNames) } returns stepValues
        every { outputPort.output(any<Report.GaugeUsageReport>()) } just Runs

        stepCollectUseCase.execute(compileClasspaths, gaugeAnnotationClassLoader)

        verify {
            outputPort.output(any<Report.GaugeUsageReport>())
        }
    }
}