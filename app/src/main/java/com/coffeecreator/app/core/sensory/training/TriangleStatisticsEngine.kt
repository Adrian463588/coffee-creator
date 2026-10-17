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
}
