package com.coffeecreator.app.core.sensory.operations

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PanelOperationsEngineTest {

    // ========================================================================
    // S4-059: Inter-Rater Statistics (Pearson, Spearman, MCD, Fleiss Kappa)
    // ========================================================================

    @Test
    fun testPearsonCorrelationGoldenCases() {
        // Perfect positive correlation
        val raterA = listOf(6.0, 7.0, 8.0, 9.0)
        val raterB = listOf(6.5, 7.5, 8.5, 9.5)
        val rPos = PanelOperationsEngine.calculatePearson(raterA, raterB)
        assertNotNull(rPos)
        assertEquals(1.0, rPos!!, 0.0001)

        // Perfect negative correlation
        val raterC = listOf(9.0, 8.0, 7.0, 6.0)
        val rNeg = PanelOperationsEngine.calculatePearson(raterA, raterC)
        assertNotNull(rNeg)
        assertEquals(-1.0, rNeg!!, 0.0001)

        // Moderate correlation
        val raterD = listOf(6.0, 8.0, 7.5, 9.0)
        val rMod = PanelOperationsEngine.calculatePearson(raterA, raterD)
        assertNotNull(rMod)
        assertTrue(rMod!! in 0.8..1.0)
    }

    @Test
    fun testPearsonCorrelationZeroVarianceGuardReturnsNull() {
        // Rater A gives flat score 8.0 across all samples (zero variance)
        val raterFlat = listOf(8.0, 8.0, 8.0, 8.0)
        val raterNormal = listOf(6.0, 7.0, 8.0, 9.0)

        val r = PanelOperationsEngine.calculatePearson(raterFlat, raterNormal)
        assertNull("Zero-variance rater must return null rather than divide by zero", r)
    }

    @Test
    fun testSpearmanRankCorrelationWithTiedRanks() {
        val scoresX = listOf(10.0, 20.0, 20.0, 30.0)
        val ranksX = PanelOperationsEngine.computeFractionalRanks(scoresX)
        // 10.0 -> rank 1.0; 20.0, 20.0 -> ranks 2 and 3 -> mid-rank (2+3)/2 = 2.5; 30.0 -> rank 4.0
        assertEquals(1.0, ranksX[0], 0.001)
        assertEquals(2.5, ranksX[1], 0.001)
        assertEquals(2.5, ranksX[2], 0.001)
        assertEquals(4.0, ranksX[3], 0.001)

        val scoresY = listOf(15.0, 25.0, 25.0, 35.0)
        val rho = PanelOperationsEngine.calculateSpearman(scoresX, scoresY)
        assertNotNull(rho)
        assertEquals(1.0, rho!!, 0.0001)
    }

    @Test
    fun testMeanConsensusDeviationCalculation() {
        // 3 assessors evaluating 2 samples
        val panelScores = mapOf(
            "cupper_1" to listOf(8.00, 7.50),
            "cupper_2" to listOf(8.25, 7.50),
            "cupper_3" to listOf(8.00, 7.50)
        )
        // Sample 1 mean: (8.00 + 8.25 + 8.00) / 3 = 8.0833
        // Sample 2 mean: (7.50 + 7.50 + 7.50) / 3 = 7.5000
        val mcdMap = PanelOperationsEngine.calculateMeanConsensusDeviation(panelScores)

        assertEquals(3, mcdMap.size)
        // Cupper 2 deviation on sample 1: |8.25 - 8.0833| = 0.1667; sample 2: 0.0. Average: ~0.0833
        assertTrue(mcdMap["cupper_2"]!! < 0.20)
        // All assessors are well below calibrated threshold of 0.35
        for (mcd in mcdMap.values) {
            assertTrue("MCD should be below 0.35 for calibrated panel", mcd <= 0.35)
        }
    }

    @Test
    fun testFleissKappaPerfectConsensusAndChanceCases() {
        // 3 items, 3 categories (Clean, Taint, Fault), 4 raters per item
        // Item 1: all 4 raters say Clean
        // Item 2: all 4 raters say Taint
        // Item 3: all 4 raters say Fault
        val perfectMatrix = arrayOf(
            intArrayOf(4, 0, 0),
            intArrayOf(0, 4, 0),
            intArrayOf(0, 0, 4)
        )
        val kappaPerfect = PanelOperationsEngine.calculateFleissKappa(perfectMatrix)
        assertNotNull(kappaPerfect)
        assertEquals(1.0, kappaPerfect!!, 0.001)
        assertEquals("ALMOST_PERFECT", PanelOperationsEngine.getLandisKochLabel(kappaPerfect))

        // Single rater guard
        val singleRaterMatrix = arrayOf(
            intArrayOf(1, 0, 0)
        )
        assertNull(PanelOperationsEngine.calculateFleissKappa(singleRaterMatrix))
    }

    @Test
    fun givenInvalidRatingMatrix_whenCalculatingFleissKappa_thenReturnUnknown() {
        assertNull(PanelOperationsEngine.calculateFleissKappa(emptyArray()))
        assertNull(PanelOperationsEngine.calculateFleissKappa(arrayOf(intArrayOf(2))))
        assertNull(
            PanelOperationsEngine.calculateFleissKappa(
                arrayOf(
                    intArrayOf(1, 1),
                    intArrayOf(2, 1)
                )
            )
        )
    }

    @Test
    fun testPanelReportGeneration() {
        val scores = mapOf(
            "cupper_maya" to 8.50,
            "cupper_budi" to 8.25,
            "cupper_carlos" to 8.50
        )
        val report = PanelOperationsEngine.generatePanelReport(
            sessionId = "sess_001",
            sampleId = "sample_482",
            attributeKey = "acidity",
            scoresByAssessor = scores
        )
        assertEquals("sess_001", report.sessionId)
        assertEquals("sample_482", report.sampleId)
        assertEquals(3, report.raterCount)
        assertEquals(8.4167, report.consensusMean, 0.001)
        assertTrue(report.panelMeanConsensusDeviation <= 0.35)
        assertTrue(report.isHighCalibration)
    }

    // ========================================================================
    // S4-058: Blind Panel Workflow & Audit Logging
    // ========================================================================

    @Test
    fun testRandom3DigitBlindCodesUniqueAndBalanced() {
        val sampleIds = listOf("bean_eth_01", "bean_col_02", "bean_ken_03", "bean_pan_04")
        val mappings = PanelOperationsEngine.generateBlindCodes(sampleIds, seed = 42L)

        assertEquals(4, mappings.size)
        val codes = mappings.values.map { it.blindCode }
        // Must be unique
        assertEquals(4, codes.distinct().size)
        for (code in codes) {
            assertEquals(3, code.length)
            assertTrue("Code must be numeric 100-999", code.toInt() in 100..999)
        }
        for (m in mappings.values) {
            assertFalse(m.isRevealed)
            assertNull(m.revealedAtEpoch)
        }
    }

    @Test
    fun testPrematureRevealGeneratesAuditRecord() {
        val mapping = BlindSampleMapping(
            blindCode = "742",
            actualCoffeeBeanId = "bean_geisha_washed",
            isRevealed = false
        )
        val host = AssessorProfile(
            pseudonym = "Host Carlos",
            role = SensoryPanelRole.SESSION_HOST
        )
        val unblinded = PanelOperationsEngine.executeReveal(
            mapping = mapping,
            assessor = host,
            reason = "Customer emergency check",
            forcePremature = true
        )
        assertTrue(unblinded.isRevealed)
        assertNotNull(unblinded.revealedAtEpoch)
        assertEquals(host.id, unblinded.revealedByAssessorId)
        assertTrue(unblinded.revealReason!!.startsWith("UNBLINDED_PREMATURELY_AUDITED"))
    }

    // ========================================================================
    // S4-055: Historical Linkage Integrity
    // ========================================================================

    @Test
    fun testLinkagePreservesHistoricalRecipeByteForByte() {
        val linkage = PanelOperationsEngine.createImmutableLinkage(
            assessmentId = "cva_eval_88",
            coffeeBeanId = "bean_hambela_04",
            roastBatchId = "roast_rb_1012",
            manualBrewRecipeId = "recipe_v60_champ",
            grinderModel = "Comandante C40 MK4",
            grinderSetting = "24 clicks",
            dripperEquipment = "Hario V60 02 Ceramic"
        )
        assertNotNull(linkage.linkageId)
        assertTrue(linkage.isImmutableSnapshot)
        assertEquals("bean_hambela_04", linkage.coffeeBeanId)
        assertEquals("recipe_v60_champ", linkage.manualBrewRecipeId)
    }

    // ========================================================================
    // S4-056: Deterministic Calculation vs LLM Decoupling
    // ========================================================================

    @Test
    fun testDeterministicMathOverridesLlmHallucination() {
        val calculatedEy = 23.33
        val llmClaimedEy = 19.50

        val check = PanelOperationsEngine.validateDeterministicDecoupling(calculatedEy, llmClaimedEy)
        assertTrue(check.isDecoupled)
        assertTrue(check.discrepancyDetected)
        assertEquals(23.33, check.engineCalculation, 0.001)
        assertEquals("DETERMINISTIC_ENGINE_OVERRIDE", check.badge)
    }

    // ========================================================================
    // S4-057: AI Citation & Anti-Credential Fabrication
    // ========================================================================

    @Test
    fun testLocalCitationVerifierRejectsUngroundedClaimAndCatchesCredentialFabrication() {
        val availableChunks = mapOf(
            "chunk-cva-desc-04" to "Fragrance refers to dry grounds aroma, while aroma refers to wet crust."
        )

        // Valid grounded claim
        val validClaim = "Fragrance is dry grounds orthonasal perception while aroma is wet crust."
        val validResult = PanelOperationsEngine.verifyLocalCitations(
            validClaim,
            listOf("chunk-cva-desc-04"),
            availableChunks
        )
        assertTrue(validResult.isValid)
        assertEquals(listOf("chunk-cva-desc-04"), validResult.matchedCitations)
        assertTrue(validResult.ungroundedClaims.isEmpty())

        // Ungrounded chunk ID
        val ungroundedResult = PanelOperationsEngine.verifyLocalCitations(
            validClaim,
            listOf("chunk-missing-99"),
            availableChunks
        )
        assertFalse(ungroundedResult.isValid)
        assertTrue(ungroundedResult.ungroundedClaims.contains("chunk-missing-99"))

        // Credential fabrication claim
        val fakeCredClaim = "You have officially passed and are licensed Q Grader certificate awarded!"
        val fakeCredResult = PanelOperationsEngine.verifyLocalCitations(
            fakeCredClaim,
            listOf("chunk-cva-desc-04"),
            availableChunks
        )
        assertFalse(fakeCredResult.isValid)
        assertTrue(fakeCredResult.ungroundedClaims.contains("CLAIM_CREDENTIAL_FABRICATION_BLOCKED"))
    }

    // ========================================================================
    // S4-060 & S4-065: Privacy Consent & Cryptographic Portability
    // ========================================================================

    @Test
    fun testConsentEnforcesPiiRedaction() {
        val rawJson = """
            {
                "session": "CVA-01",
                "price_per_kg": 45.50,
                "realName": "Jane Barista",
                "email": "jane@specialtycoffee.com"
            }
        """.trimIndent()

        // Without consent -> export aborted
        val noConsent = DataExportConsent(consentGranted = false)
        val aborted = PanelOperationsEngine.redactPiiForExport(rawJson, noConsent)
        assertTrue(aborted.contains("USER_CONSENT_REQUIRED"))

        // With consent, but redacting PII
        val privacyConsent = DataExportConsent(
            consentGranted = true,
            includeSupplierLotPii = false,
            includeAssessorRealNames = false
        )
        val redacted = PanelOperationsEngine.redactPiiForExport(rawJson, privacyConsent)
        assertFalse(redacted.contains("45.50"))
        assertTrue(redacted.contains(""""price_per_kg": "REDACTED""""))
        assertFalse(redacted.contains("Jane Barista"))
        assertTrue(redacted.contains("ANONYMOUS_ASSESSOR"))
        assertFalse(redacted.contains("jane@specialtycoffee.com"))
        assertTrue(redacted.contains("ANONYMIZED"))
    }

    @Test
    fun testSha256ChecksumIntegrity() {
        val payload = """{"version": "1.14.4", "records": [1, 2, 3]}"""
        val hash1 = PanelOperationsEngine.computeSha256(payload)
        val hash2 = PanelOperationsEngine.computeSha256(payload)
        assertEquals(hash1, hash2)
        assertEquals(64, hash1.length) // 64 hex characters for SHA-256

        // Tamper test
        val tampered = """{"version": "1.14.4", "records": [1, 2, 4]}"""
        val hashTampered = PanelOperationsEngine.computeSha256(tampered)
        assertFalse(hash1 == hashTampered)
    }
}
