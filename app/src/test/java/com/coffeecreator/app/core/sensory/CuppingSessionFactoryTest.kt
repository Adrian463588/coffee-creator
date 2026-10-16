package com.coffeecreator.app.core.sensory

import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class CuppingSessionFactoryTest {
    @Test
    fun create_requires_named_evaluators_and_samples() {
        assertThrows(IllegalArgumentException::class.java) {
            CuppingSessionFactory.create(
                title = "Calibration",
                location = "",
                templateType = CuppingTemplateType.QUICK_BARISTA_DIAL_IN,
                maskingMode = MaskingMode.SINGLE_BLIND_EVALUATOR,
                evaluatorNames = emptyList(),
                sampleBeanNames = listOf("User bean"),
                createdAtEpochMs = 1L
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            CuppingSessionFactory.create(
                title = "Calibration",
                location = "",
                templateType = CuppingTemplateType.QUICK_BARISTA_DIAL_IN,
                maskingMode = MaskingMode.SINGLE_BLIND_EVALUATOR,
                evaluatorNames = listOf("Evaluator"),
                sampleBeanNames = emptyList(),
                createdAtEpochMs = 1L
            )
        }
    }

    @Test
    fun create_keeps_unprovided_bean_metadata_unknown() {
        val session = CuppingSessionFactory.create(
            title = "Calibration",
            location = "",
            templateType = CuppingTemplateType.QUICK_BARISTA_DIAL_IN,
            maskingMode = MaskingMode.SINGLE_BLIND_EVALUATOR,
            evaluatorNames = listOf("Actual evaluator"),
            sampleBeanNames = listOf("Bean entered by user"),
            createdAtEpochMs = 123L
        )

        val sample = session.samples.single()
        assertEquals("Bean entered by user", sample.beanName)
        assertNull(sample.actualBeanId)
        assertNull(sample.roaster)
        assertNull(sample.origin)
        assertNull(sample.process)
        assertNull(sample.roastDateEpoch)
        assertEquals(123L, session.createdAt)
        assertEquals(123L, session.auditTrail.single().timestamp)
    }

    @Test
    fun validate_attribute_scores_requires_complete_in_range_values() {
        val template = StandardCuppingTemplates.QUICK_BARISTA_DIAL_IN
        val complete = template.attributes.associate { it.key to it.minValue }

        assertThrows(IllegalArgumentException::class.java) {
            CuppingAnalyticsEngine.validateAttributeScores(template, complete - template.attributes.first().key)
        }
        assertThrows(IllegalArgumentException::class.java) {
            CuppingAnalyticsEngine.validateAttributeScores(
                template,
                complete + (template.attributes.first().key to BigDecimal("10.5"))
            )
        }
        CuppingAnalyticsEngine.validateAttributeScores(template, complete)
    }
}
