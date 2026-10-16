package com.coffeecreator.app.core.brew

import com.coffeecreator.app.core.ble.DiscontinuityType
import com.coffeecreator.app.core.model.BrewStep
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Deterministic mathematical algorithms for real-time brewing science:
 * - Rolling ordinary-least-squares flow rate derivation (g/s)
 * - Physical scale artifact and vessel lift-off rejection filter
 * - Target water proximity zone classifier
 * - Piecewise ideal brew curve generation and actual-vs-ideal RMSE computation
 * - Auto-start and espresso flow auto-stop threshold evaluators
 */
object BrewFilterEngine {
    private const val FLOW_REGRESSION_EPSILON_SECONDS_SQUARED = 1e-9
    private const val FLOW_RATE_NOISE_FLOOR_GPS = 0.05
    private const val VESSEL_REMOVAL_DROP_GRAMS = 5.0
    private const val AUTO_START_WEIGHT_DELTA_GRAMS = 0.5
    private const val ESPRESSO_STOP_FLOW_RATE_GPS = 0.20
    private const val ESPRESSO_STOP_SUSTAINED_DURATION_MS = 1500L

    /**
     * Calculates rolling flow rate (g/s) from discrete (timestampMonoMs, weightGrams) samples
     * using Ordinary Least Squares (OLS) linear regression.
     */
    fun calculateRollingFlowRate(samples: List<Pair<Long, Double>>): Double {
        if (samples.size < 2) return 0.0
        val t0 = samples.first().first
        val n = samples.size
        var sumT = 0.0
        var sumW = 0.0

        // Convert delta time to seconds for numerical stability
        val points = samples.map { (tMs, w) ->
            val tSec = (tMs - t0) / 1000.0
            sumT += tSec
            sumW += w
            Pair(tSec, w)
        }

        val meanT = sumT / n
        val meanW = sumW / n

        var numerator = 0.0
        var denominator = 0.0
        for ((t, w) in points) {
            val dt = t - meanT
            val dw = w - meanW
            numerator += dt * dw
            denominator += dt * dt
        }

        if (denominator <= FLOW_REGRESSION_EPSILON_SECONDS_SQUARED) return 0.0
        val slope = numerator / denominator

        // Noise floor suppression: clamp near-zero flow to 0.0
        if (abs(slope) < FLOW_RATE_NOISE_FLOOR_GPS) return 0.0

        return BigDecimal.valueOf(slope)
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()
    }

    /**
     * Rejects physical transient artifacts (carafe swirl, spoon stir, vessel removal).
     * Returns the validated filtered weight and any flagged discontinuity.
     */
    fun filterScaleSample(
        rawWeight: Double,
        prevFiltered: Double,
        timeDeltaMs: Long,
        recentSamples: List<Double> = emptyList()
    ): Pair<Double, DiscontinuityType?> {
        val deltaWeight = rawWeight - prevFiltered

        // 1. Vessel removal detection: steep negative drop (> 5g in < 250ms)
        if (deltaWeight < -VESSEL_REMOVAL_DROP_GRAMS && timeDeltaMs in 1..400) {
            return Pair(prevFiltered, DiscontinuityType.VESSEL_REMOVAL)
        }

        // 2. Agitation / Swirl oscillation detection
        if (recentSamples.size >= 3) {
            val diffs = recentSamples.zipWithNext { a, b -> abs(b - a) }
            val maxDiff = diffs.maxOrNull() ?: 0.0
            if (maxDiff > 8.0 && abs(rawWeight - prevFiltered) > 4.0) {
                val median = recentSamples.sorted()[recentSamples.size / 2]
                return Pair(median, DiscontinuityType.AGITATION_ARTIFACT)
            }
        }

        // 3. Normal sample
        val clampedWeight = if (rawWeight < -0.2) 0.0 else rawWeight
        val rounded = BigDecimal.valueOf(clampedWeight).setScale(1, RoundingMode.HALF_UP).toDouble()
        return Pair(rounded, null)
    }

