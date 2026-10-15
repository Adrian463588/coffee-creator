package com.coffeecreator.app.core.sensory

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.security.MessageDigest

object CuppingAnalyticsEngine {

    private val MC = MathContext(8, RoundingMode.HALF_UP)

    // ========================================================================
    // 1. Multi-Evaluator Consensus & Dispersion (S2-059)
    // ========================================================================

    fun computeAttributeConsensus(
        attributeKey: String,
        scoresByEvaluator: Map<String, BigDecimal>,
        scaleType: SensoryScaleType = SensoryScaleType.Q_SCALE_6_TO_10
    ): AttributeConsensus {
        if (scoresByEvaluator.isEmpty()) {
            return AttributeConsensus(
                attributeKey = attributeKey,
                mean = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                median = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                stdDev = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                agreementIndex = BigDecimal.ONE.setScale(2, RoundingMode.HALF_UP),
                outlierEvaluatorIds = emptyList()
            )
        }

        val n = scoresByEvaluator.size
        val scores = scoresByEvaluator.values.toList()

        // 1. Mean
        val sum = scores.fold(BigDecimal.ZERO) { acc, v -> acc.add(v) }
        val mean = sum.divide(BigDecimal(n), 4, RoundingMode.HALF_UP)

        // 2. Median
        val sorted = scores.sorted()
        val median = if (n % 2 == 1) {
            sorted[n / 2]
        } else {
            sorted[n / 2 - 1].add(sorted[n / 2]).divide(BigDecimal(2), 4, RoundingMode.HALF_UP)
        }

        // 3. Sample Standard Deviation
        val stdDev = if (n > 1) {
            val varianceSum = scores.fold(BigDecimal.ZERO) { acc, v ->
                val diff = v.subtract(mean)
                acc.add(diff.multiply(diff))
            }
            val variance = varianceSum.divide(BigDecimal(n - 1), 6, RoundingMode.HALF_UP)
            BigDecimal(Math.sqrt(variance.toDouble())).setScale(4, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ZERO
        }

        // 4. Inter-Rater Agreement Index (IRAI)
        val tolerance = when (scaleType) {
            SensoryScaleType.Q_SCALE_6_TO_10 -> BigDecimal("0.50")
            SensoryScaleType.ORDINAL_1_TO_10 -> BigDecimal("1.00")
            SensoryScaleType.INTENSITY_0_TO_5 -> BigDecimal("0.75")
            SensoryScaleType.CHECKLIST_5_CUPS -> BigDecimal("0.50")
        }

        val rawAgreement = BigDecimal.ONE.subtract(
            stdDev.divide(tolerance, 4, RoundingMode.HALF_UP)
        )
        val agreementIndex = rawAgreement.coerceIn(BigDecimal.ZERO, BigDecimal.ONE)
            .setScale(2, RoundingMode.HALF_UP)

        // 5. Outlier Detection (|x - mean| > 2 * stdDev when n >= 3 and stdDev > 0)
        val outliers = mutableListOf<String>()
        if (n >= 3 && stdDev > BigDecimal("0.05")) {
            val threshold = stdDev.multiply(BigDecimal("2.0"))
            scoresByEvaluator.forEach { (evaluatorId, score) ->
                val dist = score.subtract(mean).abs()
                if (dist > threshold) {
                    outliers.add(evaluatorId)
                }
            }
        }

        return AttributeConsensus(
            attributeKey = attributeKey,
            mean = mean.setScale(2, RoundingMode.HALF_UP),
            median = median.setScale(2, RoundingMode.HALF_UP),
            stdDev = stdDev.setScale(2, RoundingMode.HALF_UP),
            agreementIndex = agreementIndex,
            outlierEvaluatorIds = outliers
        )
    }

    fun computeDescriptorCloud(sheets: List<EvaluationSheet>): List<DescriptorConcordance> {
        if (sheets.isEmpty()) return emptyList()
        val totalCupperCount = sheets.size
        val countMap = mutableMapOf<String, Int>()

        sheets.forEach { sheet ->
            sheet.descriptors.distinct().forEach { desc ->
                countMap[desc] = (countMap[desc] ?: 0) + 1
            }
        }

        return countMap.map { (desc, count) ->
            val frequency = BigDecimal(count)
                .divide(BigDecimal(totalCupperCount), 2, RoundingMode.HALF_UP)
            DescriptorConcordance(
                descriptor = desc,
                selectionCount = count,
                selectionFrequency = frequency,
                isConsensus = frequency >= BigDecimal("0.50")
            )
        }.sortedByDescending { it.selectionCount }
    }

    fun computeEvaluatorOverlapMatrix(sheets: List<EvaluationSheet>): List<EvaluatorOverlapPair> {
        val pairs = mutableListOf<EvaluatorOverlapPair>()
        val evaluators = sheets.map { it.evaluatorId }.distinct()

        for (i in 0 until evaluators.size) {
            for (j in (i + 1) until evaluators.size) {
                val evalA = evaluators[i]
                val evalB = evaluators[j]
                val descA = sheets.firstOrNull { it.evaluatorId == evalA }?.descriptors?.toSet() ?: emptySet()
                val descB = sheets.firstOrNull { it.evaluatorId == evalB }?.descriptors?.toSet() ?: emptySet()

                val intersectionSize = descA.intersect(descB).size
                val unionSize = descA.union(descB).size

                val jaccard = if (unionSize == 0) {
                    BigDecimal.ONE
                } else {
                    BigDecimal(intersectionSize).divide(BigDecimal(unionSize), 2, RoundingMode.HALF_UP)
                }

                pairs.add(EvaluatorOverlapPair(evalA, evalB, jaccard))
            }
        }
        return pairs
    }

    fun computeConsensus(session: CuppingSession, sampleId: String): ConsensusAnalytics {
        val sample = session.samples.firstOrNull { it.sampleId == sampleId }
            ?: throw IllegalArgumentException("Sample $sampleId not found in session ${session.id}")

        val relevantSheets = session.sheets.filter { it.sampleId == sampleId }
        val blindLabel = if (sample.isRevealed) "${sample.blindCode} (${sample.beanName})" else sample.blindCode

        if (relevantSheets.isEmpty()) {
            return ConsensusAnalytics(
                sampleId = sampleId,
                blindCode = blindLabel,
                finalConsensusScore = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                attributeConsensus = emptyMap(),
                descriptorCloud = emptyList(),
                overlapMatrix = emptyList(),
                overallPanelAgreementIndex = BigDecimal.ONE.setScale(2, RoundingMode.HALF_UP)
            )
        }

        // Aggregate by attribute
        val attrMap = mutableMapOf<String, AttributeConsensus>()
        val allKeys = session.template.attributes.map { it.key }

        allKeys.forEach { key ->
            val scoresForAttr = relevantSheets.mapNotNull { sheet ->
                sheet.attributeScores[key]?.let { sheet.evaluatorId to it }
            }.toMap()
            val attrDef = session.template.attributes.first { it.key == key }
            attrMap[key] = computeAttributeConsensus(key, scoresForAttr, attrDef.scaleType)
        }

        // Final consensus score = average of sheet total scores
        val totalScoreSum = relevantSheets.fold(BigDecimal.ZERO) { acc, sheet -> acc.add(sheet.totalScore) }
        val finalScore = totalScoreSum.divide(BigDecimal(relevantSheets.size), 2, RoundingMode.HALF_UP)

        // Descriptor cloud & overlap
        val cloud = computeDescriptorCloud(relevantSheets)
        val overlap = computeEvaluatorOverlapMatrix(relevantSheets)

        // Overall panel agreement
        val avgAgreement = if (attrMap.isNotEmpty()) {
            val sumAgreement = attrMap.values.fold(BigDecimal.ZERO) { acc, item -> acc.add(item.agreementIndex) }
            sumAgreement.divide(BigDecimal(attrMap.size), 2, RoundingMode.HALF_UP)
        } else {
            BigDecimal.ONE.setScale(2, RoundingMode.HALF_UP)
        }

        return ConsensusAnalytics(
            sampleId = sampleId,
            blindCode = blindLabel,
            finalConsensusScore = finalScore,
            attributeConsensus = attrMap,
            descriptorCloud = cloud,
            overlapMatrix = overlap,
            overallPanelAgreementIndex = avgAgreement
        )
    }

    // ========================================================================
    // 2. Score Calculation & Defect Deductions (S2-053, S2-055)
    // ========================================================================

    fun calculateTotalCuppingScore(
        templateType: CuppingTemplateType,
        attributeScores: Map<String, BigDecimal>,
        defects: DefectRecord = DefectRecord()
    ): BigDecimal {
        val rawSum = attributeScores.values.fold(BigDecimal.ZERO) { acc, v -> acc.add(v) }
        val defectDeduction = BigDecimal(defects.taintCupCount * 2 + defects.faultCupCount * 4)

        return when (templateType) {
            CuppingTemplateType.SPECIALTY_SCA_EQUIVALENT -> {
                rawSum.subtract(defectDeduction).coerceIn(BigDecimal.ZERO, BigDecimal("100.00"))
                    .setScale(2, RoundingMode.HALF_UP)
            }
            CuppingTemplateType.QUICK_BARISTA_DIAL_IN,
            CuppingTemplateType.PRODUCTION_QC,
            CuppingTemplateType.ESPRESSO_SENSORY -> {
                val count = attributeScores.size.coerceAtLeast(1)
                val average = rawSum.divide(BigDecimal(count), 2, RoundingMode.HALF_UP)
                average.subtract(defectDeduction).coerceIn(BigDecimal.ZERO, BigDecimal("10.00"))
                    .setScale(2, RoundingMode.HALF_UP)
            }
        }
    }

    // ========================================================================
    // 3. Physical Green & Roasted Grading (S2-061)
    // ========================================================================

    fun gradeGreenPhysicalLot(
        moisturePercentage: BigDecimal,
        waterActivityAw: BigDecimal,
        defects: List<GreenCoffeeDefectEntry>
    ): Pair<String, Pair<Int, Int>> {
        var primaryTotal = 0
        var secondaryTotal = 0

        defects.forEach { entry ->
            val count = entry.rawCount
            val weight = entry.defectType.penaltyWeight
            val fullDefects = count / weight
            if (entry.defectType.isPrimary) {
                primaryTotal += fullDefects
            } else {
                secondaryTotal += fullDefects
            }
        }

        val totalEquivalentDefects = primaryTotal + secondaryTotal

        val grade = when {
            primaryTotal == 0 && secondaryTotal <= 5 &&
                moisturePercentage in BigDecimal("10.0")..BigDecimal("12.0") &&
                waterActivityAw <= BigDecimal("0.70") -> "Specialty Grade 1 (Strict)"

            primaryTotal <= 1 && totalEquivalentDefects <= 8 &&
                moisturePercentage in BigDecimal("9.5")..BigDecimal("12.5") &&
                waterActivityAw <= BigDecimal("0.72") -> "Premium Grade 2"

            totalEquivalentDefects <= 15 -> "Exchange Grade 3 (Commercial)"
            else -> "Below Standard Grade (Off-Grade)"
        }

        return Pair(grade, Pair(primaryTotal, secondaryTotal))
    }

    // ========================================================================
    // 4. Roast-Rest Degassing Kinetics & Predictions (S2-062)
    // ========================================================================

    fun predictRoastRestCurve(
        bagId: String,
        roastLevel: String = "LIGHT", // LIGHT, MEDIUM, DARK
        process: String = "WASHED"     // WASHED, NATURAL, ANAEROBIC
    ): RestCurvePrediction {
        val cleanRoast = roastLevel.uppercase()
        val cleanProcess = process.uppercase()

        val (peakStart, peakEnd, staleDay) = when {
            cleanRoast.contains("LIGHT") -> {
                val start = if (cleanProcess.contains("ANAEROBIC") || cleanProcess.contains("NATURAL")) 8 else 10
                val end = if (cleanProcess.contains("ANAEROBIC") || cleanProcess.contains("NATURAL")) 28 else 24
                Triple(start, end, 45)
            }
            cleanRoast.contains("DARK") -> {
                Triple(3, 10, 25)
            }
            else -> { // MEDIUM
                val start = if (cleanProcess.contains("NATURAL")) 5 else 6
                val end = if (cleanProcess.contains("NATURAL")) 20 else 18
                Triple(start, end, 35)
            }
        }

        val badge = ProvenanceBadge(
            origin = EpistemologicalOrigin.AI_HEURISTIC_HYPOTHESIS,
            authorOrModelId = "COFFEE_REST_KINETICS_V1",
            confidenceLower = BigDecimal(peakStart),
            confidenceUpper = BigDecimal(peakEnd),
            confidenceLevelPercent = 85,
            methodDisclaimer = "Heuristic estimate based on bean density, process, and roast level. Actual degassing rates depend on ambient storage temperature and valve packaging.",
            timestamp = System.currentTimeMillis()
        )

        return RestCurvePrediction(
            bagId = bagId,
            estimatedPeakWindowStartDay = peakStart,
            estimatedPeakWindowEndDay = peakEnd,
            expectedStalingDay = staleDay,
            provenanceBadge = badge
        )
    }

    fun classifyObservationPhase(
        daysPostRoast: Int,
        prediction: RestCurvePrediction
    ): DegassingPhase {
        return when {
            daysPostRoast < prediction.estimatedPeakWindowStartDay -> DegassingPhase.TOO_FRESH_CO2_INTERFERENCE
            daysPostRoast <= prediction.estimatedPeakWindowEndDay -> DegassingPhase.PEAK_FLAVOR_WINDOW
            daysPostRoast <= prediction.expectedStalingDay -> DegassingPhase.STABLE_POST_PEAK
            else -> DegassingPhase.STALE_OXIDIZED
        }
    }

    // ========================================================================
    // 5. Offline QC Report & Cryptographic Receipt (S2-060)
    // ========================================================================

    fun generateOfflineReport(
        session: CuppingSession,
        physicalQcBySample: Map<String, PhysicalQcRecord> = emptyMap()
    ): QcReportReceipt {
        val now = System.currentTimeMillis()
        val sb = StringBuilder()

        sb.append("# Cupping Quality Control Report\n")
        sb.append("**Session:** ${session.title}\n")
        sb.append("**Protocol:** ${session.template.title} (${session.template.type.name})\n")
        sb.append("**Location:** ${session.location}\n")
        sb.append("**Masking Mode:** ${session.maskingMode.name}\n")
        sb.append("**Session Status:** ${session.status.name}\n")
        sb.append("**Evaluator Count:** ${session.evaluators.size}\n")
        sb.append("**Sample Count:** ${session.samples.size}\n")
        sb.append("**Generated At:** ${java.util.Date(now)}\n\n")

        sb.append("## Evaluator Panel\n")
        session.evaluators.forEach { eval ->
            sb.append("- **${eval.name}** (${eval.role.name}) [ID: ${eval.id}]\n")
        }
        sb.append("\n")

        sb.append("## Sample Evaluation Results\n\n")
        val csv = StringBuilder()
        csv.append("sample_id,blind_code,bean_name,origin,consensus_score,panel_agreement_index,primary_descriptors,green_grade,agtron_delta\n")

        session.samples.forEach { sample ->
            val consensus = computeConsensus(session, sample.sampleId)
            val phys = physicalQcBySample[sample.sampleId]

            val displayName = if (sample.isRevealed) "${sample.beanName} (${sample.origin})" else "HIDDEN (Masked)"
            sb.append("### Sample: ${sample.blindCode} — $displayName\n")
            sb.append("- **Consensus Cupping Score:** ${consensus.finalConsensusScore} / 100\n")
            sb.append("- **Panel Agreement Index (IRAI):** ${consensus.overallPanelAgreementIndex}\n")

            sb.append("- **Attribute Breakdown:**\n")
            consensus.attributeConsensus.forEach { (k, c) ->
                val outlierTag = if (c.outlierEvaluatorIds.isNotEmpty()) " [Outliers: ${c.outlierEvaluatorIds.joinToString()}]" else ""
                sb.append("  - `$k`: Mean ${c.mean}, Median ${c.median}, SD ${c.stdDev}, Agreement ${c.agreementIndex}$outlierTag\n")
            }

            val topDescriptors = consensus.descriptorCloud.take(5).joinToString(", ") { "${it.descriptor} (${(it.selectionFrequency * BigDecimal(100)).toInt()}%)" }
            sb.append("- **Consensus Descriptors:** ${if (topDescriptors.isEmpty()) "None recorded" else topDescriptors}\n")

            if (phys != null) {
                sb.append("- **Physical QC:** Grade: ${phys.calculatedGrade}, Moisture: ${phys.moisturePercentage}%, aw: ${phys.waterActivityAw}, Agtron Roast Delta: ${phys.roastDeltaAgtron}\n")
            }
            sb.append("\n")

            // Append to CSV
            val descCsv = consensus.descriptorCloud.take(3).joinToString(";") { it.descriptor }
            val gradeCsv = phys?.calculatedGrade ?: "N/A"
            val agtronCsv = phys?.roastDeltaAgtron?.toString() ?: "N/A"
            csv.append("${sample.sampleId},\"${sample.blindCode}\",\"${if (sample.isRevealed) sample.beanName else "MASKED"}\",\"${if (sample.isRevealed) sample.origin else "MASKED"}\",${consensus.finalConsensusScore},${consensus.overallPanelAgreementIndex},\"$descCsv\",\"$gradeCsv\",$agtronCsv\n")
        }

        sb.append("## Audit Trail Log\n")
        if (session.auditTrail.isEmpty()) {
            sb.append("_No administrative state mutations recorded._\n")
        } else {
            session.auditTrail.forEach { entry ->
                sb.append("- [${java.util.Date(entry.timestamp)}] User `${entry.editorId}`: **${entry.action}** — ${entry.reason} (State: ${entry.previousStateSummary} -> ${entry.newStateSummary})\n")
            }
        }

        val fullReportText = sb.toString()
        val md = MessageDigest.getInstance("SHA-256")
        val hashBytes = md.digest(fullReportText.toByteArray(Charsets.UTF_8))
        val sha256 = hashBytes.joinToString("") { "%02x".format(it) }

        return QcReportReceipt(
            sessionId = session.id,
            generatedAt = now,
            sha256Fingerprint = sha256,
            markdownReport = fullReportText,
            csvData = csv.toString()
        )
    }
}
