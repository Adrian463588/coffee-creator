package com.coffeecreator.app

import android.app.Application
import android.content.ComponentCallbacks2
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@HiltAndroidApp
class CoffeeApplication : Application() {
    @Inject lateinit var graph: AppGraph
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    fun persistDraft(json: String) {
        applicationScope.launch {
            try {
                graph.repository.saveSetting("draft", json)
            } catch (cancelled: kotlinx.coroutines.CancellationException) {
                throw cancelled
            } catch (boundaryFailure: Exception) {
                graph.repository.reportFailure(boundaryFailure)
            }
        }
    }
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level >= ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN || level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW ||
            level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL
        ) {
            graph.native.cancel()
            applicationScope.launch { graph.native.unload() }
        }
    }
}