    /**
     * Determines target water proximity state for visual and haptic feedback.
     */
    fun calculateProximity(currentWeight: Double, targetWater: Double): ProximityState {
        val remaining = targetWater - currentWeight
        return when {
            remaining > 10.0 -> ProximityState.NORMAL
            remaining > 2.0 -> ProximityState.APPROACHING_10G
            remaining > 0.0 -> ProximityState.IMMINENT_2G
            remaining >= -1.0 -> ProximityState.TARGET_MATCHED
            else -> ProximityState.OVERSHOOT
        }
    }

    /**
     * Generates a piecewise linear ramp of ideal target water mass (g) over elapsed seconds.
     */
    fun generateIdealCurvePoints(steps: List<BrewStep>): List<Pair<Int, Double>> {
        if (steps.isEmpty()) return listOf(Pair(0, 0.0))
        val result = mutableListOf<Pair<Int, Double>>()
        result.add(Pair(0, 0.0))

        var accumulatedTime = 0
        var previousTarget = 0.0

        for (step in steps) {
            val stepStart = accumulatedTime
            val stepEnd = accumulatedTime + step.durationSeconds
            val target = step.cumulativeMassGrams

            // Linear interpolation points per second or at boundaries
            result.add(Pair(stepStart, previousTarget))
            result.add(Pair(stepEnd, target))

            accumulatedTime = stepEnd
            previousTarget = target
        }
        return result
    }

    /**
     * Computes the Root Mean Square Error (RMSE) between actual weight stream and ideal curve.
     */
    fun calculateCurveRmse(actualPoints: List<Pair<Int, Double>>, idealPoints: List<Pair<Int, Double>>): Double {
        if (actualPoints.isEmpty() || idealPoints.isEmpty()) return 0.0

        val idealMap = idealPoints.toMap()
        var sumSquaredError = 0.0
        var count = 0

        for ((t, actualW) in actualPoints) {
            val idealW = idealMap[t] ?: interpolateIdealWeight(t, idealPoints)
            val error = actualW - idealW
            sumSquaredError += error * error
            count++
        }

        if (count == 0) return 0.0
        val mse = sumSquaredError / count
        return BigDecimal.valueOf(sqrt(mse)).setScale(2, RoundingMode.HALF_UP).toDouble()
    }

    private fun interpolateIdealWeight(t: Int, idealPoints: List<Pair<Int, Double>>): Double {
        if (t <= idealPoints.first().first) return idealPoints.first().second
        if (t >= idealPoints.last().first) return idealPoints.last().second

        for (i in 0 until idealPoints.size - 1) {
            val (t1, w1) = idealPoints[i]
            val (t2, w2) = idealPoints[i + 1]
            if (t in t1..t2) {
                if (t2 == t1) return w1
                val fraction = (t - t1).toDouble() / (t2 - t1)
                return w1 + fraction * (w2 - w1)
            }
        }
        return idealPoints.last().second
    }

    /**
     * Detects weight-triggered auto-start condition: positive delta >= 0.5g within short window.
     */
    fun checkAutoStart(rawWeightDelta: Double, timeDeltaMs: Long): Boolean =
        rawWeightDelta >= AUTO_START_WEIGHT_DELTA_GRAMS && timeDeltaMs in 1..600

    /**
     * Detects espresso auto-stop condition: flow rate < 0.20 g/s sustained for >= 1.5s after reaching yield.
     */
    fun checkEspressoAutoStop(
        flowRateGps: Double,
        sustainedDurationMs: Long,
        currentYieldGrams: Double,
        minYieldGrams: Double = 5.0
    ): Boolean = currentYieldGrams >= minYieldGrams &&
        flowRateGps < ESPRESSO_STOP_FLOW_RATE_GPS &&
        sustainedDurationMs >= ESPRESSO_STOP_SUSTAINED_DURATION_MS
}
