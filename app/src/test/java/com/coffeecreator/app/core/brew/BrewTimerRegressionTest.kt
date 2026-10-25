package com.coffeecreator.app.core.brew

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.test.core.app.ApplicationProvider
import com.coffeecreator.app.AppGraph
import com.coffeecreator.app.ui.viewmodel.CoffeeViewModel
import com.coffeecreator.app.ui.viewmodel.defaultFallbackRecipe
import com.coffeecreator.app.ui.viewmodel.initBrewSession
import com.coffeecreator.app.ui.viewmodel.resetBrewSession
import com.coffeecreator.app.ui.viewmodel.skipBrewStep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class BrewTimerRegressionTest {
    private lateinit var model: CoffeeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        model = CoffeeViewModel(AppGraph(ApplicationProvider.getApplicationContext()), SavedStateHandle())
    }

    @After
    fun tearDown() {
        model.viewModelScope.cancel()
        Dispatchers.resetMain()
    }

    @Test
    fun givenSecondPour_whenResettingTimer_thenFirstPourAndItsFullCountdownReturn() {
        model.initBrewSession(defaultFallbackRecipe())
        model.skipBrewStep()
        assertEquals(1, model.state.value.brewSession?.stepProgress?.stepIndex)

        model.resetBrewSession()

        val session = requireNotNull(model.state.value.brewSession)
        assertEquals(0, model.state.value.timerSeconds)
        assertEquals(BrewExecutionStatus.READY_ARMED, session.status)
        assertEquals(0, session.stepProgress?.stepIndex)
        assertEquals(45, session.stepProgress?.stepRemainingSeconds)
        assertEquals("Bloom", session.stepProgress?.currentStep?.description)
    }
}
