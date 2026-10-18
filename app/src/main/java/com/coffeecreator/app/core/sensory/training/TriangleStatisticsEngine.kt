package com.coffeecreator.app.core.sensory.training

import java.util.UUID
import kotlin.random.Random

data class BinomialProbabilityResult(
    val totalTrialsN: Int,
    val correctTrialsK: Int,
    val nullHypothesisP: Double = 1.0 / 3.0,
    val pValueCumulative: Double,
    val isSignificantAlpha05: Boolean,
    val isSignificantAlpha01: Boolean,
    val methodologicalCaveats: List<String>
)

object TriangleStatisticsEngine {

    private fun combinations(n: Int, k: Int): Double {
        if (k < 0 || k > n) return 0.0
        if (k == 0 || k == n) return 1.0
        val effectiveK = if (k > n / 2) n - k else k
        var res = 1.0
        for (i in 1..effectiveK) {
            res = res * (n - i + 1) / i
        }
        return res
    }

    /**
     * Exact Binomial Upper-tail Cumulative Probability:
     * P(X >= k) = \sum_{i=k}^n \binom{n}{i} * p^i * (1-p)^{n-i}
     */
    fun calculateBinomialPValue(n: Int, k: Int, p: Double = 1.0 / 3.0): BinomialProbabilityResult {
        require(n > 0) { "Total trials n must be positive" }
        require(k in 0..n) { "Correct count k must be between 0 and n" }

        var pValue = 0.0
        for (i in k..n) {
            val comb = combinations(n, i)
            val probI = comb * Math.pow(p, i.toDouble()) * Math.pow(1.0 - p, (n - i).toDouble())
            pValue += probI
        }
        val clampedPValue = pValue.coerceIn(0.0, 1.0)

        val caveats = mutableListOf<String>()
        if (n < 6) {
            caveats.add("Small sample size (n < 6): statistical power is severely constrained; results are preliminary.")
        }
        caveats.add("Assumes independent trials: repeated testing with identical coffees incurs sensory adaptation and memory bias.")
        caveats.add("Statistical significance does NOT prove superior cupping ability; it rejects the pure guessing hypothesis.")
        caveats.add("Practice drill only: does NOT confer official SCA or CQI Q-Grader credential.")

        return BinomialProbabilityResult(
            totalTrialsN = n,
            correctTrialsK = k,
            nullHypothesisP = p,
            pValueCumulative = clampedPValue,
            isSignificantAlpha05 = clampedPValue <= 0.05,
            isSignificantAlpha01 = clampedPValue <= 0.01,
            methodologicalCaveats = caveats
        )
    }

    /**
     * Generates a balanced sequence of Triangle trials across all 6 permutations:
     * AAB, ABA, BAA, BBA, BAB, ABB.
     */
    fun generateBalancedSession(
        sessionSeed: Long,
        trialCount: Int = 6,
        referenceCoffee: String = "Coffee A (Washed)",
        contrastCoffee: String = "Coffee B (Natural)"
    ): List<TriangleTrial> {
        val random = Random(sessionSeed)
        val allPermutations = TriangleSampleOrder.values().toList()
        val usedCodes = mutableSetOf<String>()

        return (0 until trialCount).map { i ->
            val order = allPermutations[i % allPermutations.size]
            val codes = mutableListOf<String>()
            repeat(3) {
                var code: String
                do {
                    code = "%03d".format(random.nextInt(100, 1000))
                } while (code in usedCodes)
                usedCodes.add(code)
                codes.add(code)
            }

            TriangleTrial(
                trialId = "tri_${UUID.randomUUID().toString().take(8)}",
                trialIndex = i + 1,
                referenceSampleName = referenceCoffee,
                contrastSampleName = contrastCoffee,
                assignedOrder = order,
                blindSampleCodes = codes
            )
        }
    }

    /**
     * Submits an assessor's choice for a trial and evaluates correctness against the concealed key.
     */
    fun evaluateTrial(trial: TriangleTrial, chosenCupIndex: Int, confidence: Int): TriangleTrial {
        require(chosenCupIndex in 0..2) { "Chosen cup index must be 0, 1, or 2" }
        val isCorrect = chosenCupIndex == trial.assignedOrder.oddCupIndex
        return trial.copy(
            submittedChoiceIndex = chosenCupIndex,
            confidenceRating = confidence.coerceIn(1, 5),
            timestampEpochMs = System.currentTimeMillis(),
            isEvaluated = true,
            isCorrect = isCorrect
        )
    }

    /**
     * Exact Binomial probability for 2-AFC Paired Comparison (p0 = 1/2).
     */
    fun calculatePairedComparisonPValue(n: Int, k: Int): BinomialProbabilityResult {
        require(n > 0) { "Total trials n must be positive" }
        require(k in 0..n) { "Correct count k must be between 0 and n" }

        var pValue = 0.0
        val p = 0.5
        for (i in k..n) {
            val comb = combinations(n, i)
            val probI = comb * Math.pow(p, i.toDouble()) * Math.pow(1.0 - p, (n - i).toDouble())
            pValue += probI
        }
        val clampedPValue = pValue.coerceIn(0.0, 1.0)

        val caveats = mutableListOf<String>()
        if (n < 6) {
            caveats.add("Small sample size (n < 6): statistical power is severely constrained.")
        }
        caveats.add("Assumes independent 2-AFC trials. Null hypothesis p0 = 0.50 (chance guess between 2 options).")
        caveats.add("Practice drill only: does NOT confer official SCA or CQI Q-Grader credential.")

        return BinomialProbabilityResult(
            totalTrialsN = n,
            correctTrialsK = k,
            nullHypothesisP = p,
            pValueCumulative = clampedPValue,
            isSignificantAlpha05 = clampedPValue <= 0.05,
            isSignificantAlpha01 = clampedPValue <= 0.01,
            methodologicalCaveats = caveats
        )
    }

