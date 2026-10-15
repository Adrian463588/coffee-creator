package com.coffeecreator.app.benchmark

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CoffeeBaselineProfile {
    @get:Rule val profile = BaselineProfileRule()

    @Test fun manualRoutes() = profile.collect(packageName = CoffeeBenchmark.PACKAGE) {
        pressHome()
        startActivityAndWait()
        device.wait(Until.hasObject(By.text("Brew")), 5000)
        listOf("Brew", "Library", "Journal", "Home").forEach { label ->
            val target = device.findObject(By.text(label)) ?: error("Missing route $label")
            target.click()
            device.waitForIdle()
        }
    }
}
