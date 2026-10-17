package com.coffeecreator.app.core.sensory.training

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TriangleStatisticsEngineTest {

    @Test
    fun testSixPermutationsBalancedSessionGeneration() {
        val trials = TriangleStatisticsEngine.generateBalancedSession(
            sessionSeed = 12345L,
            trialCount = 6,
            referenceCoffee = "Ethiopia Yirgacheffe (Washed)",
            contrastCoffee = "Ethiopia Yirgacheffe (Natural)"
        )

        assertEquals(6, trials.size)
        val orders = trials.map { it.assignedOrder }.toSet()
        // With 6 trials and 6 distinct permutations, all 6 should be represented
        assertEquals(6, orders.size)
        assertTrue(orders.contains(TriangleSampleOrder.AAB))
        assertTrue(orders.contains(TriangleSampleOrder.ABA))
        assertTrue(orders.contains(TriangleSampleOrder.BAA))
        assertTrue(orders.contains(TriangleSampleOrder.BBA))
        assertTrue(orders.contains(TriangleSampleOrder.BAB))
        assertTrue(orders.contains(TriangleSampleOrder.ABB))

        // Check each trial has 3 opaque codes
        trials.forEach { t ->
            assertEquals(3, t.blindSampleCodes.size)
            assertFalse(t.isEvaluated)
        }
    }

    @Test
    fun testTrialEvaluationAgainstConcealedKey() {
        val trials = TriangleStatisticsEngine.generateBalancedSession(123L, 6)
        val trial0 = trials[0]
        val oddIndex = trial0.assignedOrder.oddCupIndex

        // Correct selection
        val evaluatedCorrect = TriangleStatisticsEngine.evaluateTrial(trial0, oddIndex, 5)
        assertTrue(evaluatedCorrect.isEvaluated)
        assertTrue(evaluatedCorrect.isCorrect)
        assertEquals(oddIndex, evaluatedCorrect.submittedChoiceIndex)

        // Incorrect selection
        val wrongIndex = (oddIndex + 1) % 3
        val evaluatedWrong = TriangleStatisticsEngine.evaluateTrial(trial0, wrongIndex, 3)
        assertTrue(evaluatedWrong.isEvaluated)
        assertFalse(evaluatedWrong.isCorrect)
    }

    @Test
    fun testExactBinomialPValueCalculus() {
        // n = 6, k = 5:
        // C(6,5) * (1/3)^5 * (2/3)^1 + C(6,6) * (1/3)^6 * (2/3)^0
        // = 6 * (2 / 729) + 1 * (1 / 729) = 13 / 729 ≈ 0.01783
        val result6_5 = TriangleStatisticsEngine.calculateBinomialPValue(n = 6, k = 5)
        assertEquals(0.0178, result6_5.pValueCumulative, 0.001)
        assertTrue(result6_5.isSignificantAlpha05)
        assertTrue(result6_5.methodologicalCaveats.isNotEmpty())

        // n = 6, k = 2 (at chance expectation):
        val result6_2 = TriangleStatisticsEngine.calculateBinomialPValue(n = 6, k = 2)
        assertFalse(result6_2.isSignificantAlpha05)
        assertTrue(result6_2.pValueCumulative > 0.60)

        // n = 18, k = 10:
        val result18_10 = TriangleStatisticsEngine.calculateBinomialPValue(n = 18, k = 10)
        assertEquals(0.0433, result18_10.pValueCumulative, 0.002)
        assertTrue(result18_10.isSignificantAlpha05)
        assertFalse(result18_10.isSignificantAlpha01)
    }
}
