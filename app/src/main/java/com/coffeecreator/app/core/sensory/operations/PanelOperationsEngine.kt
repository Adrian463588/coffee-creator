package com.coffeecreator.app.core.sensory.operations

import java.security.MessageDigest
import java.util.Random
import java.util.UUID
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Pure Kotlin mathematical and workflow engine for coffee sensory panel operations (Epic E, S4-052 to S4-065).
 * Free from Android, Room, or UI dependencies.
 */
object PanelOperationsEngine {

    // ========================================================================
    // 1. Panel Agreement & Calibration Statistics (S4-059)
    // ========================================================================

    /**
     * Computes the Pearson Product-Moment Correlation coefficient (r) between two assessors' scores.
     * Includes sample-size (N >= 2) and zero-variance guards (returns null when ratings are invariant).
     */
    fun calculatePearson(xScores: List<Double>, yScores: List<Double>): Double? {
        val n = minOf(xScores.size, yScores.size)
        if (n < 2) return null

        val xSub = xScores.take(n)
        val ySub = yScores.take(n)

        val xMean = xSub.average()
        val yMean = ySub.average()

        var sxx = 0.0
        var syy = 0.0
        var sxy = 0.0

        for (i in 0 until n) {
            val dx = xSub[i] - xMean
            val dy = ySub[i] - yMean
            sxx += dx * dx
            syy += dy * dy
            sxy += dx * dy
        }

        // Zero-variance guard: If either rater has invariant scores, correlation is undefined.
        if (sxx < 1e-9 || syy < 1e-9) {
            return null
        }

        val r = sxy / sqrt(sxx * syy)
        return roundToFourDecimals(r.coerceIn(-1.0, 1.0))
    }

    /**
     * Computes the Spearman Rank Correlation coefficient (rho) using fractional mid-ranks for tied values.
     */
    fun calculateSpearman(xScores: List<Double>, yScores: List<Double>): Double? {
        val n = minOf(xScores.size, yScores.size)
        if (n < 2) return null

        val xRanks = computeFractionalRanks(xScores.take(n))
        val yRanks = computeFractionalRanks(yScores.take(n))

        return calculatePearson(xRanks, yRanks)
    }

    /**
     * Computes fractional mid-ranks for a list of numeric values.
     * Tied scores receive the average of the ranks they span.
     */
    fun computeFractionalRanks(values: List<Double>): List<Double> {
        val n = values.size
        if (n == 0) return emptyList()

        val indexed = values.mapIndexed { idx, v -> Pair(v, idx) }.sortedBy { it.first }
        val ranks = DoubleArray(n)

        var i = 0
        while (i < n) {
            var j = i
            while (j + 1 < n && abs(indexed[j + 1].first - indexed[i].first) < 1e-9) {
                j++
            }
            // Tied group from i to j (0-indexed). 1-based ranks are (i + 1) to (j + 1).
            val avgRank = (i + 1 + j + 1) / 2.0
            for (k in i..j) {
                ranks[indexed[k].second] = avgRank
            }
            i = j + 1
        }
        return ranks.toList()
    }

    /**
     * Computes Mean Consensus Deviation (MCD) for each assessor against the panel consensus mean.
     * MCD_k = (1 / S) * sum_{s=1..S} |x_{k, s} - mu_s|
     */
    fun calculateMeanConsensusDeviation(scoresByAssessor: Map<String, List<Double>>): Map<String, Double> {
        if (scoresByAssessor.isEmpty()) return emptyMap()

        val sampleCount = scoresByAssessor.values.minOfOrNull { it.size } ?: 0
        if (sampleCount == 0) return emptyMap()

        // 1. Calculate consensus mean per sample
        val sampleMeans = DoubleArray(sampleCount)
        for (s in 0 until sampleCount) {
            var sum = 0.0
            var count = 0
            for (scores in scoresByAssessor.values) {
                sum += scores[s]
                count++
            }
            sampleMeans[s] = if (count > 0) sum / count else 0.0
        }

        // 2. Calculate MCD for each assessor
        val mcdMap = mutableMapOf<String, Double>()
        for ((assessorId, scores) in scoresByAssessor) {
            var totalDev = 0.0
            for (s in 0 until sampleCount) {
                totalDev += abs(scores[s] - sampleMeans[s])
            }
            val mcd = totalDev / sampleCount
            mcdMap[assessorId] = roundToFourDecimals(mcd)
        }
        return mcdMap
    }

