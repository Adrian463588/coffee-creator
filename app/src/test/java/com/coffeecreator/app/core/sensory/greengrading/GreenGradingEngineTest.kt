package com.coffeecreator.app.core.sensory.greengrading

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GreenGradingEngineTest {

    @Test
    fun testSpecialtyGrade1CleanSample() {
        // 350g sample with 0 Category 1 defects, 3 partial black (1 full defect equivalent), moisture 11.2%
        val sample = GreenLotSample(
            lotId = "lot_eth_01",
            lotName = "Ethiopia Yirgacheffe Washed G1",
            species = GreenCoffeeSpecies.COFFEA_ARABICA,
            processMethod = GreenProcessingMethod.WASHED,
            sampleMassGrams = 350.0,
            defects = listOf(
                DefectTallyRecord(GreenDefectType.PARTIAL_BLACK, 3) // 3 / 3 = 1.0 equivalent
            ),
            moisture = MoistureAssessment(moisturePercent = 11.2)
        )

        val result = StandardGreenGradingEngine.evaluateWashedArabicaGrade(sample)
        assertEquals(PhysicalGradeClassification.SPECIALTY_GRADE_1, result.grade)
        assertTrue(result.isSpecialtyPhysicalEligible)
        assertTrue(result.moistureWithinStandardRange)
        assertEquals(0.0, result.totalCategory1Equivalents, 0.001)
        assertEquals(1.0, result.totalCategory2Equivalents, 0.001)
        assertEquals(1.0, result.totalFullDefectsEquivalents, 0.001)
    }

    @Test
    fun testCategory1DefectDisqualifiesSpecialty() {
        // 350g sample with 1 Full Black (Cat 1 defect), 0 Cat 2 defects
        val sample = GreenLotSample(
            lotId = "lot_gt_01",
            lotName = "Guatemala Antigua",
            species = GreenCoffeeSpecies.COFFEA_ARABICA,
            processMethod = GreenProcessingMethod.WASHED,
            sampleMassGrams = 350.0,
            defects = listOf(
                DefectTallyRecord(GreenDefectType.FULL_BLACK, 1)
            ),
            moisture = MoistureAssessment(moisturePercent = 10.5)
        )

        val result = StandardGreenGradingEngine.evaluateWashedArabicaGrade(sample)
        // With 1 Cat 1 defect, it cannot be Specialty Grade 1; total full defects = 1.0 -> Premium Grade 2
        assertEquals(PhysicalGradeClassification.PREMIUM_GRADE_2, result.grade)
        assertFalse(result.isSpecialtyPhysicalEligible)
        assertEquals(1.0, result.totalCategory1Equivalents, 0.001)
    }

    @Test
    fun testOutOfScopeGuardForCanephoraOrNatural() {
        // Robusta sample
        val robustaSample = GreenLotSample(
            lotId = "lot_rob_01",
            lotName = "Lampung Fine Robusta",
            species = GreenCoffeeSpecies.COFFEA_CANEPHORA,
            processMethod = GreenProcessingMethod.WASHED,
            sampleMassGrams = 350.0,
            moisture = MoistureAssessment(moisturePercent = 11.0)
        )
        val robustaResult = StandardGreenGradingEngine.evaluateWashedArabicaGrade(robustaSample)
        assertEquals(PhysicalGradeClassification.UNDETERMINED_OUT_OF_SCOPE, robustaResult.grade)
        assertFalse(robustaResult.isSpecialtyPhysicalEligible)
        assertTrue(robustaResult.evaluationNotes.first().contains("only valid for Coffea Arabica washed"))

        // Arabica Natural sample (2004 washed standard not directly applicable)
        val naturalSample = GreenLotSample(
            lotId = "lot_nat_01",
            lotName = "Ethiopia Guji Natural",
            species = GreenCoffeeSpecies.COFFEA_ARABICA,
            processMethod = GreenProcessingMethod.NATURAL,
            sampleMassGrams = 350.0,
            moisture = MoistureAssessment(moisturePercent = 11.5)
        )
        val naturalResult = StandardGreenGradingEngine.evaluateWashedArabicaGrade(naturalSample)
        assertEquals(PhysicalGradeClassification.UNDETERMINED_OUT_OF_SCOPE, naturalResult.grade)
    }

    @Test
    fun testSieveFractionsDistribution() {
        val sieveMasses = mapOf(
            ScreenSieveNumber.SCREEN_18 to 175.0, // 50%
            ScreenSieveNumber.SCREEN_17 to 105.0, // 30%
            ScreenSieveNumber.SCREEN_16 to 70.0   // 20%
        )
        val fractions = StandardGreenGradingEngine.calculateSieveFractions(350.0, sieveMasses)
        assertEquals(3, fractions.size)
        assertEquals(50.0, fractions[0].percentageOfTotal, 0.01)
        assertEquals(30.0, fractions[1].percentageOfTotal, 0.01)
        assertEquals(20.0, fractions[2].percentageOfTotal, 0.01)
    }

    @Test
    fun testUnmeasuredMoistureHandling() {
        val sample = GreenLotSample(
            lotId = "lot_unmeas_01",
            lotName = "Java Preanger Washed",
            species = GreenCoffeeSpecies.COFFEA_ARABICA,
            processMethod = GreenProcessingMethod.WASHED,
            sampleMassGrams = 350.0,
            moisture = MoistureAssessment(moisturePercent = null) // NOT_MEASURED
        )
        val result = StandardGreenGradingEngine.evaluateWashedArabicaGrade(sample)
        assertFalse(result.moistureWithinStandardRange)
        assertTrue(result.evaluationNotes.any { it.contains("NOT_MEASURED") })
    }
}
