package dev.iwhammy.gauge.annotations.domain

import com.thoughtworks.gauge.Step
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class GaugeUsageTest {

    @Test
    fun testLoadGaugeUsage() {
        DummyForTest::class.java.loadGaugeUsage() shouldBe GaugeClassUsage(
            DummyForTest::class.qualifiedName!!, listOf(
                GaugeUsedMethod(
                    "annotated", listOf(
                        GaugeAnnotationEntry(Step::class.java, listOf("foo", "bar"))
                    )
                )
            )
        )
    }

    @Test
    fun testLoadGaugeUsageWhenNoGaugeAnnotations() {
        NonGaugeUsedClass::class.java.loadGaugeUsage() shouldBe GaugeClassUsage(
            NonGaugeUsedClass::class.qualifiedName!!, listOf()
        )
    }
}

class DummyForTest {
    @Step("foo", "bar")
    fun annotated() {
    }

    fun nonAnnotated() {}
}

class NonGaugeUsedClass {
    fun nonAnnotated() {}
}