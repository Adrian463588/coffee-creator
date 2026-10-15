package com.coffeecreator.app.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CoffeeBenchmark {
    @get:Rule val benchmark = MacrobenchmarkRule()

    @Test fun coldStartup() = benchmark.measureRepeated(
        packageName = PACKAGE, metrics = listOf(StartupTimingMetric()), iterations = 5,
        startupMode = StartupMode.COLD, compilationMode = CompilationMode.None(),
        setupBlock = { pressHome() }
    ) { startActivityAndWait() }

    @Test fun formulationScroll() = benchmark.measureRepeated(
        packageName = PACKAGE, metrics = listOf(FrameTimingMetric()), iterations = 5,
        compilationMode = CompilationMode.None(),
        setupBlock = {
            startActivityAndWait()
            device.wait(Until.hasObject(By.text("Brew")), 5000)
            (device.findObject(By.text("Brew")) ?: device.findObject(By.text("Formulate"))).click()
            device.waitForIdle()
        }
    ) {
        val x = device.displayWidth / 2
        repeat(3) {
            device.swipe(x, device.displayHeight * 3 / 4, x, device.displayHeight / 3, 20)
            device.waitForIdle()
        }
    }

    companion object { const val PACKAGE = "com.coffeecreator.app" }
}
