package com.coffeecreator.app.core.sensory

import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ActiveSensoryCuppingTest {

    // S2-051: Hierarchical Searchable Flavor Wheel & Accessible List
    @Test
    fun testFlavorTaxonomySearchAndAccessibility() {
        val taxonomy = SensoryTaxonomyRegistry.DEFAULT_TAXONOMY
        assertEquals("1.0-ORIGINAL-COFFEE-CREATOR", taxonomy.version)
        assertEquals(9, taxonomy.categories.size)

        // Search for "Grapefruit"
        val results = SensoryTaxonomyRegistry.searchDescriptors("Grapefruit", taxonomy)
        assertTrue(results.any { it.id == "desc_grapefruit" })
        val grapefruit = results.first { it.id == "desc_grapefruit" }
        assertEquals("desc_grapefruit", grapefruit.id)
        assertEquals("cat_fruity", grapefruit.categoryId)
        assertEquals("sub_citrus", grapefruit.subcategoryId)
        assertEquals("#FB8C00", grapefruit.colorHex)
        assertTrue(grapefruit.accessibilityDescription.contains("Grapefruit: vibrant citrus"))

        // Search for synonyms like "vinous"
        val vinousResults = SensoryTaxonomyRegistry.searchDescriptors("vinous", taxonomy)
        assertTrue(vinousResults.any { it.name == "Winey" || it.synonyms.contains("vinous") })

        // Accessible linear list
        val allDescriptors = SensoryTaxonomyRegistry.getAllDescriptors(taxonomy)
        assertTrue(allDescriptors.size >= 25)
        allDescriptors.forEach { desc ->
            assertFalse(desc.accessibilityDescription.isBlank())
            assertFalse(desc.colorHex.isBlank())
        }
    }

    // S2-052: Descriptor Definitions & Safe Food-Grade Calibration References
    @Test
    fun testCalibrationReferencesAndSafetyBoundaries() {
        val exercises = SensoryTaxonomyRegistry.STANDARD_CALIBRATION_EXERCISES
        assertEquals(5, exercises.size)

        // Citric Acid recipe for 1000 mL
        val citric = exercises.first { it.id == "cal_citric_lemon" }
        assertEquals(ChemicalGrade.FOOD_GRADE_FCC, citric.requiredGrade)
        assertEquals(BigDecimal("0.50"), citric.targetConcentrationGPerL)
        val citricDose1000ml = SensoryTaxonomyRegistry.calculateCalibrationBatch(citric, BigDecimal("1000"))
        assertEquals(BigDecimal("0.5000"), citricDose1000ml)

        // Citric Acid recipe for 500 mL
        val citricDose500ml = SensoryTaxonomyRegistry.calculateCalibrationBatch(citric, BigDecimal("500"))
        assertEquals(BigDecimal("0.2500"), citricDose500ml)

        // Quinine Bitterness safety limit
        val quinine = exercises.first { it.id == "cal_quinine_bitter" }
        assertEquals(ChemicalGrade.PHARMACEUTICAL_USP, quinine.requiredGrade)
        assertEquals(BigDecimal("0.005"), quinine.targetConcentrationGPerL)
        assertEquals(50, quinine.maxDailyConsumptionLimitMl)
        assertTrue(quinine.safetyWarning.contains("Contraindicated during pregnancy"))
    }

    // S2-053: Standard Sensory Sliders & Score Aggregation with Defect Deductions
    @Test
    fun testSensoryScoreAggregationAndDefectDeduction() {
        val scores = mapOf(
            "fragrance_aroma" to BigDecimal("8.50"),
            "flavor" to BigDecimal("8.75"),
            "aftertaste" to BigDecimal("8.25"),
            "acidity" to BigDecimal("8.50"),
            "body" to BigDecimal("8.25"),
            "balance" to BigDecimal("8.50"),
            "uniformity" to BigDecimal("10.00"),
            "clean_cup" to BigDecimal("10.00"),
            "sweetness" to BigDecimal("10.00"),
            "overall" to BigDecimal("8.50")
        )
        // Sum without defect = 89.25
        val totalNoDefect = CuppingAnalyticsEngine.calculateTotalCuppingScore(
            CuppingTemplateType.SPECIALTY_SCA_EQUIVALENT,
            scores
        )
        assertEquals(BigDecimal("89.25"), totalNoDefect)

        // With 1 taint cup (-2) and 1 fault cup (-4) = deduction of 6
        val defects = DefectRecord(taintCupCount = 1, faultCupCount = 1, defectDescription = "Phenolic rubber note")
        val totalWithDefect = CuppingAnalyticsEngine.calculateTotalCuppingScore(
            CuppingTemplateType.SPECIALTY_SCA_EQUIVALENT,
            scores,
            defects
        )
        assertEquals(BigDecimal("83.25"), totalWithDefect)
    }

    // S2-054: Epistemological Origin Distinction
    @Test
    fun testEpistemologicalProvenanceBadges() {
        val humanBadge = ProvenanceBadge(
            origin = EpistemologicalOrigin.HUMAN_OBSERVATION,
            authorOrModelId = "lead_cupper_01",
            methodDisclaimer = "",
            timestamp = System.currentTimeMillis()
        )
        assertEquals(EpistemologicalOrigin.HUMAN_OBSERVATION, humanBadge.origin)
        assertTrue(humanBadge.methodDisclaimer.isEmpty())

        val aiBadge = ProvenanceBadge(
            origin = EpistemologicalOrigin.AI_HEURISTIC_HYPOTHESIS,
            authorOrModelId = "COFFEE_REST_KINETICS_V1",
            confidenceLower = BigDecimal("10"),
            confidenceUpper = BigDecimal("24"),
            confidenceLevelPercent = 85,
            methodDisclaimer = "Heuristic estimate; validate by empirical observation.",
            timestamp = System.currentTimeMillis()
        )
        assertEquals(EpistemologicalOrigin.AI_HEURISTIC_HYPOTHESIS, aiBadge.origin)
        assertTrue(aiBadge.methodDisclaimer.contains("Heuristic estimate"))
        assertEquals(85, aiBadge.confidenceLevelPercent)
    }

    // S2-055: Cupping Sheet Templates
    @Test
    fun testCuppingSheetTemplates() {
        val specialty = StandardCuppingTemplates.getTemplate(CuppingTemplateType.SPECIALTY_SCA_EQUIVALENT)
        assertEquals(10, specialty.attributes.size)
        assertEquals(SensoryScaleType.Q_SCALE_6_TO_10, specialty.attributes.first().scaleType)

        val dialIn = StandardCuppingTemplates.getTemplate(CuppingTemplateType.QUICK_BARISTA_DIAL_IN)
        assertEquals(6, dialIn.attributes.size)
        assertEquals(SensoryScaleType.ORDINAL_1_TO_10, dialIn.attributes.first().scaleType)

        val espresso = StandardCuppingTemplates.getTemplate(CuppingTemplateType.ESPRESSO_SENSORY)
        assertEquals(6, espresso.attributes.size)
        assertTrue(espresso.attributes.any { it.key == "crema_texture" })
    }

    // S2-056, S2-057, S2-058: Session Management, Masking, and Lifecycle Audit Trail
    @Test
    fun testCuppingSessionLifecycleAndBlindMasking() {
        val evaluators = listOf(
            Evaluator("eval_1", "Marcus (Lead Cupper)", EvaluatorRole.LEAD_CUPPER),
            Evaluator("eval_2", "Sarah (Q-Grader)", EvaluatorRole.Q_GRADER)
        )
        val sampleA = CuppingSample(
            sampleId = "sample_001",
            blindCode = "Sample #A1",
            actualBeanId = "bean_ethiopia_01",
            beanName = "Yirgacheffe Gedeb",
            roaster = "Onyx Coffee Lab",
            origin = "Ethiopia",
            process = "Washed",
            roastDateEpoch = System.currentTimeMillis() - 86400000L * 12,
            tablePosition = 1,
            isRevealed = false
        )

        val session = CuppingSession(
            id = "session_s2_058",
            title = "Origin Evaluation",
            location = "QC Lab",
            template = StandardCuppingTemplates.SPECIALTY_STANDARD,
            maskingMode = MaskingMode.DOUBLE_BLIND_PANEL,
            status = SessionStatus.DRAFT,
            evaluators = evaluators,
            samples = listOf(sampleA),
            sheets = emptyList(),
            auditTrail = listOf(
                AuditEntry(
                    timestamp = System.currentTimeMillis(),
                    editorId = "eval_1",
                    action = "CREATE_SESSION",
                    previousStateSummary = "NONE",
                    newStateSummary = "DRAFT",
                    reason = "Initiate session"
                )
            ),
            createdAt = System.currentTimeMillis()
        )

        assertEquals(SessionStatus.DRAFT, session.status)
        assertFalse(session.samples.first().isRevealed)
        assertEquals("Sample #A1", session.samples.first().blindCode)

        // Reveal sample
        val revealedSample = session.samples.first().copy(isRevealed = true)
        val openedSession = session.copy(
            status = SessionStatus.OPEN,
            samples = listOf(revealedSample),
            auditTrail = session.auditTrail + AuditEntry(
                timestamp = System.currentTimeMillis(),
                editorId = "eval_1",
                action = "REVEAL_AND_OPEN",
                previousStateSummary = "DRAFT",
                newStateSummary = "OPEN",
                reason = "Blind evaluation complete, reveal bean identities"
            )
        )

        assertEquals(SessionStatus.OPEN, openedSession.status)
        assertTrue(openedSession.samples.first().isRevealed)
        assertEquals(2, openedSession.auditTrail.size)
    }

    // S2-059: Multi-Evaluator Consensus, IRAI, Outliers, and Jaccard Overlap
    @Test
    fun testMultiEvaluatorConsensusAndAgreementMetrics() {
        val sheets = listOf(
            EvaluationSheet(
                id = "sheet_1",
                sessionId = "sess_01",
                sampleId = "sample_001",
                evaluatorId = "eval_1",
                attributeScores = mapOf("flavor" to BigDecimal("8.50"), "acidity" to BigDecimal("8.25")),
                descriptors = listOf("Jasmine", "Bergamot", "Peach"),
                totalScore = BigDecimal("85.00"),
                submittedAt = System.currentTimeMillis()
            ),
            EvaluationSheet(
                id = "sheet_2",
                sessionId = "sess_01",
                sampleId = "sample_001",
                evaluatorId = "eval_2",
                attributeScores = mapOf("flavor" to BigDecimal("8.75"), "acidity" to BigDecimal("8.50")),
                descriptors = listOf("Jasmine", "Peach", "Honey"),
                totalScore = BigDecimal("86.50"),
                submittedAt = System.currentTimeMillis()
            ),
            EvaluationSheet(
                id = "sheet_3",
                sessionId = "sess_01",
                sampleId = "sample_001",
                evaluatorId = "eval_3",
                attributeScores = mapOf("flavor" to BigDecimal("8.50"), "acidity" to BigDecimal("8.50")),
                descriptors = listOf("Jasmine", "Blackberry"),
                totalScore = BigDecimal("85.50"),
                submittedAt = System.currentTimeMillis()
            )
        )

        val session = CuppingSession(
            id = "sess_01",
            title = "Consensus Test",
            location = "Cupping Lab",
            template = StandardCuppingTemplates.SPECIALTY_STANDARD,
            maskingMode = MaskingMode.SINGLE_BLIND_EVALUATOR,
            status = SessionStatus.CLOSED,
            evaluators = listOf(
                Evaluator("eval_1", "Marcus", EvaluatorRole.LEAD_CUPPER),
                Evaluator("eval_2", "Sarah", EvaluatorRole.Q_GRADER),
                Evaluator("eval_3", "Alex", EvaluatorRole.BARISTA)
            ),
            samples = listOf(
                CuppingSample("sample_001", "Sample #A1", "b1", "Geisha", "Hacienda", "Panama", "Washed", 0L, 1, true)
            ),
            sheets = sheets,
            auditTrail = emptyList(),
            createdAt = System.currentTimeMillis()
        )

        val consensus = CuppingAnalyticsEngine.computeConsensus(session, "sample_001")
        assertNotNull(consensus)
        // Mean score = (85.00 + 86.50 + 85.50) / 3 = 85.67
        assertEquals(BigDecimal("85.67"), consensus.finalConsensusScore)

        // Flavor attribute consensus: (8.50 + 8.75 + 8.50) / 3 = 8.58, median = 8.50
        val flavorConsensus = consensus.attributeConsensus["flavor"]
        assertNotNull(flavorConsensus)
        assertEquals(BigDecimal("8.58"), flavorConsensus!!.mean)
        assertEquals(BigDecimal("8.50"), flavorConsensus.median)
        assertTrue(flavorConsensus.agreementIndex >= BigDecimal("0.70"))

        // Descriptor cloud: "Jasmine" picked by all 3 (100%), "Peach" picked by 2 (67%)
        val jasmineConcordance = consensus.descriptorCloud.first { it.descriptor == "Jasmine" }
        assertEquals(3, jasmineConcordance.selectionCount)
        assertEquals(BigDecimal("1.00"), jasmineConcordance.selectionFrequency)
        assertTrue(jasmineConcordance.isConsensus)

        // Jaccard similarity between eval_1 and eval_2:
        // eval_1 = {Jasmine, Bergamot, Peach}, eval_2 = {Jasmine, Peach, Honey}
        // Intersect = {Jasmine, Peach} (2), Union = {Jasmine, Bergamot, Peach, Honey} (4) => 2/4 = 0.50
        val overlap12 = consensus.overlapMatrix.first {
            (it.evaluatorAId == "eval_1" && it.evaluatorBId == "eval_2") ||
                (it.evaluatorAId == "eval_2" && it.evaluatorBId == "eval_1")
        }
        assertEquals(BigDecimal("0.50"), overlap12.jaccardSimilarity)
    }

    // S2-060: Offline QC Report & Cryptographic Receipt
    @Test
    fun testOfflineQcReportAndReceipt() {
        val sample = CuppingSample("s1", "Sample #X1", "b1", "Bourbon", "Daterra", "Brazil", "Natural", 0L, 1, true)
        val sheet = EvaluationSheet(
            id = "sh1",
            sessionId = "sess_qc",
            sampleId = "s1",
            evaluatorId = "ev1",
            attributeScores = mapOf("flavor" to BigDecimal("8.00")),
            descriptors = listOf("Chocolate"),
            totalScore = BigDecimal("84.00"),
            submittedAt = System.currentTimeMillis()
        )
        val session = CuppingSession(
            id = "sess_qc",
            title = "QC Session",
            location = "Lab",
            template = StandardCuppingTemplates.SPECIALTY_STANDARD,
            maskingMode = MaskingMode.OPEN_LABEL,
            status = SessionStatus.CLOSED,
            evaluators = listOf(Evaluator("ev1", "QC Manager", EvaluatorRole.LEAD_CUPPER)),
            samples = listOf(sample),
            sheets = listOf(sheet),
            auditTrail = emptyList(),
            createdAt = System.currentTimeMillis()
        )

        val receipt = CuppingAnalyticsEngine.generateOfflineReport(session)
        assertNotNull(receipt)
        assertEquals("sess_qc", receipt.sessionId)
        assertFalse(receipt.sha256Fingerprint.isBlank())
        assertEquals(64, receipt.sha256Fingerprint.length) // SHA-256 hex string length
        assertTrue(receipt.markdownReport.contains("Cupping Quality Control Report"))
        assertTrue(receipt.csvData.contains("consensus_score"))
    }

    // S2-061: Physical Green & Roasted Grading
    @Test
    fun testPhysicalGreenAndRoastedGrading() {
        val defects = listOf(
            // 0 primary.
            GreenCoffeeDefectEntry(GreenDefectType.FULL_BLACK, 0),
            // 6/3 = 2 secondary.
            GreenCoffeeDefectEntry(GreenDefectType.PARTIAL_BLACK, 6),
            // 5/5 = 1 secondary.
            GreenCoffeeDefectEntry(GreenDefectType.BROKEN_CHIPPED, 5)
        )
        val (grade, defectCounts) = CuppingAnalyticsEngine.gradeGreenPhysicalLot(
            moisturePercentage = BigDecimal("11.2"),
            waterActivityAw = BigDecimal("0.58"),
            defects = defects
        )
        val (primary, secondary) = defectCounts
        assertEquals(0, primary)
        assertEquals(3, secondary)
        assertEquals("Specialty Grade 1 (Strict)", grade)

        // Agtron roast delta
        val wholeBean = BigDecimal("65.0")
        val ground = BigDecimal("72.5")
        val delta = ground.subtract(wholeBean)
        assertEquals(BigDecimal("7.5"), delta)
    }

    // S2-062: Roast-Rest Degassing Kinetics & Observations
    @Test
    fun testRoastRestDegassingKineticsAndObservations() {
        val prediction = CuppingAnalyticsEngine.predictRoastRestCurve("bag_001", "LIGHT", "WASHED")
        assertEquals(10, prediction.estimatedPeakWindowStartDay)
        assertEquals(24, prediction.estimatedPeakWindowEndDay)
        assertEquals(45, prediction.expectedStalingDay)

        // Classify observation at Day 3 (Too fresh / degassing)
        val phaseDay3 = CuppingAnalyticsEngine.classifyObservationPhase(3, prediction)
        assertEquals(DegassingPhase.TOO_FRESH_CO2_INTERFERENCE, phaseDay3)

        // Classify observation at Day 14 (Peak maturity)
        val phaseDay14 = CuppingAnalyticsEngine.classifyObservationPhase(14, prediction)
        assertEquals(DegassingPhase.PEAK_FLAVOR_WINDOW, phaseDay14)

        // Classify observation at Day 35 (Stable post peak)
        val phaseDay35 = CuppingAnalyticsEngine.classifyObservationPhase(35, prediction)
        assertEquals(DegassingPhase.STABLE_POST_PEAK, phaseDay35)

        // Classify observation at Day 50 (Stale)
        val phaseDay50 = CuppingAnalyticsEngine.classifyObservationPhase(50, prediction)
        assertEquals(DegassingPhase.STALE_OXIDIZED, phaseDay50)
    }

    // Comprehensive Taxonomy Expansion & Defect QC Verification (S2-051, S2-052)
    @Test
    fun testExpandedWheelTaxonomyAndDefects() {
        val taxonomy = SensoryTaxonomyRegistry.DEFAULT_TAXONOMY
        assertEquals(9, taxonomy.categories.size)

        val defectCategory = SensoryTaxonomyRegistry.findCategoryById("cat_other", taxonomy)
            ?: SensoryTaxonomyRegistry.findCategoryById("cat_defects_taints", taxonomy)
        assertNotNull(defectCategory)
        assertTrue(defectCategory!!.name.contains("Defect", ignoreCase = true) || defectCategory.name.contains("Other", ignoreCase = true))
        assertTrue(defectCategory.subcategories.size >= 4)

        // Verify potato defect exists and is flagged as defect
        val potato = SensoryTaxonomyRegistry.findDescriptorByName("Potato Defect", taxonomy)
        assertNotNull(potato)
        assertTrue(potato!!.isDefect)
        assertEquals("sub_off_notes", potato.subcategoryId)

        // Verify Indonesian specialty descriptors
        val gulaAren = SensoryTaxonomyRegistry.findDescriptorByName("Palm Sugar / Gula Aren", taxonomy)
        assertNotNull(gulaAren)
        assertEquals("cat_sweet", gulaAren!!.categoryId)

        val pandan = SensoryTaxonomyRegistry.findDescriptorByName("Pandan", taxonomy)
            ?: SensoryTaxonomyRegistry.findDescriptorByName("Pandan Leaf / Daun Pandan", taxonomy)
        assertNotNull(pandan)
        assertTrue(pandan!!.categoryId == "cat_spices" || pandan.categoryId == "cat_green")

        // Total descriptors count
        val allDescriptors = SensoryTaxonomyRegistry.getAllDescriptors(taxonomy)
        assertTrue(allDescriptors.size >= 85)

        // Category & subcategory lookups
        val fruityDescriptors = SensoryTaxonomyRegistry.getDescriptorsForCategory("cat_fruity", taxonomy)
        assertTrue(fruityDescriptors.size >= 15)

        val berryDescriptors = SensoryTaxonomyRegistry.getDescriptorsForSubcategory("sub_berry", taxonomy)
        assertEquals(4, berryDescriptors.size)
    }

    // Dynamic Cupping Template Scoring Math across all 4 templates (S2-053, S2-055)
    @Test
    fun testAllFourCuppingTemplatesScoringMath() {
        // 1. Espresso Sensory (6 attributes, 1-10 scale, average expected)
        val espressoScores = mapOf(
            "crema_texture" to BigDecimal("8.00"),
            "aroma_intensity" to BigDecimal("8.50"),
            "acidity_structure" to BigDecimal("8.00"),
            "sweetness_core" to BigDecimal("9.00"),
            "viscosity_mouthfeel" to BigDecimal("8.50"),
            "finish_lingering" to BigDecimal("8.00")
        )
        // Average = 50.0 / 6 = 8.33
        val espressoTotal = CuppingAnalyticsEngine.calculateTotalCuppingScore(
            CuppingTemplateType.ESPRESSO_SENSORY,
            espressoScores
        )
        assertEquals(BigDecimal("8.33"), espressoTotal)

        // 2. Production QC (4 attributes, 1-10 scale, average expected)
        val qcScores = mapOf(
            "profile_match" to BigDecimal("9.00"),
            "clean_cup" to BigDecimal("9.50"),
            "roast_development" to BigDecimal("8.50"),
            "overall_commercial" to BigDecimal("9.00")
        )
        // Average = 36.0 / 4 = 9.00
        val qcTotal = CuppingAnalyticsEngine.calculateTotalCuppingScore(
            CuppingTemplateType.PRODUCTION_QC,
            qcScores
        )
        assertEquals(BigDecimal("9.00"), qcTotal)

        // 3. Quick Barista Dial-In (6 attributes, 1-10 scale)
        val dialInScores = mapOf(
            "sweetness" to BigDecimal("8.00"),
            "acidity" to BigDecimal("7.50"),
            "bitterness" to BigDecimal("8.00"),
            "body" to BigDecimal("8.00"),
            "balance" to BigDecimal("8.50"),
            "aftertaste" to BigDecimal("8.00")
        )
        // Average = 48.0 / 6 = 8.00
        val dialInTotal = CuppingAnalyticsEngine.calculateTotalCuppingScore(
            CuppingTemplateType.QUICK_BARISTA_DIAL_IN,
            dialInScores
        )
        assertEquals(BigDecimal("8.00"), dialInTotal)
    }

    // S2-051: Complete 221-Descriptor Taxonomy, 84 Official, 137 Extensions
    @Test
    fun testComplete221DescriptorTaxonomyCountsAndBilingualLookup() {
        val taxonomy = SensoryTaxonomyRegistry.DEFAULT_TAXONOMY
        assertEquals(9, taxonomy.categories.size)
        assertEquals(221, taxonomy.totalDescriptorCount)
        assertEquals(84, taxonomy.officialReferenceCount)
        assertEquals(137, taxonomy.customExtensionCount)

        // Verify official vs extension counts via registry functions
        val officialList = SensoryTaxonomyRegistry.getOfficialDescriptors(taxonomy)
        val expandedList = SensoryTaxonomyRegistry.getExpandedLocalDescriptors(taxonomy)
        assertEquals(84, officialList.size)
        assertEquals(137, expandedList.size)

        // Bilingual lookups (Indonesian label -> English descriptor)
        val blackTea = SensoryTaxonomyRegistry.searchDescriptors("teh hitam", taxonomy)
        assertTrue(blackTea.any { it.name == "Black Tea" })

        val lime = SensoryTaxonomyRegistry.searchDescriptors("jeruk nipis", taxonomy)
        assertTrue(lime.any { it.name == "Lime" })

        val basil = SensoryTaxonomyRegistry.searchDescriptors("kemangi", taxonomy)
        assertTrue(basil.any { it.name == "Basil" })

        val jackfruit = SensoryTaxonomyRegistry.searchDescriptors("nangka", taxonomy)
        assertTrue(jackfruit.any { it.name == "Jackfruit" })

        val salak = SensoryTaxonomyRegistry.searchDescriptors("salak", taxonomy)
        assertTrue(salak.any { it.name == "Salak" })

        val snakeFruit = SensoryTaxonomyRegistry.searchDescriptors("snake fruit", taxonomy)
        assertTrue(snakeFruit.any { it.name == "Salak" })
    }

    // S2-053, S2-055: CVA 16-Attribute Descriptive Protocol Scoring Math
    @Test
    fun testCva16AttributeScoringProtocolMath() {
        val template = StandardCuppingTemplates.CVA_DESCRIPTIVE_16
        assertEquals(CuppingTemplateType.CVA_DESCRIPTIVE_16_ATTRIBUTE, template.type)
        assertEquals(16, template.attributes.size)

        // Score 16 attributes
        val scores = mutableMapOf<String, BigDecimal>()
        scores["fragrance"] = BigDecimal("12.0")
        scores["aroma"] = BigDecimal("12.0")
        scores["flavor"] = BigDecimal("13.0")
        scores["aftertaste"] = BigDecimal("12.5")
        scores["acidity_intensity"] = BigDecimal("11.0")
        scores["acidity_quality"] = BigDecimal("8.5")
        scores["sweetness_intensity"] = BigDecimal("12.0")
        scores["bitterness_intensity"] = BigDecimal("3.0")
        scores["saltiness_intensity"] = BigDecimal("1.0")
        scores["body_intensity"] = BigDecimal("11.5")
        scores["body_texture"] = BigDecimal("8.5")
        scores["astringency"] = BigDecimal("2.0")
        scores["finish_duration_s"] = BigDecimal("35.0")
        scores["temperature_c"] = BigDecimal("65.0")
        scores["defects"] = BigDecimal("0.0")
        scores["overall_impression"] = BigDecimal("8.0")

        val totalNoDefects = CuppingAnalyticsEngine.calculateTotalCuppingScore(
            CuppingTemplateType.CVA_DESCRIPTIVE_16_ATTRIBUTE,
            scores
        )
        assertTrue(totalNoDefects > BigDecimal("50.00") && totalNoDefects <= BigDecimal("100.00"))

        // With 1 taint (-2) and 1 fault (-4) = deduction of 6
        val defects = DefectRecord(taintCupCount = 1, faultCupCount = 1, defectDescription = "Baggy defect")
        val totalWithDefects = CuppingAnalyticsEngine.calculateTotalCuppingScore(
            CuppingTemplateType.CVA_DESCRIPTIVE_16_ATTRIBUTE,
            scores,
            defects
        )
        assertEquals(totalNoDefects.subtract(BigDecimal("6.00")), totalWithDefects)
    }

    // S2-053, S2-056: Multi-Phase Temperature Observations
    @Test
    fun testMultiPhaseTemperatureObservations() {
        val phaseObs = listOf(
            CuppingPhaseObservation(
                phase = CuppingPhase.INITIAL_HOT_CUPPING,
                temperatureCelsius = 70.0,
                descriptorIds = listOf("Jasmine", "Bergamot"),
                perceivedIntensity = 8.5,
                timestamp = System.currentTimeMillis()
            ),
            CuppingPhaseObservation(
                phase = CuppingPhase.WARM_CUPPING,
                temperatureCelsius = 60.0,
                descriptorIds = listOf("Peach", "Apricot"),
                perceivedIntensity = 8.0,
                timestamp = System.currentTimeMillis() + 120_000
            ),
            CuppingPhaseObservation(
                phase = CuppingPhase.COOL_CUPPING,
                temperatureCelsius = 40.0,
                descriptorIds = listOf("Honey", "Black Tea"),
                perceivedIntensity = 9.0,
                timestamp = System.currentTimeMillis() + 300_000
            )
        )

        assertEquals(3, phaseObs.size)
        assertEquals(CuppingPhase.INITIAL_HOT_CUPPING, phaseObs[0].phase)
        assertEquals(CuppingPhase.WARM_CUPPING, phaseObs[1].phase)
        assertEquals(CuppingPhase.COOL_CUPPING, phaseObs[2].phase)
        assertTrue(phaseObs[0].temperatureCelsius!! > phaseObs[1].temperatureCelsius!!)
        assertTrue(phaseObs[1].temperatureCelsius!! > phaseObs[2].temperatureCelsius!!)
    }

    // S2-056, S2-039: 1-Click Cupping Sample to Journal Entry
    @Test
    fun testCuppingSampleToJournalEntryMapping() {
        val sample = CuppingSample(
            sampleId = "sample_test_01",
            blindCode = "Bowl #04",
            actualBeanId = "bean_yirgacheffe",
            beanName = "Ethiopia Yirgacheffe Gedeb",
            roaster = "Onyx Coffee Lab",
            origin = "Gedeb, Yirgacheffe, Ethiopia",
            process = "Washed",
            roastDateEpoch = System.currentTimeMillis() - 864000000L,
            tablePosition = 4,
            isRevealed = true
        )

        val sheet = EvaluationSheet(
            id = "sheet_01",
            sessionId = "session_01",
            sampleId = "sample_test_01",
            evaluatorId = "eval_lead",
            attributeScores = mapOf("flavor" to BigDecimal("8.75"), "acidity" to BigDecimal("9.00")),
            descriptors = listOf("Jasmine", "Peach", "Bergamot"),
            totalScore = BigDecimal("88.50"),
            notes = "Super clean floral finish",
            submittedAt = System.currentTimeMillis()
        )

        val journalEntry = sample.toBrewJournalEntry(sheet = sheet)
        assertEquals("Ethiopia Yirgacheffe Gedeb", journalEntry.beanName)
        assertEquals("Cupping (SCA/CVA)", journalEntry.brewMethod)
        assertEquals(8.9, journalEntry.rating!!, 0.1)
        assertEquals(listOf("Jasmine", "Peach", "Bergamot"), journalEntry.tasteNotes)
        assertTrue(journalEntry.generalNotes.contains("Onyx Coffee Lab"))
        assertTrue(journalEntry.generalNotes.contains("Washed"))
        assertTrue(journalEntry.generalNotes.contains("Super clean floral finish"))
        assertTrue(journalEntry.tags.contains("cupping"))
        assertTrue(journalEntry.tags.contains("revealed"))
    }
}