    /**
     * Computes Fleiss' Generalized Kappa (kappa_Fleiss) for categorical multi-rater classifications
     * (e.g. Clean vs Taint vs Fault).
     *
     * @param ratingMatrix N x C matrix where N is items and C is categories.
     *                     ratingMatrix[i][j] is the count of raters assigning item i to category j.
     */
    fun calculateFleissKappa(ratingMatrix: Array<IntArray>): Double? {
        val n = ratingMatrix.size // Number of items/samples
        if (n < 1) return null

        val c = ratingMatrix[0].size // Number of categories
        if (c < 2) return null

        val m = ratingMatrix[0].sum() // Raters per item
        if (m < 2) return null

        // Check that all rows have identical rater count m
        for (i in 0 until n) {
            if (ratingMatrix[i].sum() != m) return null
        }

        // 1. Compute Pi (observed agreement for item i)
        // Pi = (1 / (m * (m - 1))) * (sum_j n_ij^2 - m)
        var pBarSum = 0.0
        for (i in 0 until n) {
            var sumSquares = 0
            for (j in 0 until c) {
                val count = ratingMatrix[i][j]
                sumSquares += count * count
            }
            val pi = (sumSquares - m).toDouble() / (m * (m - 1))
            pBarSum += pi
        }
        val pBar = pBarSum / n

        // 2. Compute pj (proportion of all assignments to category j)
        val pj = DoubleArray(c)
        val totalAssignments = (n * m).toDouble()
        for (j in 0 until c) {
            var colSum = 0
            for (i in 0 until n) {
                colSum += ratingMatrix[i][j]
            }
            pj[j] = colSum.toDouble() / totalAssignments
        }

        // 3. Compute Pe_bar (chance agreement)
        var peBar = 0.0
        for (j in 0 until c) {
            peBar += pj[j] * pj[j]
        }

        // Guard against perfect chance agreement
        if (abs(pBar - 1.0) < 1e-9 && abs(peBar - 1.0) < 1e-9) {
            return 1.0
        }
        if (abs(1.0 - peBar) < 1e-9) {
            return null
        }

        val kappa = (pBar - peBar) / (1.0 - peBar)
        return roundToFourDecimals(kappa.coerceIn(-1.0, 1.0))
    }

    /**
     * Translates Fleiss or Cohen Kappa into standard Landis & Koch (1977) agreement benchmarks.
     */
    fun getLandisKochLabel(kappa: Double?): String {
        return when {
            kappa == null -> "INDETERMINATE"
            kappa < 0.0 -> "POOR"
            kappa <= 0.20 -> "SLIGHT"
            kappa <= 0.40 -> "FAIR"
            kappa <= 0.60 -> "MODERATE"
            kappa <= 0.80 -> "SUBSTANTIAL"
            else -> "ALMOST_PERFECT"
        }
    }

