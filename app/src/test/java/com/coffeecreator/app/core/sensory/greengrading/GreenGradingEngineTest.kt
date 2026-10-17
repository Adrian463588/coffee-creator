package com.coffeecreator.app.core.sensory.greengrading

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GreenGradingEngineTest {

    @Test
    fun testSpecialtyGrade1CleanSample() {
        // S4-041 & S4-047: 350g sample with 0 Category 1 defects, 3 partial black (1 full defect equivalent), moisture 11.2%
        val sample = GreenLotSample(
            lotId = "lot_eth_01",
            lotName = "Ethiopia Yirgacheffe Washed G1",
            species = GreenCoffeeSpecies.COFFEA_ARABICA,
            processMethod = GreenProcessingMethod.WASHED,
            sampleMassGrams = 350.0,
            defects = listOf(
                DefectTallyRecord(GreenDefectType.PARTIAL_BLACK, 3) // 3 / 3 = 1.0 equivalent
            ),
            moisture = MoistureAssessment(moisturePercent = 11.2),
            odor = GreenBeanOdor.CLEAN_FRESH_VEGETAL
        )

        val result = StandardGreenGradingEngine.evaluateWashedArabicaGrade(sample)
        assertEquals(PhysicalGradeClassification.SPECIALTY_GRADE_1, result.grade)
        assertTrue(result.isSpecialtyPhysicalEligible)
        assertTrue(result.moistureWithinStandardRange)
        assertTrue(result.odorClean)
        assertEquals(0.0, result.totalCategory1Equivalents, 0.001)
        assertEquals(1.0, result.totalCategory2Equivalents, 0.001)
        assertEquals(1.0, result.totalFullDefectsEquivalents, 0.001)
    }

    @Test
    fun testCategory1DefectDisqualifiesSpecialty() {
        // S4-042 & S4-043: 350g sample with 1 Full Black (Cat 1 defect), 0 Cat 2 defects
        val sample = GreenLotSample(
            lotId = "lot_gt_01",
            lotName = "Guatemala Antigua",
            species = GreenCoffeeSpecies.COFFEA_ARABICA,
            processMethod = GreenProcessingMethod.WASHED,
            sampleMassGrams = 350.0,
            defects = listOf(
                DefectTallyRecord(GreenDefectType.FULL_BLACK, 1, evidencePhotoUris = listOf("content://qc/photo1.jpg"))
            ),
            moisture = MoistureAssessment(moisturePercent = 10.5)
        )

        val result = StandardGreenGradingEngine.evaluateWashedArabicaGrade(sample)
        assertEquals(PhysicalGradeClassification.PREMIUM_GRADE_2, result.grade)
        assertFalse(result.isSpecialtyPhysicalEligible)
        assertEquals(1.0, result.totalCategory1Equivalents, 0.001)
        assertTrue(result.evaluationNotes.any { it.contains("Category 1 defects present") })
    }

    @Test
    fun testOutOfScopeGuardForCanephoraOrNatural() {
        // S4-047: Robusta sample
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
        assertTrue(robustaResult.evaluationNotes.first().contains("Standard only valid for Coffea Arabica washed"))

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
    fun testSieveAnalysisDistributionAndMassBalanceRecovery() {
        // S4-044: ISO 4150 sieve distribution & mass balance check
        val sieveMasses = mapOf(
            ScreenSieveNumber.SCREEN_18 to 175.0, // 50%
            ScreenSieveNumber.SCREEN_17 to 87.5,  // 25%
            ScreenSieveNumber.SCREEN_16 to 70.0,  // 20%
            ScreenSieveNumber.SCREEN_15 to 14.0,  // 4%
            ScreenSieveNumber.BELOW_12 to 3.5     // 1%
        )
        val analysis = StandardGreenGradingEngine.calculateSieveAnalysis(350.0, sieveMasses)
        assertEquals(5, analysis.fractions.size)
        assertEquals(350.0, analysis.totalRecoveredMassGrams, 0.001)
        assertEquals(0.0, analysis.massBalanceMismatchGrams, 0.001)
        assertTrue(analysis.isMassBalanceValid)
        // Screen 16+ retention: 18 (50%) + 17 (25%) + 16 (20%) = 95.0%
        assertEquals(95.0, analysis.cumulativeRetentionScreen16Plus, 0.01)

        // Test mass balance violation (> 1.5g lost)
        val leakySieveMasses = mapOf(
            ScreenSieveNumber.SCREEN_18 to 175.0,
            ScreenSieveNumber.SCREEN_16 to 170.0 // 345.0g recovered, 5.0g lost
        )
        val leakyAnalysis = StandardGreenGradingEngine.calculateSieveAnalysis(350.0, leakySieveMasses)
        assertEquals(5.0, leakyAnalysis.massBalanceMismatchGrams, 0.001)
        assertFalse(leakyAnalysis.isMassBalanceValid)
    }

    @Test
    fun testUnmeasuredMoistureHandlingNotMeasured() {
        // S4-045 & S4-051: Unmeasured moisture stays NOT_MEASURED
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

    @Test
    fun testFoulOdorImmediateDisqualification() {
        // S4-046: Musty / moldy odor immediately disqualifies sample from specialty
        val sample = GreenLotSample(
            lotId = "lot_musty_01",
            lotName = "Sumatra Kerinci Damaged",
            species = GreenCoffeeSpecies.COFFEA_ARABICA,
            processMethod = GreenProcessingMethod.WASHED,
            sampleMassGrams = 350.0,
            defects = emptyList(), // Zero physical defects!
            moisture = MoistureAssessment(moisturePercent = 11.0),
            odor = GreenBeanOdor.MUSTY
        )
        val result = StandardGreenGradingEngine.evaluateWashedArabicaGrade(sample)
        assertFalse(result.isSpecialtyPhysicalEligible)
        assertFalse(result.odorClean)
        assertTrue(result.evaluationNotes.any { it.contains("ODOR_CONTAMINATION_DISQUALIFIED") })
    }

    @Test
    fun testBulkDensityAndWaterActivityAssessment() {
        // S4-046: Bulk density (ISO 6669) & Water Activity
        val density = StandardGreenGradingEngine.calculateBulkDensity(715.0, 1.0)
        assertEquals(715.0, density.bulkDensityGramsPerLiter!!, 0.01)
        assertTrue(density.isStrictlyHardBean)

        val waterActivitySafe = WaterActivityAssessment(waterActivityAw = 0.580)
        assertTrue(waterActivitySafe.isSafeStorage)

        val waterActivityDangerous = WaterActivityAssessment(waterActivityAw = 0.740)
        assertFalse(waterActivityDangerous.isSafeStorage)
    }

    @Test
    fun testEpistemologicalDecouplingPhysicalVsSensory() {
        // S4-048: Physical grade and sensory score decoupled
        val physicalGrade1 = PhysicalGradingEvaluation(
            standardApplied = "SCA Washed Arabica 2004 Reference",
            sampleMassGrams = 350.0,
            totalCategory1Equivalents = 0.0,
            totalCategory2Equivalents = 2.0,
            totalFullDefectsEquivalents = 2.0,
            grade = PhysicalGradeClassification.SPECIALTY_GRADE_1,
            isSpecialtyPhysicalEligible = true,
            moistureWithinStandardRange = true,
            odorClean = true,
            evaluationNotes = listOf("Clean")
        )

        // Case 1: Physical Grade 1 but low sensory score (76.0) -> Not full specialty
        val dualLowSensory = StandardGreenGradingEngine.evaluateDualSpecialty(physicalGrade1, 76.0)
        assertFalse(dualLowSensory.isFullSpecialtyCertified)
        assertTrue(dualLowSensory.guidanceMessage.contains("sensory score (76.0) is below 80.00"))

        // Case 2: Physical Grade 2 (disqualified) but high sensory score (87.0) -> Cannot override physical grade
        val physicalGrade2 = physicalGrade1.copy(
            grade = PhysicalGradeClassification.PREMIUM_GRADE_2,
            isSpecialtyPhysicalEligible = false,
            totalCategory1Equivalents = 1.0
        )
        val dualHighSensory = StandardGreenGradingEngine.evaluateDualSpecialty(physicalGrade2, 87.0)
        assertFalse(dualHighSensory.isFullSpecialtyCertified)
        assertTrue(dualHighSensory.guidanceMessage.contains("Physical grade cannot be overridden by cup score"))

        // Case 3: Both pass -> Certified full specialty
        val dualPass = StandardGreenGradingEngine.evaluateDualSpecialty(physicalGrade1, 86.5)
        assertTrue(dualPass.isFullSpecialtyCertified)
    }

    @Test
    fun testBuyerSpecificationContractPassFail() {
        // S4-049: Buyer contract evaluation
        val limits = BuyerAcceptanceLimits(
            contractName = "Buyer Premium Spec 2026",
            maxCategory1FullDefects = 0,
            maxTotalEquivalentDefects = 5.0,
            minMoisturePercent = 10.0,
            maxMoisturePercent = 12.0,
            maxWaterActivityAw = 0.620,
            minScreen16PlusPercent = 85.0,
            minBulkDensityGramsPerLiter = 680.0
        )

        val passingSample = GreenLotSample(
            lotId = "lot_buyer_pass",
            lotName = "Colombia Huila Micro",
            species = GreenCoffeeSpecies.COFFEA_ARABICA,
            processMethod = GreenProcessingMethod.WASHED,
            defects = listOf(
                DefectTallyRecord(GreenDefectType.PARTIAL_BLACK, 3) // 1.0 equiv
            ),
            moisture = MoistureAssessment(moisturePercent = 11.2),
            waterActivity = WaterActivityAssessment(waterActivityAw = 0.585),
            bulkDensity = BulkDensityAssessment(bulkDensityGramsPerLiter = 705.0)
        )
        val passingSieve = StandardGreenGradingEngine.calculateSieveAnalysis(
            350.0,
            mapOf(ScreenSieveNumber.SCREEN_18 to 200.0, ScreenSieveNumber.SCREEN_16 to 115.0, ScreenSieveNumber.BELOW_12 to 35.0)
        )
        val result = StandardGreenGradingEngine.evaluateBuyerContract(passingSample, limits, passingSieve)
        assertTrue(result.isAccepted)
        assertEquals(0, result.failureReasons.size)

        // High moisture failing sample
        val wetSample = passingSample.copy(
            moisture = MoistureAssessment(moisturePercent = 12.8)
        )
        val failResult = StandardGreenGradingEngine.evaluateBuyerContract(wetSample, limits, passingSieve)
        assertFalse(failResult.isAccepted)
        assertTrue(failResult.failureReasons.any { it.contains("Moisture (12.8%) outside contract tolerance") })
    }

    @Test
    fun testGreenQcJsonCsvExportAndSha256AuditSeal() {
        // S4-050: Export with equipment calibration, assessor and SHA-256 seal
        val sample = GreenLotSample(
            lotId = "LOT-2026-ETH-YIRG-042",
            lotName = "Yirgacheffe Kochere Grade 1",
            species = GreenCoffeeSpecies.COFFEA_ARABICA,
            processMethod = GreenProcessingMethod.WASHED,
            originCountry = "Ethiopia",
            originRegion = "Gedeo Zone, Kochere",
            producer = "Boji Washing Station",
            sampleMassGrams = 350.0,
            moisture = MoistureAssessment(moisturePercent = 10.8),
            waterActivity = WaterActivityAssessment(waterActivityAw = 0.585),
            bulkDensity = BulkDensityAssessment(bulkDensityGramsPerLiter = 710.0),
            odor = GreenBeanOdor.CLEAN_FRESH_VEGETAL
        )
        val evaluation = StandardGreenGradingEngine.evaluateWashedArabicaGrade(sample)
        val audit = GreenQCAuditRecord(
            assessorName = "Q-Grader Marcus",
            assessorId = "ASSESSOR-4092",
            evaluationTimestampEpochMs = 1792185600000L
        )

        val json = StandardGreenGradingEngine.generateGreenQcJson(sample, evaluation, audit)
        assertTrue(json.contains("\"lot_id\": \"LOT-2026-ETH-YIRG-042\""))
        assertTrue(json.contains("\"sha256_audit_seal\":"))
        assertNotNull(json)

        val csv = StandardGreenGradingEngine.generateGreenQcCsv(sample, evaluation, audit)
        assertTrue(csv.contains("Lot Identifier,LOT-2026-ETH-YIRG-042,N/A,MEASURED"))
        assertTrue(csv.contains("Physical Grade,Specialty Grade 1,N/A,EVALUATED"))
    }

    @Test
    fun testChainOfCustodyDataRetention() {
        // S4-041: Complete chain of custody tracking
        val custody = ChainOfCustody(
            exporter = "Bole Specialty Coffee Exporters",
            importer = "Nordic Coffee Importers Hamburg",
            warehouseLocation = "Port of Hamburg Terminal Shed 4",
            transportContainerId = "MSCU-884192",
            harvestCropYear = "2025/2026",
            ambientStorageTempCelsius = 18.5,
            ambientStorageRelativeHumidityPercent = 55.0
        )
        val sample = GreenLotSample(
            lotId = "lot_custody_01",
            lotName = "Ethiopia Sidama Bensa",
            species = GreenCoffeeSpecies.COFFEA_ARABICA,
            processMethod = GreenProcessingMethod.WASHED,
            chainOfCustody = custody
        )
        assertNotNull(sample.chainOfCustody)
        assertEquals("Bole Specialty Coffee Exporters", sample.chainOfCustody?.exporter)
        assertEquals("Nordic Coffee Importers Hamburg", sample.chainOfCustody?.importer)
        assertEquals("MSCU-884192", sample.chainOfCustody?.transportContainerId)
    }
}
