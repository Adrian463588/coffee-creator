package com.coffeecreator.app.core.sensory.greengrading

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
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
            defectInspectionComplete = true,
            // Three partial black beans equal one full defect.
            defects = listOf(DefectTallyRecord(GreenDefectType.PARTIAL_BLACK, 3)),
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
            defectInspectionComplete = true,
            defects = listOf(
                DefectTallyRecord(GreenDefectType.FULL_BLACK, 1, evidencePhotoUris = listOf("content://qc/photo1.jpg"))
            ),
            moisture = MoistureAssessment(moisturePercent = 10.5),
            odor = GreenBeanOdor.CLEAN_FRESH_VEGETAL
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
        assertTrue(robustaResult.evaluationNotes.first().contains("explicitly selected"))

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
    fun givenValueWithNotMeasuredFlag_whenConstructingAssessment_thenRejectsContradictoryState() {
        assertThrows(IllegalArgumentException::class.java) {
            MoistureAssessment(moisturePercent = 11.0, isMeasured = false)
        }
        assertThrows(IllegalArgumentException::class.java) {
            WaterActivityAssessment(waterActivityAw = 0.58, isMeasured = false)
        }
        assertThrows(IllegalArgumentException::class.java) {
            BulkDensityAssessment(bulkDensityGramsPerLiter = 700.0, isMeasured = false)
        }
    }

    @Test
    fun givenDefectEquivalenceThatDisagreesWithRawCount_whenConstructingTally_thenRejectsIt() {
        assertThrows(IllegalArgumentException::class.java) {
            DefectTallyRecord(
                defectType = GreenDefectType.FULL_BLACK,
                rawCount = 1,
                equivalentFullDefects = 0.0
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            DefectTallyRecord(
                defectType = GreenDefectType.SLIGHT_INSECT_DAMAGE,
                rawCount = 10,
                equivalentFullDefects = 1.0,
                standardSupported = false
            )
        }
    }

    @Test
    fun givenObservedBeanColor_whenExportingGreenQc_thenIncludesTheHumanObservation() {
        val sample = GreenLotSample(
            lotId = "lot_color_01",
            lotName = "Color observation",
            species = GreenCoffeeSpecies.COFFEA_ARABICA,
            processMethod = GreenProcessingMethod.WASHED,
            color = GreenBeanColor.GREEN
        )
        val evaluation = StandardGreenGradingEngine.evaluateWashedArabicaGrade(sample)
        val audit = GreenQCAuditRecord(evaluationTimestampEpochMs = 1L)

        val json = StandardGreenGradingEngine.generateGreenQcJson(sample, evaluation, audit)
        val csv = StandardGreenGradingEngine.generateGreenQcCsv(sample, evaluation, audit)

        assertTrue(json.contains("\"bean_color\": \"GREEN\""))
        assertTrue(csv.contains("Bean Color,\"Green (Standard washed)\",N/A,OBSERVED"))
    }

    @Test
    fun givenUnsupportedNonzeroDefectTally_whenGrading_thenClassificationStaysPending() {
        val sample = GreenLotSample(
            lotId = "lot_unsupported_defect_basis",
            lotName = "",
            species = GreenCoffeeSpecies.COFFEA_ARABICA,
            processMethod = GreenProcessingMethod.WASHED,
            sampleMassGrams = 350.0,
            defectInspectionComplete = true,
            defects = listOf(
                DefectTallyRecord(
                    defectType = GreenDefectType.FULL_BLACK,
                    rawCount = 1,
                    standardSupported = false
                )
            ),
            moisture = MoistureAssessment(11.0),
            odor = GreenBeanOdor.CLEAN_FRESH_VEGETAL
        )

        val result = StandardGreenGradingEngine.evaluateWashedArabicaGrade(sample)

        assertEquals(PhysicalGradeClassification.UNDETERMINED_OUT_OF_SCOPE, result.grade)
        assertFalse(result.isSpecialtyPhysicalEligible)
        assertTrue(result.evaluationNotes.any { it.contains("unsupported defect", ignoreCase = true) })
    }

    @Test
    fun givenUnsupportedNonzeroDefectTally_whenCheckingBuyerContract_thenNeverAccepts() {
        val sample = GreenLotSample(
            lotId = "lot_contract_unsupported_defect_basis",
            lotName = "",
            species = GreenCoffeeSpecies.COFFEA_ARABICA,
            processMethod = GreenProcessingMethod.WASHED,
            sampleMassGrams = 350.0,
            defectInspectionComplete = true,
            defects = listOf(
                DefectTallyRecord(
                    defectType = GreenDefectType.FULL_BLACK,
                    rawCount = 1,
                    standardSupported = false
                )
            ),
            moisture = MoistureAssessment(11.0),
            waterActivity = WaterActivityAssessment(0.58),
            bulkDensity = BulkDensityAssessment(700.0),
            odor = GreenBeanOdor.CLEAN_FRESH_VEGETAL
        )
        val limits = BuyerAcceptanceLimits(
            contractName = "Buyer contract",
            sourceReference = "user-provided contract",
            maxCategory1FullDefects = 5,
            maxTotalEquivalentDefects = 10.0,
            minMoisturePercent = 9.0,
            maxMoisturePercent = 13.0,
            maxWaterActivityAw = 0.65,
            minScreen16PlusPercent = 80.0,
            minBulkDensityGramsPerLiter = 650.0
        )
        val sieve = SieveAnalysisResult(350.0, emptyList(), 350.0, 0.0, true, 90.0)

        val result = StandardGreenGradingEngine.evaluateBuyerContract(sample, limits, sieve)

        assertFalse(result.isAccepted)
        assertTrue(result.failureReasons.any { it.contains("unsupported defect", ignoreCase = true) })
    }

    @Test
    fun givenFormulaPrefixedLotId_whenExportingCsv_thenCellIsMarkedAsText() {
        val sample = GreenLotSample(
            lotId = "=HYPERLINK(\"https://unsafe\")",
            lotName = "",
            species = GreenCoffeeSpecies.COFFEA_ARABICA,
            processMethod = GreenProcessingMethod.WASHED
        )
        val evaluation = StandardGreenGradingEngine.evaluateWashedArabicaGrade(sample)
        val csv = StandardGreenGradingEngine.generateGreenQcCsv(
            sample,
            evaluation,
            GreenQCAuditRecord(evaluationTimestampEpochMs = 1L)
        )

        assertTrue(csv.contains("\"'=HYPERLINK(\"\"https://unsafe\"\")\""))
    }

    @Test
    fun testSieveAnalysisDistributionAndMassBalanceRecovery() {
        // S4-044: ISO 4150 sieve distribution & mass balance check
        // Measured sieve fractions: 50%, 25%, 20%, 4%, and 1%.
        val sieveMasses = mapOf(
            ScreenSieveNumber.SCREEN_18 to 175.0,
            ScreenSieveNumber.SCREEN_17 to 87.5,
            ScreenSieveNumber.SCREEN_16 to 70.0,
            ScreenSieveNumber.SCREEN_15 to 14.0,
            ScreenSieveNumber.BELOW_12 to 3.5
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
            ScreenSieveNumber.SCREEN_16 to 170.0
        )
        val leakyAnalysis = StandardGreenGradingEngine.calculateSieveAnalysis(350.0, leakySieveMasses)
        assertEquals(5.0, leakyAnalysis.massBalanceMismatchGrams, 0.001)
        assertFalse(leakyAnalysis.isMassBalanceValid)

        assertThrows(IllegalArgumentException::class.java) {
            StandardGreenGradingEngine.calculateSieveAnalysis(
                350.0,
                mapOf(ScreenSieveNumber.SCREEN_18 to Double.NaN)
            )
        }
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
            // No meter reading exists for this sample.
            moisture = MoistureAssessment(moisturePercent = null)
        )
        val result = StandardGreenGradingEngine.evaluateWashedArabicaGrade(sample)
        assertFalse(result.moistureWithinStandardRange)
        assertTrue(result.evaluationNotes.any { it.contains("NOT_MEASURED") })
    }

    @Test
    fun givenNoMoistureMeasurementAndNoDefects_whenGrading_thenClassificationRemainsUndetermined() {
        val sample = GreenLotSample(
            lotId = "lot_pending_review",
            lotName = "",
            species = GreenCoffeeSpecies.COFFEA_ARABICA,
            processMethod = GreenProcessingMethod.WASHED,
            defects = emptyList(),
            moisture = MoistureAssessment(moisturePercent = null)
        )

        val result = StandardGreenGradingEngine.evaluateWashedArabicaGrade(sample)

        assertEquals(PhysicalGradeClassification.UNDETERMINED_OUT_OF_SCOPE, result.grade)
        assertFalse(result.isSpecialtyPhysicalEligible)
        assertTrue(result.evaluationNotes.any { it.contains("NOT_MEASURED") })
        assertTrue(result.evaluationNotes.any { it.contains("classification", ignoreCase = true) })
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
            defectInspectionComplete = true,
            // The completed inspection found no physical defects.
            defects = emptyList(),
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

        val waterActivity = WaterActivityAssessment(waterActivityAw = 0.580)
        assertEquals(0.580, waterActivity.waterActivityAw!!, 0.001)
        assertThrows(IllegalArgumentException::class.java) {
            WaterActivityAssessment(waterActivityAw = null, isMeasured = true)
        }
    }

    @Test
    fun testEpistemologicalDecouplingPhysicalVsSensory() {
        // S4-048: Physical grade and sensory score stay separate without verified mapping.
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

        // Any entered score remains separate from the historical physical reference.
        val dualLowSensory = StandardGreenGradingEngine.evaluateDualSpecialty(physicalGrade1, 76.0)
        assertFalse(dualLowSensory.meetsCombinedConfiguredThresholds)
        assertTrue(dualLowSensory.guidanceMessage.contains("remain separate"))

        // Case 2: Physical Grade 2 (disqualified) but high sensory score (87.0) -> Cannot override physical grade
        val physicalGrade2 = physicalGrade1.copy(
            grade = PhysicalGradeClassification.PREMIUM_GRADE_2,
            isSpecialtyPhysicalEligible = false,
            totalCategory1Equivalents = 1.0
        )
        val dualHighSensory = StandardGreenGradingEngine.evaluateDualSpecialty(physicalGrade2, 87.0)
        assertFalse(dualHighSensory.meetsCombinedConfiguredThresholds)
        assertTrue(dualHighSensory.guidanceMessage.contains("No approved protocol mapping"))

        // A high numeric score cannot create a combined specialty credential.
        val dualPass = StandardGreenGradingEngine.evaluateDualSpecialty(physicalGrade1, 86.5)
        assertFalse(dualPass.meetsCombinedConfiguredThresholds)
        assertFalse(dualPass.isSensorySpecialtyEligible)
    }

    @Test
    fun testBuyerSpecificationContractPassFail() {
        // S4-049: Buyer contract evaluation
        val limits = BuyerAcceptanceLimits(
            contractName = "Buyer Premium Spec 2026",
            sourceReference = "Test buyer specification",
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
            sampleMassGrams = 350.0,
            defectInspectionComplete = true,
            // Three partial black beans equal one full defect.
            defects = listOf(DefectTallyRecord(GreenDefectType.PARTIAL_BLACK, 3)),
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
    fun givenBuyerLimitsWithoutCompletedDefectInspection_whenEvaluated_thenContractDoesNotPass() {
        val limits = BuyerAcceptanceLimits(
            contractName = "Buyer spec",
            sourceReference = "user-provided document page 2",
            maxCategory1FullDefects = 0,
            maxTotalEquivalentDefects = 5.0,
            minMoisturePercent = 10.0,
            maxMoisturePercent = 12.0,
            maxWaterActivityAw = 0.62,
            minScreen16PlusPercent = 85.0,
            minBulkDensityGramsPerLiter = 680.0
        )
        val sample = GreenLotSample(
            lotId = "lot_pending_inspection",
            lotName = "",
            species = GreenCoffeeSpecies.COFFEA_ARABICA,
            processMethod = GreenProcessingMethod.WASHED,
            sampleMassGrams = 350.0,
            defectInspectionComplete = false,
            moisture = MoistureAssessment(11.0),
            waterActivity = WaterActivityAssessment(0.55),
            bulkDensity = BulkDensityAssessment(700.0)
        )
        val sieve = StandardGreenGradingEngine.calculateSieveAnalysis(
            350.0,
            mapOf(ScreenSieveNumber.SCREEN_18 to 350.0)
        )

        val result = StandardGreenGradingEngine.evaluateBuyerContract(sample, limits, sieve)

        assertFalse(result.isAccepted)
        assertTrue(result.failureReasons.any { it.contains("NOT_MEASURED") })
    }

    @Test
    fun givenInconsistentBuyerMoistureLimits_whenCreated_thenValidationRejectsThem() {
        assertThrows(IllegalArgumentException::class.java) {
            BuyerAcceptanceLimits(
                contractName = "Buyer spec",
                sourceReference = "user-provided document",
                maxCategory1FullDefects = 0,
                maxTotalEquivalentDefects = 5.0,
                minMoisturePercent = 13.0,
                maxMoisturePercent = 10.0,
                maxWaterActivityAw = 0.62,
                minScreen16PlusPercent = 85.0,
                minBulkDensityGramsPerLiter = 680.0
            )
        }
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
            defectInspectionComplete = true,
            moisture = MoistureAssessment(moisturePercent = 10.8),
            waterActivity = WaterActivityAssessment(waterActivityAw = 0.585),
            bulkDensity = BulkDensityAssessment(bulkDensityGramsPerLiter = 710.0),
            odor = GreenBeanOdor.CLEAN_FRESH_VEGETAL
        )
        val evaluation = StandardGreenGradingEngine.evaluateWashedArabicaGrade(sample)
        val sieve = StandardGreenGradingEngine.calculateSieveAnalysis(
            350.0,
            mapOf(ScreenSieveNumber.SCREEN_18 to 350.0)
        )
        val audit = GreenQCAuditRecord(
            assessorName = "Q-Grader Marcus",
            assessorId = "ASSESSOR-4092",
            evaluationTimestampEpochMs = 1792185600000L
        )

        val json = StandardGreenGradingEngine.generateGreenQcJson(sample, evaluation, audit, sieve)
        assertTrue(json.contains("\"lot_id\": \"LOT-2026-ETH-YIRG-042\""))
        assertTrue(json.contains("\"sha256_audit_seal\":"))
        assertNotNull(json)

        val csv = StandardGreenGradingEngine.generateGreenQcCsv(sample, evaluation, audit)
        assertTrue(csv.contains("Lot Identifier,\"LOT-2026-ETH-YIRG-042\",N/A,RECORDED"))
        assertTrue(csv.contains("Physical Grade,\"Historical reference: Grade 1\",N/A,HISTORICAL_REFERENCE_ONLY"))
        assertTrue(json.contains("\"official_certification\": false"))
    }

    @Test
    fun givenQuotedLotMetadata_whenExportingJson_thenStringsAreEscapedBeforeHashing() {
        val sample = GreenLotSample(
            lotId = "LOT-\"A\"",
            lotName = "Line 1\nLine 2",
            species = GreenCoffeeSpecies.COFFEA_ARABICA,
            processMethod = GreenProcessingMethod.WASHED,
            producer = "Producer \\ Name"
        )
        val evaluation = StandardGreenGradingEngine.evaluateWashedArabicaGrade(sample)
        val audit = GreenQCAuditRecord(evaluationTimestampEpochMs = 1L)

        val json = StandardGreenGradingEngine.generateGreenQcJson(sample, evaluation, audit)

        assertTrue(json.contains("\"lot_id\": \"LOT-\\\"A\\\"\""))
        assertTrue(json.contains("\"lot_name\": \"Line 1\\nLine 2\""))
        assertTrue(json.contains("\"producer\": \"Producer \\\\ Name\""))
        val seal = json.substringAfter("\"sha256_audit_seal\": \"").substringBefore('"')
        assertEquals(64, seal.length)
        assertTrue(seal.all { it in '0'..'9' || it in 'a'..'f' })
        val unsignedPayload = json.substringBefore(",\n  \"sha256_audit_seal\":") + "\n}"
        assertEquals(StandardGreenGradingEngine.computeSha256(unsignedPayload), seal)
    }

    @Test
    fun givenTraceableQcRecord_whenExported_thenIncludesEvidenceAndProvenance() {
        val sample = GreenLotSample(
            lotId = "LOT-TRACE-01",
            lotName = "Traceable Sample",
            species = GreenCoffeeSpecies.COFFEA_ARABICA,
            processMethod = GreenProcessingMethod.WASHED,
            sampleMassGrams = 350.0,
            defectInspectionComplete = true,
            chainOfCustody = ChainOfCustody(
                exporter = "Exporter A",
                importer = "Importer B",
                warehouseLocation = "Warehouse C"
            ),
            defects = listOf(DefectTallyRecord(GreenDefectType.FULL_BLACK, rawCount = 1)),
            moisture = MoistureAssessment(
                moisturePercent = 11.2,
                uncertaintyPercent = 0.2,
                meterDeviceModel = "NIR-1",
                meterSerialNumber = "SERIAL-9",
                calibrationDateEpochMs = 1_790_000_000_000,
                measurementMethod = MoistureMeasurementMethod.NEAR_INFRARED
            ),
            waterActivity = WaterActivityAssessment(waterActivityAw = 0.55),
            bulkDensity = BulkDensityAssessment(bulkDensityGramsPerLiter = 700.0),
            odor = GreenBeanOdor.CLEAN_FRESH_VEGETAL
        )
        val evaluation = StandardGreenGradingEngine.evaluateWashedArabicaGrade(sample)
        val sieve = StandardGreenGradingEngine.calculateSieveAnalysis(
            350.0,
            mapOf(ScreenSieveNumber.SCREEN_18 to 350.0)
        )
        val audit = GreenQCAuditRecord(
            assessorName = "Assessor A",
            assessorId = "ID-1",
            evaluationTimestampEpochMs = 1_790_000_000_000,
            evidencePhotoUris = listOf("content://provider/document/evidence-1"),
            standardVersion = "Historical 2004 reference (unverified)",
            equipmentCalibrationSummary = "NIR-1 calibration record 2026-10-18"
        )

        val json = StandardGreenGradingEngine.generateGreenQcJson(sample, evaluation, audit, sieve)

        assertTrue(json.contains("\"standard_version\": \"Historical 2004 reference (unverified)\""))
        assertTrue(json.contains("\"defect_records\""))
        assertTrue(json.contains("\"raw_count\": 1"))
        assertTrue(json.contains("\"chain_of_custody\""))
        assertTrue(json.contains("\"exporter\": \"Exporter A\""))
        assertTrue(json.contains("\"evidence_photo_uri_references\""))
        assertTrue(json.contains("content://provider/document/evidence-1"))
        assertTrue(json.contains("NIR-1 calibration record 2026-10-18"))
        assertTrue(json.contains("\"mass_balance_valid\": true"))
        assertTrue(json.contains("\"sieve\": \"SCREEN_18\""))

        val csv = StandardGreenGradingEngine.generateGreenQcCsv(sample, evaluation, audit, sieve)
        assertTrue(csv.contains("Defect FULL_BLACK,\"1\",beans,SUPPORTED"))
        assertTrue(csv.contains("Evidence Photo URI,\"content://provider/document/evidence-1\",content_uri,REFERENCE_ONLY"))
        assertTrue(csv.contains("Sieve Mass Balance,\"0.0\",g,VALID"))
    }

    @Test
    fun givenMissingRequiredObservation_whenGrading_thenNoDefinitiveClassificationIsReturned() {
        val measuredSample = GreenLotSample(
            lotId = "lot_complete",
            lotName = "",
            species = GreenCoffeeSpecies.COFFEA_ARABICA,
            processMethod = GreenProcessingMethod.WASHED,
            sampleMassGrams = 350.0,
            defectInspectionComplete = true,
            defects = emptyList(),
            moisture = MoistureAssessment(moisturePercent = 11.0),
            odor = GreenBeanOdor.CLEAN_FRESH_VEGETAL
        )

        val incompleteSamples = listOf(
            measuredSample.copy(sampleMassGrams = 0.0),
            measuredSample.copy(defectInspectionComplete = false),
            measuredSample.copy(odor = null)
        )

        incompleteSamples.forEach { sample ->
            val evaluation = StandardGreenGradingEngine.evaluateWashedArabicaGrade(sample)
            assertEquals(PhysicalGradeClassification.UNDETERMINED_OUT_OF_SCOPE, evaluation.grade)
            assertFalse(evaluation.isSpecialtyPhysicalEligible)
        }
    }

    @Test
    fun givenUnselectedSpeciesOrProcess_whenGrading_thenResultStaysOutOfScope() {
        val sample = GreenLotSample(
            lotId = "lot_unselected",
            lotName = "",
            species = null,
            processMethod = null,
            sampleMassGrams = 350.0,
            defectInspectionComplete = true,
            moisture = MoistureAssessment(11.0),
            odor = GreenBeanOdor.CLEAN_FRESH_VEGETAL
        )

        val result = StandardGreenGradingEngine.evaluateWashedArabicaGrade(sample)

        assertEquals(PhysicalGradeClassification.UNDETERMINED_OUT_OF_SCOPE, result.grade)
        assertTrue(result.evaluationNotes.any { it.contains("UNKNOWN") })
    }

    @Test
    fun givenNonFiniteSensoryScore_whenCombiningAssessments_thenNoSpecialtyClaimIsProduced() {
        val physical = PhysicalGradingEvaluation(
            standardApplied = "reference",
            sampleMassGrams = 350.0,
            totalCategory1Equivalents = 0.0,
            totalCategory2Equivalents = 0.0,
            totalFullDefectsEquivalents = 0.0,
            grade = PhysicalGradeClassification.SPECIALTY_GRADE_1,
            isSpecialtyPhysicalEligible = true,
            moistureWithinStandardRange = true,
            odorClean = true,
            evaluationNotes = emptyList()
        )

        val result = StandardGreenGradingEngine.evaluateDualSpecialty(physical, Double.POSITIVE_INFINITY)

        assertFalse(result.meetsCombinedConfiguredThresholds)
        assertFalse(result.isSensorySpecialtyEligible)
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
