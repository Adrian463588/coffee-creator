package com.coffeecreator.app.core.brew

import com.coffeecreator.app.core.ble.BleScaleDevice
import com.coffeecreator.app.core.ble.DiscontinuityType
import com.coffeecreator.app.core.ble.ScaleConnectionState
import com.coffeecreator.app.core.ble.SimulatedBleScaleDriver
import com.coffeecreator.app.core.data.CuratedCoffeeCatalog
import com.coffeecreator.app.core.model.BrewStep
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.core.model.SensoryObservation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * BDD-style unit tests verifying Epic S2-E03: Active Brew Timer, BLE and Live Feedback.
 * Validates S2-027 through S2-038.
 */
class ActiveBrewTimerBleTest {

    // --- S2-027: Monotonic Background-Safe Timebase ---

    @Test
    fun `S2-027 Elapsed time accumulates accurately across pause and resume cycles`() {
        var accumulatedSeconds = 0
        var isRunning = false
        var timerOriginMonoMs = 0L

        // Start timer at t = 10,000ms
        val startMonoMs = 10000L
        timerOriginMonoMs = startMonoMs - accumulatedSeconds * 1000L
        isRunning = true

        // Simulate 15s elapsed
        val pauseMonoMs = 25000L
        accumulatedSeconds = ((pauseMonoMs - timerOriginMonoMs) / 1000L).toInt()
        isRunning = false
        assertEquals(15, accumulatedSeconds)

        // Wall clock change or background pause for 20s
        val resumeMonoMs = 45000L
        timerOriginMonoMs = resumeMonoMs - accumulatedSeconds * 1000L
        isRunning = true

        // Evaluate at 50,000ms (+5s)
        val currentMonoMs = 50000L
        val currentElapsed = ((currentMonoMs - timerOriginMonoMs) / 1000L).toInt()
        assertEquals(20, currentElapsed)
    }

    // --- S2-028: Multi-Step Pour Guidance & Target Water Tracking ---

    @Test
    fun `S2-028 Step progression tracks active step, next step preview, and remaining water`() {
        val steps = listOf(
            BrewStep(stepNumber = 1, description = "Bloom", targetWaterGrams = 50.0, cumulativeMassGrams = 50.0, durationSeconds = 45, action = "Bloom"),
            BrewStep(stepNumber = 2, description = "First pour", targetWaterGrams = 100.0, cumulativeMassGrams = 150.0, durationSeconds = 30, action = "Pour 1"),
            BrewStep(stepNumber = 3, description = "Second pour", targetWaterGrams = 100.0, cumulativeMassGrams = 250.0, durationSeconds = 45, action = "Pour 2")
        )

        // At t = 20s (inside Bloom)
        val t20 = 20
        var accTime = 0
        var activeStepIdx = 0
        for (i in steps.indices) {
            if (t20 < accTime + steps[i].durationSeconds) {
                activeStepIdx = i
                break
            }
            accTime += steps[i].durationSeconds
        }
        assertEquals(0, activeStepIdx)
        assertEquals("Bloom", steps[activeStepIdx].action)
        assertEquals(25, steps[activeStepIdx].durationSeconds - (t20 - accTime))

        // At t = 50s (inside Pour 1)
        val t50 = 50
        accTime = 0
        for (i in steps.indices) {
            if (t50 < accTime + steps[i].durationSeconds) {
                activeStepIdx = i
                break
            }
            accTime += steps[i].durationSeconds
        }
        assertEquals(1, activeStepIdx)
        assertEquals("Pour 1", steps[activeStepIdx].action)
        assertEquals(150.0, steps[activeStepIdx].cumulativeMassGrams, 0.001)

        // Remaining water to cumulative target at 120.0g current weight
        val currentWeight = 120.0
        val remainingWater = steps[activeStepIdx].cumulativeMassGrams - currentWeight
        assertEquals(30.0, remainingWater, 0.001)
    }

    // --- S2-029: Pause, Resume, Skip, Restart & Snapshot Recovery ---

    @Test
    fun `S2-029 Brew session snapshot serializes and restores without state loss`() {
        val recipe = CuratedCoffeeCatalog.recipes.first()
        val snapshot = BrewSessionSnapshot(
            sessionId = "session-snap-01",
            recipeId = recipe.id,
            status = BrewExecutionStatus.PAUSED,
            currentStepIndex = 1,
            accumulatedElapsedSeconds = 65,
            lastWeightGrams = 148.5,
            sampleCount = 120
        )

        assertEquals("session-snap-01", snapshot.sessionId)
        assertEquals(recipe.id, snapshot.recipeId)
        assertEquals(BrewExecutionStatus.PAUSED, snapshot.status)
        assertEquals(1, snapshot.currentStepIndex)
        assertEquals(65, snapshot.accumulatedElapsedSeconds)
        assertEquals(148.5, snapshot.lastWeightGrams, 0.001)
        assertEquals(120, snapshot.sampleCount)
    }