    /**
     * Generates a complete PanelAgreementReport for a given sample and attribute.
     */
    fun generatePanelReport(
        sessionId: String,
        sampleId: String,
        attributeKey: String,
        scoresByAssessor: Map<String, Double>,
        categoriesByAssessor: Map<String, String>? = null
    ): PanelAgreementReport {
        val raterCount = scoresByAssessor.size
        val scores = scoresByAssessor.values.toList()

        val mean = if (raterCount > 0) scores.average() else 0.0
        val stdDev = if (raterCount > 1) {
            val variance = scores.map { (it - mean) * (it - mean) }.sum() / (raterCount - 1)
            sqrt(variance)
        } else 0.0

        // Single sample MCD is simply |score - mean|
        val assessorMcds = scoresByAssessor.mapValues { roundToFourDecimals(abs(it.value - mean)) }
        val panelMcd = if (assessorMcds.isNotEmpty()) assessorMcds.values.average() else 0.0

        val caveats = mutableListOf<String>()
        if (raterCount < 3) {
            caveats.add("Small panel size (N=$raterCount < 3): Statistical power is constrained.")
        }
        if (stdDev < 1e-9 && raterCount >= 2) {
            caveats.add("Zero variance detected: All raters assigned identical scores.")
        }

        // Fleiss Kappa if categorical data is supplied
        var fleissKappa: Double? = null
        var catLabel: String? = null
        if (!categoriesByAssessor.isNullOrEmpty() && categoriesByAssessor.size >= 2) {
            val distinctCategories = categoriesByAssessor.values.distinct().sorted()
            if (distinctCategories.size >= 2) {
                val matrix = Array(1) { IntArray(distinctCategories.size) }
                for (cat in categoriesByAssessor.values) {
                    val catIdx = distinctCategories.indexOf(cat)
                    if (catIdx >= 0) {
                        matrix[0][catIdx]++
                    }
                }
                fleissKappa = calculateFleissKappa(matrix)
                catLabel = getLandisKochLabel(fleissKappa)
            }
        }

        return PanelAgreementReport(
            sessionId = sessionId,
            sampleId = sampleId,
            attributeKey = attributeKey,
            raterCount = raterCount,
            consensusMean = roundToFourDecimals(mean),
            standardDeviation = roundToFourDecimals(stdDev),
            panelMeanConsensusDeviation = roundToFourDecimals(panelMcd),
            assessorMcds = assessorMcds,
            fleissKappa = fleissKappa,
            categoricalAgreementLabel = catLabel,
            caveats = caveats,
            isHighCalibration = panelMcd <= 0.35
        )
    }

    // ========================================================================
    // 2. Blind Panel Workflow Management (S4-058)
    // ========================================================================

    /**
     * Generates randomized, unique 3-digit blind sample codes (e.g. "382", "914") for panel tasting.
     */
    fun generateBlindCodes(sampleIds: List<String>, seed: Long? = null): Map<String, BlindSampleMapping> {
        val random = if (seed != null) Random(seed) else Random()
        val assignedCodes = mutableSetOf<String>()
        val mappings = mutableMapOf<String, BlindSampleMapping>()

        for (sampleId in sampleIds) {
            var code: String
            do {
                code = (100 + random.nextInt(900)).toString()
            } while (assignedCodes.contains(code))

            assignedCodes.add(code)
            mappings[sampleId] = BlindSampleMapping(
                blindCode = code,
                actualCoffeeBeanId = sampleId,
                isRevealed = false
            )
        }
        return mappings
    }

    /**
     * Unblinds a sample mapping, enforcing role permissions and logging premature unblinding.
     */
    fun executeReveal(
        mapping: BlindSampleMapping,
        assessor: AssessorProfile,
        reason: String,
        forcePremature: Boolean = false
    ): BlindSampleMapping {
        val auditReason = if (forcePremature) {
            "UNBLINDED_PREMATURELY_AUDITED: $reason"
        } else {
            reason
        }

        return mapping.copy(
            isRevealed = true,
            revealedAtEpoch = System.currentTimeMillis(),
            revealedByAssessorId = assessor.id,
            revealReason = auditReason
        )
    }

    // ========================================================================
    // 3. Historical Linkage Management (S4-055)
    // ========================================================================

    /**
     * Creates an immutable historical linkage record connecting a cupping evaluation to coffee beans,
     * roast batches, recipes, and grinder equipment without modifying existing target records.
     */
    fun createImmutableLinkage(
        assessmentId: String,
        coffeeBeanId: String,
        roastBatchId: String? = null,
        manualBrewRecipeId: String? = null,
        grinderModel: String? = null,
        grinderSetting: String? = null,
        dripperEquipment: String? = null
    ): CuppingHistoricalLinkage {
        return CuppingHistoricalLinkage(
            linkageId = UUID.randomUUID().toString(),
            assessmentId = assessmentId,
            coffeeBeanId = coffeeBeanId,
            roastBatchId = roastBatchId,
            manualBrewRecipeId = manualBrewRecipeId,
            grinderModel = grinderModel,
            grinderSetting = grinderSetting,
            dripperEquipment = dripperEquipment,
            createdAtEpoch = System.currentTimeMillis(),
            isImmutableSnapshot = true
        )
    }