    /**
     * ASTM E679 Ascending Concentration Best-Estimate Threshold (BET) calculus.
     */
    fun calculateBestEstimateThreshold(steps: List<AscendingConcentrationStep>, taste: BasicTaste): TasteThresholdResult {
        if (steps.isEmpty()) {
            return TasteThresholdResult(taste, null, null)
        }

        val sortedSteps = steps.sortedBy { it.concentrationGramsPerLiter }

        // Detection threshold: lowest concentration where distinguishable from water
        val firstDetIdx = sortedSteps.indexOfFirst { it.isDistinguishableFromWater == true }
        val detThreshold = when {
            firstDetIdx == -1 -> null
            firstDetIdx == 0 -> sortedSteps[0].concentrationGramsPerLiter
            else -> {
                val prevC = sortedSteps[firstDetIdx - 1].concentrationGramsPerLiter
                val currC = sortedSteps[firstDetIdx].concentrationGramsPerLiter
                Math.sqrt(prevC * currC)
            }
        }

        // Recognition threshold: lowest concentration where specific taste correctly identified
        val firstRecIdx = sortedSteps.indexOfFirst { it.isCorrectIdentification == true }
        val recThreshold = when {
            firstRecIdx == -1 -> null
            firstRecIdx == 0 -> sortedSteps[0].concentrationGramsPerLiter
            else -> {
                val prevC = sortedSteps[firstRecIdx - 1].concentrationGramsPerLiter
                val currC = sortedSteps[firstRecIdx].concentrationGramsPerLiter
                Math.sqrt(prevC * currC)
            }
        }

        return TasteThresholdResult(
            taste = taste,
            detectionThresholdGramsPerLiter = detThreshold,
            recognitionThresholdGramsPerLiter = recThreshold,
            nonClinicalDisclaimer = "Educational threshold test only. Does not infer medical or clinical gustatory impairment."
        )
    }

    /**
     * Olfactory Spaced Repetition (Sensory SM-2 algorithm).
     * Quality rating q in 1..5.
     */
    fun updateSpacedRepetition(
        card: AromaSpacedRepetitionCard,
        qualityRating: Int,
        nowEpochMs: Long = System.currentTimeMillis()
    ): AromaSpacedRepetitionCard {
        val q = qualityRating.coerceIn(1, 5)
        val newEaseFactor = Math.max(1.3, card.easeFactor + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02)))

        val (newRepCount, newIntervalDays) = if (q < 3) {
            Pair(0, 1.0)
        } else {
            when (card.repetitionCount) {
                0 -> Pair(1, 1.0)
                1 -> Pair(2, 3.0)
                else -> Pair(card.repetitionCount + 1, (card.intervalDays * newEaseFactor).coerceAtLeast(1.0))
            }
        }

        val nextReviewMs = nowEpochMs + (newIntervalDays * 24.0 * 3600.0 * 1000.0).toLong()

        return card.copy(
            repetitionCount = newRepCount,
            intervalDays = newIntervalDays,
            easeFactor = newEaseFactor,
            nextReviewEpochMs = nextReviewMs,
            totalAttempts = card.totalAttempts + 1,
            correctAttempts = card.correctAttempts + (if (q >= 3) 1 else 0)
        )
    }

    /**
     * Aggregates aroma identification trials into a Confusion Matrix frequency list.
     */
    fun computeAromaConfusionMatrix(trials: List<Pair<String, String>>): List<AromaConfusionEntry> {
        val counts = mutableMapOf<Pair<String, String>, Int>()
        trials.forEach { (target, selected) ->
            val key = Pair(target, selected)
            counts[key] = (counts[key] ?: 0) + 1
        }
        return counts.map { (key, freq) ->
            AromaConfusionEntry(targetVialId = key.first, selectedVialId = key.second, frequency = freq)
        }.sortedByDescending { it.frequency }
    }

    /**
     * Confidence calibration score using Brier score (lower is better calibrated).
     * predictions: list of (confidence 1..5, isCorrect: Boolean).
     */
    fun calculateBrierScore(predictions: List<Pair<Int, Boolean>>): Double {
        if (predictions.isEmpty()) return 0.0
        var sumSquaredError = 0.0
        for ((conf, isCorrect) in predictions) {
            val prob = conf.coerceIn(1, 5) / 5.0
            val outcome = if (isCorrect) 1.0 else 0.0
            sumSquaredError += Math.pow(prob - outcome, 2.0)
        }
        return sumSquaredError / predictions.size.toDouble()
    }

    /**
     * Human observation verification guard (S4-040).
     * Strictly blocks virtual quizzes or reading-only completions from being marked as practical PASS.
     */
    fun verifyPracticalSensoryPassGuard(verification: PracticalExerciseVerification): Boolean {
        // Invariant 1: Cannot be auto-passed
        if (verification.isAutoPassed) return false
        // Invariant 2: Physical sample MUST have been prepared
        if (!verification.physicalSamplePrepared) return false
        // Invariant 3: Verifier identity must be provided
        if (verification.verifierPseudonym.trim().isBlank()) return false
        return true
    }
}