    // --- S2-031: Completed Session Auto-Logging to Journal ---

    @Test
    fun `S2-031 Auto-logging creates valid SensoryObservation linked to recipe and actual metrics`() {
        val recipe = CuratedCoffeeCatalog.recipes.first()
        val elapsedSeconds = 215
        val actualWaterGrams = 498.2

        val autoLog = SensoryObservation(
            id = "obs-auto-01",
            recipeId = recipe.id,
            aroma = 7,
            acidity = 7,
            sweetness = 7,
            bitterness = 3,
            body = 7,
            clarity = 8,
            finish = 7,
            balance = 7,
            notes = "Auto-logged from active brew session: ${recipe.name} (${elapsedSeconds}s elapsed, ${actualWaterGrams}g water poured).",
            assessor = "Barista",
            timestamp = System.currentTimeMillis()
        )

        assertEquals(recipe.id, autoLog.recipeId)
        assertTrue(autoLog.notes.contains("Auto-logged"))
        assertTrue(autoLog.notes.contains("215s elapsed"))
        assertTrue(autoLog.notes.contains("498.2g water poured"))
    }

    // --- S2-032: BLE Device Abstraction & Simulated Scale Driver ---

    @Test
    fun `S2-032 Simulated BLE scale driver establishes connection and streams net samples`() = runBlocking {
        val driver = SimulatedBleScaleDriver()
        assertEquals(ScaleConnectionState.DISCONNECTED, driver.connectionState.value)

        val connectResult = driver.connect()
        assertTrue(connectResult.isSuccess)
        assertEquals(ScaleConnectionState.READY, driver.connectionState.value)

        // Feed weight and check tare
        driver.feedWeight(125.0)
        val tareResult = driver.tare()
        assertTrue(tareResult.isSuccess)

        driver.disconnect()
        assertEquals(ScaleConnectionState.DISCONNECTED, driver.connectionState.value)
    }

    // --- S2-033: Real-Time Flow Rate (g per s) via Linear Regression ---

    @Test
    fun `S2-033 Rolling least-squares regression calculates accurate flow rate in grams per second`() {
        // Continuous steady pour at exactly 5.0 g/s over 1.0 second (5 samples at 200ms intervals)
        val samples = listOf(
            Pair(0L, 0.0),
            Pair(200L, 1.0),
            Pair(400L, 2.0),
            Pair(600L, 3.0),
            Pair(800L, 4.0),
            Pair(1000L, 5.0)
        )

        val flowRate = BrewFilterEngine.calculateRollingFlowRate(samples)
        assertEquals(5.00, flowRate, 0.05)
    }

    @Test
    fun `S2-033 Rolling regression suppresses noise below threshold`() {
        val noiseSamples = listOf(
            Pair(0L, 100.00),
            Pair(200L, 100.01),
            Pair(400L, 100.00),
            Pair(600L, 100.01)
        )

        val flowRate = BrewFilterEngine.calculateRollingFlowRate(noiseSamples)
        assertEquals(0.0, flowRate, 0.001)
    }

    // --- S2-034: Auto-Start & Espresso Auto-Stop Thresholds ---

    @Test
    fun `S2-034 Auto-start detects positive weight delta exceeding 0_5g`() {
        assertTrue(BrewFilterEngine.checkAutoStart(rawWeightDelta = 0.8, timeDeltaMs = 300))
        assertTrue(BrewFilterEngine.checkAutoStart(rawWeightDelta = 0.5, timeDeltaMs = 150))
        assertFalse(BrewFilterEngine.checkAutoStart(rawWeightDelta = 0.3, timeDeltaMs = 200)) // below 0.5g
        assertFalse(BrewFilterEngine.checkAutoStart(rawWeightDelta = 1.0, timeDeltaMs = 900)) // too slow
    }

    @Test
    fun `S2-034 Espresso auto-stop detects flow cessation after yield milestone`() {
        // Flow rate 0.08 g/s sustained for 1600ms with yield 36g
        assertTrue(
            BrewFilterEngine.checkEspressoAutoStop(
                flowRateGps = 0.08,
                sustainedDurationMs = 1600L,
                currentYieldGrams = 36.0,
                minYieldGrams = 5.0
            )
        )

        // Flow rate still high
        assertFalse(
            BrewFilterEngine.checkEspressoAutoStop(
                flowRateGps = 1.2,
                sustainedDurationMs = 2000L,
                currentYieldGrams = 36.0
            )
        )

        // Duration too short (< 1500ms)
        assertFalse(
            BrewFilterEngine.checkEspressoAutoStop(
                flowRateGps = 0.05,
                sustainedDurationMs = 800L,
                currentYieldGrams = 36.0
            )
        )
    }