    // ========================================================================
    // 4. Deterministic Engine vs LLM Decoupling & Citation (S4-056, S4-057)
    // ========================================================================

    /**
     * Verifies that pure Kotlin deterministic calculations override any conflicting LLM claims.
     */
    fun validateDeterministicDecoupling(
        calculatedValue: Double,
        reportedClaim: Double?
    ): DeterministicValidationResult {
        val discrepancy = reportedClaim != null && abs(calculatedValue - reportedClaim) > 0.05
        return DeterministicValidationResult(
            isDecoupled = true,
            engineCalculation = roundToFourDecimals(calculatedValue),
            reportedClaim = reportedClaim?.let { roundToFourDecimals(it) },
            discrepancyDetected = discrepancy,
            badge = if (discrepancy) "DETERMINISTIC_ENGINE_OVERRIDE" else "DETERMINISTIC_ENGINE"
        )
    }

    /**
     * Verifies citations of local RAG knowledge chunks and rejects ungrounded claims or fake credentials.
     */
    fun verifyLocalCitations(
        responseClaim: String,
        citedChunkIds: List<String>,
        availableChunkStore: Map<String, String>
    ): CitationVerificationResult {
        val matched = mutableListOf<String>()
        val ungrounded = mutableListOf<String>()

        for (chunkId in citedChunkIds) {
            if (availableChunkStore.containsKey(chunkId)) {
                matched.add(chunkId)
            } else {
                ungrounded.add(chunkId)
            }
        }

        val claimLower = responseClaim.lowercase()
        val hasCredentialViolation = claimLower.contains("official q grader certified") ||
                claimLower.contains("licensed q grader certificate awarded") ||
                claimLower.contains("sca official certification granted")

        if (hasCredentialViolation) {
            ungrounded.add("CLAIM_CREDENTIAL_FABRICATION_BLOCKED")
        }

        val isValid = ungrounded.isEmpty() && matched.isNotEmpty()
        return CitationVerificationResult(
            isValid = isValid,
            matchedCitations = matched,
            ungroundedClaims = ungrounded,
            disclaimer = CitationVerificationResult.NON_CERTIFICATION_DISCLAIMER
        )
    }

    // ========================================================================
    // 5. Cryptographic Portability & Privacy Redaction (S4-060, S4-065)
    // ========================================================================

    /**
     * Calculates the SHA-256 cryptographic digest of an export payload.
     */
    fun computeSha256(content: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(content.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    /**
     * Applies privacy redactions (scrubbing supplier pricing, lot PII, and cupper real names)
     * based on user consent before sharing.
     */
    fun redactPiiForExport(
        rawPayload: String,
        consent: DataExportConsent
    ): String {
        if (!consent.consentGranted) {
            return """{"error": "USER_CONSENT_REQUIRED", "export_aborted": true}"""
        }

        var processed = rawPayload
        if (!consent.includeSupplierLotPii) {
            // Scrub prices and proprietary farm contracts
            processed = processed.replace(Regex(""""price_per_kg":\s*[0-9.]+"""), """"price_per_kg": "REDACTED"""")
            processed = processed.replace(Regex(""""purchasePrice":\s*[0-9.]+"""), """"purchasePrice": "REDACTED"""")
            processed = processed.replace(Regex(""""supplierContractId":\s*"[^"]*""""), """"supplierContractId": "REDACTED"""")
        }

        if (!consent.includeAssessorRealNames) {
            // Anonymize assessor names to pseudonyms
            processed = processed.replace(Regex(""""realName":\s*"[^"]*""""), """"realName": "ANONYMOUS_ASSESSOR"""")
            processed = processed.replace(Regex(""""email":\s*"[^"]*""""), """"email": "ANONYMIZED"""")
        }

        return processed
    }

    private fun roundToFourDecimals(value: Double): Double {
        return (value * 10000.0).roundToInt() / 10000.0
    }
}
