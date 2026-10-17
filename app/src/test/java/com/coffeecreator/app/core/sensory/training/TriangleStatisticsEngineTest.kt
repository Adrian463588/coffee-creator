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

    @Test
    fun testPairedComparisonPValueTwoAFC() {
        // n = 10, k = 8 for p0 = 0.5:
        // C(10,8)*0.5^10 + C(10,9)*0.5^10 + C(10,10)*0.5^10 = (45 + 10 + 1) / 1024 = 56 / 1024 ≈ 0.05468
        val result10_8 = TriangleStatisticsEngine.calculatePairedComparisonPValue(10, 8)
        assertEquals(0.0547, result10_8.pValueCumulative, 0.001)
        assertFalse(result10_8.isSignificantAlpha05)

        // n = 10, k = 9:
        // (10 + 1) / 1024 = 11 / 1024 ≈ 0.0107
        val result10_9 = TriangleStatisticsEngine.calculatePairedComparisonPValue(10, 9)
        assertEquals(0.0107, result10_9.pValueCumulative, 0.001)
        assertTrue(result10_9.isSignificantAlpha05)
    }

    @Test
    fun testAscendingConcentrationBestEstimateThreshold() {
        val steps = listOf(
            AscendingConcentrationStep(1, 0.05, "101", isDistinguishableFromWater = false, identifiedTaste = null, isCorrectIdentification = false),
            AscendingConcentrationStep(2, 0.10, "102", isDistinguishableFromWater = true, identifiedTaste = null, isCorrectIdentification = false),
            AscendingConcentrationStep(3, 0.20, "103", isDistinguishableFromWater = true, identifiedTaste = BasicTaste.SOUR, isCorrectIdentification = true),
            AscendingConcentrationStep(4, 0.40, "104", isDistinguishableFromWater = true, identifiedTaste = BasicTaste.SOUR, isCorrectIdentification = true)
        )

        val thresholdResult = TriangleStatisticsEngine.calculateBestEstimateThreshold(steps, BasicTaste.SOUR)
        // Detection transition between step 1 (0.05) and step 2 (0.10): sqrt(0.05 * 0.10) ≈ 0.0707
        assertEquals(0.0707, thresholdResult.detectionThresholdGramsPerLiter ?: 0.0, 0.001)
        // Recognition transition between step 2 (0.10) and step 3 (0.20): sqrt(0.10 * 0.20) ≈ 0.1414
        assertEquals(0.1414, thresholdResult.recognitionThresholdGramsPerLiter ?: 0.0, 0.001)
        assertTrue(thresholdResult.nonClinicalDisclaimer.contains("Does not infer medical or clinical gustatory impairment"))
    }

    @Test
    fun testOlfactorySpacedRepetitionSM2() {
        val now = 1000000L
        val initialCard = AromaSpacedRepetitionCard(
            vialId = "enz_01",
            repetitionCount = 0,
            intervalDays = 1.0,
            easeFactor = 2.5,
            nextReviewEpochMs = now
        )

        // Correct response with high confidence (q = 5)
        val updatedCard1 = TriangleStatisticsEngine.updateSpacedRepetition(initialCard, qualityRating = 5, nowEpochMs = now)
        assertEquals(1, updatedCard1.repetitionCount)
        assertEquals(1.0, updatedCard1.intervalDays, 0.01)
        assertEquals(2.6, updatedCard1.easeFactor, 0.01)
        assertEquals(1, updatedCard1.totalAttempts)
        assertEquals(1, updatedCard1.correctAttempts)

        // Second correct response (q = 4)
        val updatedCard2 = TriangleStatisticsEngine.updateSpacedRepetition(updatedCard1, qualityRating = 4, nowEpochMs = now)
        assertEquals(2, updatedCard2.repetitionCount)
        assertEquals(3.0, updatedCard2.intervalDays, 0.01)

        // Failure response (q = 2): resets interval to 1.0 and rep count to 0
        val failedCard = TriangleStatisticsEngine.updateSpacedRepetition(updatedCard2, qualityRating = 2, nowEpochMs = now)
        assertEquals(0, failedCard.repetitionCount)
        assertEquals(1.0, failedCard.intervalDays, 0.01)
        assertTrue(failedCard.easeFactor < updatedCard2.easeFactor)
    }

    @Test
    fun testAromaConfusionMatrixAndBrierScore() {
        val trials = listOf(
            Pair("enz_01", "enz_01"),
            Pair("enz_01", "enz_02"),
            Pair("enz_01", "enz_01"),
            Pair("sb_01", "sb_02")
        )
        val matrix = TriangleStatisticsEngine.computeAromaConfusionMatrix(trials)
        val topEntry = matrix.first()
        assertEquals("enz_01", topEntry.targetVialId)
        assertEquals("enz_01", topEntry.selectedVialId)
        assertEquals(2, topEntry.frequency)

        // Brier score: perfect calibration (confidence 5/5 -> prob 1.0, outcome 1.0)
        val perfectPredictions = listOf(Pair(5, true), Pair(5, true))
        val perfectBrier = TriangleStatisticsEngine.calculateBrierScore(perfectPredictions)
        assertEquals(0.0, perfectBrier, 0.0001)

        // Pure overconfidence failure (confidence 5/5 -> prob 1.0, outcome 0.0)
        val overconfidentPredictions = listOf(Pair(5, false))
        val overconfidentBrier = TriangleStatisticsEngine.calculateBrierScore(overconfidentPredictions)
        assertEquals(1.0, overconfidentBrier, 0.0001)
    }

    @Test
    fun testHumanObservationVerificationGuard() {
        // Test S4-040: Never auto-pass based on reading or quizzes
        val autoPassedAttempt = PracticalExerciseVerification(
            exerciseId = "ex_taste_01",
            verificationType = PracticalVerificationType.PHYSICAL_TASTING_SELF_LOGGED,
            physicalSamplePrepared = true,
            verifierPseudonym = "Alex",
            timestampEpochMs = 1234567L,
            isAutoPassed = true // VIOLATION
        )
        assertFalse(TriangleStatisticsEngine.verifyPracticalSensoryPassGuard(autoPassedAttempt))

        // Virtual-only (no physical sample prepared)
        val noPhysicalSample = PracticalExerciseVerification(
            exerciseId = "ex_taste_02",
            verificationType = PracticalVerificationType.PHYSICAL_TASTING_SELF_LOGGED,
            physicalSamplePrepared = false, // VIOLATION
            verifierPseudonym = "Alex",
            timestampEpochMs = 1234567L,
            isAutoPassed = false
        )
        assertFalse(TriangleStatisticsEngine.verifyPracticalSensoryPassGuard(noPhysicalSample))

        // Valid physical observation with human co-signer
        val validPhysicalObservation = PracticalExerciseVerification(
            exerciseId = "ex_taste_03",
            verificationType = PracticalVerificationType.TRAINER_SUPERVISED_CO_SIGNED,
            physicalSamplePrepared = true,
            verifierPseudonym = "Instructor Sarah",
            timestampEpochMs = 1234567L,
            isAutoPassed = false
        )
        assertTrue(TriangleStatisticsEngine.verifyPracticalSensoryPassGuard(validPhysicalObservation))
    }

    @Test
    fun testStandardOrganicAcidsAndRoastDefectsIntegrity() {
        // S4-035: Ensure all 7 organic acids are defined with positive concentration
        assertEquals(7, OrganicAcidType.values().size)
        OrganicAcidType.values().forEach { acid ->
            assertTrue(acid.pKa1 > 0.0)
            assertTrue(acid.referencePreparationGramsPerLiter > 0.0)
            assertTrue(acid.sensoryProfileEn.isNotBlank())
        }

        // S4-036: Ensure 5 roast defects are cataloged
        assertEquals(5, RoastDefectCategory.values().size)
        assertEquals(4, StandardRoastDefects.ALL_DEFECTS.size)
    }
}