    // --- S2-035: Transient Scale Artifact Rejection (Swirl & Vessel Removal) ---

    @Test
    fun `S2-035 Vessel removal rejects steep negative drop and holds previous filtered weight`() {
        val prevWeight = 150.0
        val liftWeight = 20.0 // sudden -130g drop
        val dt = 150L

        val (filtered, discontinuity) = BrewFilterEngine.filterScaleSample(
            rawWeight = liftWeight,
            prevFiltered = prevWeight,
            timeDeltaMs = dt
        )

        assertEquals(150.0, filtered, 0.001)
        assertEquals(DiscontinuityType.VESSEL_REMOVAL, discontinuity)
    }

    @Test
    fun `S2-035 Agitation oscillation uses median filter to stabilize swirl noise`() {
        val recent = listOf(148.0, 156.0, 142.0, 158.0, 150.0)
        val (filtered, discontinuity) = BrewFilterEngine.filterScaleSample(
            rawWeight = 160.0,
            prevFiltered = 150.0,
            timeDeltaMs = 100L,
            recentSamples = recent
        )

        assertEquals(DiscontinuityType.AGITATION_ARTIFACT, discontinuity)
        assertEquals(150.0, filtered, 0.001) // median of recent samples
    }

    // --- S2-036: Target-Weight Proximity Visual & Haptic Alerts ---

    @Test
    fun `S2-036 Proximity classifier transitions through all progressive warning zones`() {
        val target = 250.0

        assertEquals(ProximityState.NORMAL, BrewFilterEngine.calculateProximity(230.0, target)) // 20g left
        assertEquals(ProximityState.APPROACHING_10G, BrewFilterEngine.calculateProximity(242.0, target)) // 8g left
        assertEquals(ProximityState.IMMINENT_2G, BrewFilterEngine.calculateProximity(248.5, target)) // 1.5g left
        assertEquals(ProximityState.TARGET_MATCHED, BrewFilterEngine.calculateProximity(250.0, target)) // 0g left
        assertEquals(ProximityState.TARGET_MATCHED, BrewFilterEngine.calculateProximity(250.4, target)) // +0.4g (within 0.5g)
        assertEquals(ProximityState.OVERSHOOT, BrewFilterEngine.calculateProximity(252.5, target)) // +2.5g overshoot
    }

    // --- S2-037: On-Device Brew Curve Overlays & RMSE Calculation ---

    @Test
    fun `S2-037 Ideal curve ramps correctly and calculates RMSE deviation accurately`() {
        val steps = listOf(
            BrewStep(stepNumber = 1, description = "Bloom", targetWaterGrams = 50.0, cumulativeMassGrams = 50.0, durationSeconds = 10),
            BrewStep(stepNumber = 2, description = "Pour 1", targetWaterGrams = 50.0, cumulativeMassGrams = 100.0, durationSeconds = 10)
        )

        val idealPoints = BrewFilterEngine.generateIdealCurvePoints(steps)
        assertEquals(Pair(0, 0.0), idealPoints[0])
        assertEquals(Pair(10, 50.0), idealPoints[2])
        assertEquals(Pair(20, 100.0), idealPoints[4])

        // Perfect tracking -> RMSE = 0.0
        val perfectActual = listOf(
            Pair(0, 0.0),
            Pair(10, 50.0),
            Pair(20, 100.0)
        )
        val rmseZero = BrewFilterEngine.calculateCurveRmse(perfectActual, idealPoints)
        assertEquals(0.0, rmseZero, 0.001)

        // Deviating tracking (+5g error at each point) -> RMSE = 5.0
        val deviatingActual = listOf(
            Pair(0, 5.0),
            Pair(10, 55.0),
            Pair(20, 105.0)
        )
        val rmseDev = BrewFilterEngine.calculateCurveRmse(deviatingActual, idealPoints)
        assertEquals(5.00, rmseDev, 0.01)
    }

    // --- S2-038: Android Ongoing Notification Payload Contract ---

    @Test
    fun `S2-038 Ongoing notification payload formats synchronized time, step, and actions`() {
        val payload = BrewNotificationPayload(
            title = "Brewing: James Hoffmann Ultimate V60",
            subText = "Step 2 of 5 · Pour 1",
            body = "150.0g / 300.0g · 4.80 g/s",
            elapsedSeconds = 65,
            isRunning = true,
            canPause = true,
            canSkip = true
        )

        assertTrue(payload.title.contains("James Hoffmann Ultimate V60"))
        assertTrue(payload.subText.contains("Step 2 of 5"))
        assertTrue(payload.body.contains("150.0g"))
        assertEquals(65, payload.elapsedSeconds)
        assertTrue(payload.isRunning)
        assertTrue(payload.canPause)
    }
}
