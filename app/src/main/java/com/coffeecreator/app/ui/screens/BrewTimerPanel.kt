@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package com.coffeecreator.app.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.coffeecreator.app.core.brew.BrewExecutionStatus
import com.coffeecreator.app.ui.components.TactileButton
import com.coffeecreator.app.ui.components.TactileFilledTonalButton
import com.coffeecreator.app.ui.components.TactileFilterChip
import com.coffeecreator.app.ui.viewmodel.CoffeeUiState
import com.coffeecreator.app.ui.viewmodel.CoffeeViewModel
import com.coffeecreator.app.ui.viewmodel.finishBrewSession
import com.coffeecreator.app.ui.viewmodel.pauseBrewSession
import com.coffeecreator.app.ui.viewmodel.recordManualWeight
import com.coffeecreator.app.ui.viewmodel.resetBrewSession
import com.coffeecreator.app.ui.viewmodel.restartBrewStep
import com.coffeecreator.app.ui.viewmodel.skipBrewStep
import com.coffeecreator.app.ui.viewmodel.startBrewSession
import com.coffeecreator.app.ui.viewmodel.toggleBrewNotifications
import com.coffeecreator.app.ui.viewmodel.toggleHaptics
import com.coffeecreator.app.ui.viewmodel.toggleKeepScreenOn
import com.coffeecreator.app.ui.viewmodel.toggleSound

private const val LARGE_TIMER_FONT_SCALE = 1.5f

/** Manual timer; machine controls and optional scale integration never gate this workflow. */
@Composable
internal fun BrewTimerPanel(state: CoffeeUiState, model: CoffeeViewModel, editRecipe: () -> Unit) {
    val session = state.brewSession
    val progress = session?.stepProgress
    val context = LocalContext.current
    val view = LocalView.current
    val fields = state.draft.fields
    val draftMethod = if (fields["method"] == "Custom") fields["customMethod"].orEmpty() else fields["method"].orEmpty()
    val isEspresso = (session?.recipe?.brewMethod ?: draftMethod).contains("Espresso", ignoreCase = true)
    val targetSeconds = session?.recipe?.brewTimeSeconds ?: fields["seconds"]?.toIntOrNull()
    val completed = session?.status == BrewExecutionStatus.COMPLETED
    val keepAwake = session?.keepScreenOn == true && state.timerRunning
    DisposableEffect(view, keepAwake) {
        val previous = view.keepScreenOn
        view.keepScreenOn = keepAwake
        onDispose { view.keepScreenOn = previous }
    }
    var permissionGranted by rememberSaveable {
        mutableStateOf(
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        )
    }
    var permissionRequested by rememberSaveable { mutableStateOf(false) }
    var startAfterPermission by rememberSaveable { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                permissionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    val notificationPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        permissionGranted = granted
        if (startAfterPermission) model.startBrewSession()
        startAfterPermission = false
    }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            if (isEspresso) "Manual espresso timer" else "Brew timer",
            Modifier.semantics { heading() },
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            session?.recipe?.name ?: state.draft.fields["name"].orEmpty(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (isEspresso) {
            Text(
                "Start the timer with the machine. Stop the machine yourself, then Finish & Log. No machine connection is required.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("timer:manual_machine")
            )
            session?.recipe?.equipment?.takeIf { it.isNotBlank() }?.let {
                Text(it, style = MaterialTheme.typography.labelLarge)
            }
        }
        Surface(
            modifier = Modifier.fillMaxWidth().testTag("timer:display"),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = MaterialTheme.shapes.medium
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    when {
                        state.timerRunning -> "Running"
                        session?.status == BrewExecutionStatus.PAUSED -> "Paused"
                        completed -> "Completed"
                        else -> "Ready"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )
                Text("Elapsed", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    formatBrewTime(state.timerSeconds),
                    style = (
                        if (LocalDensity.current.fontScale >= LARGE_TIMER_FONT_SCALE) {
                            MaterialTheme.typography.headlineLarge
                        } else {
                            MaterialTheme.typography.displayMedium
                        }
                        ).copy(fontFeatureSettings = "tnum"),
                    modifier = Modifier.testTag("timer:elapsed")
                )
                if (isEspresso) {
                    Text(
                        if (targetSeconds != null && targetSeconds > 0) {
                            "Recipe target ${formatBrewTime(targetSeconds)}"
                        } else {
                            "Set a target time in Recipe."
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (state.timerSeconds >= (session?.recipe?.brewTimeSeconds ?: Int.MAX_VALUE) && !completed) {
                        Text(
                            "Target time reached. Finish when the shot is complete.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                        )
                    }
                }
            }
        }
        session?.recoveryNotice?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.testTag("timer:recovery"))
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = if (LocalDensity.current.fontScale >= LARGE_TIMER_FONT_SCALE) 1 else 2
        ) {
            TactileButton(
                onClick = {
                    if (state.timerRunning) {
                        model.pauseBrewSession()
                    } else if (!permissionGranted && !permissionRequested && session?.notificationEnabled != false) {
                        permissionRequested = true
                        startAfterPermission = true
                        notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        model.startBrewSession()
                    }
                },
                enabled = !completed,
                modifier = Modifier.weight(1f).heightIn(min = 56.dp).testTag("timer:toggle"),
                colors = ButtonDefaults.buttonColors()
            ) {
                Text(
                    when {
                        state.timerRunning -> "Pause Session"
                        session?.status == BrewExecutionStatus.PAUSED -> "Resume Session"
                        else -> "Start Session"
                    },
                    style = MaterialTheme.typography.titleMedium
                )
            }
            TactileFilledTonalButton(
                onClick = model::finishBrewSession,
                enabled = session != null && !completed && state.timerSeconds > 0,
                modifier = Modifier.weight(1f).heightIn(min = 56.dp).testTag("timer:finish_and_log")
            ) { Text("Finish & Log") }
        }
        if (!isEspresso && progress != null) {
            Column(
                Modifier.fillMaxWidth().testTag("timer:step_card"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Step ${progress.stepIndex + 1} of ${progress.totalSteps} · ${progress.currentStep.action}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
                )
                Text(
                    if (completed) {
                        "Plan complete"
                    } else {
                        "${formatBrewTime(progress.stepRemainingSeconds)} " +
                            if (progress.nextStep == null) "until plan complete" else "to next step"
                    },
                    style = MaterialTheme.typography.headlineSmall.copy(fontFeatureSettings = "tnum")
                )
                LinearProgressIndicator(
                    progress = {
                        if (progress.currentStep.durationSeconds > 0) {
                            (progress.stepElapsedSeconds.toFloat() / progress.currentStep.durationSeconds).coerceIn(0f, 1f)
                        } else {
                            0f
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "Pour +${progress.stepTargetWaterGrams} g · cumulative target ${progress.cumulativeTargetWaterGrams} g",
                    style = MaterialTheme.typography.bodyLarge
                )
                progress.currentStep.temperatureCelsius?.let { Text("Water $it °C", style = MaterialTheme.typography.bodyMedium) }
                if (progress.currentStep.description.isNotBlank()) {
                    Text(progress.currentStep.description, style = MaterialTheme.typography.bodyMedium)
                }
                progress.nextStep?.let { next ->
                    Text(
                        "Next: ${next.action} · +${next.targetWaterGrams} g · ${next.durationSeconds} s",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        var confirmReset by rememberSaveable { mutableStateOf(false) }
        if (confirmReset) {
            AlertDialog(
                onDismissRequest = { confirmReset = false },
                title = { Text("Reset this timer?") },
                text = { Text("The current elapsed time will be cleared. Saved brews are kept.") },
                confirmButton = {
                    TextButton(onClick = {
                        model.resetBrewSession()
                        confirmReset = false
                    }) { Text("Reset timer") }
                },
                dismissButton = { TextButton(onClick = { confirmReset = false }) { Text("Keep brewing") } }
            )
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            if (!isEspresso) {
                OutlinedButton(
                    onClick = model::skipBrewStep,
                    enabled = session != null && !completed,
                    modifier = Modifier.heightIn(min = 48.dp).testTag("timer:skip_step")
                ) { Text("Next step") }
                OutlinedButton(
                    onClick = model::restartBrewStep,
                    enabled = session != null && !completed,
                    modifier = Modifier.heightIn(min = 48.dp).testTag("timer:restart_step")
                ) { Text("Restart step") }
            }
            OutlinedButton(
                onClick = {
                    if (!completed && state.timerSeconds > 0) confirmReset = true else model.resetBrewSession()
                },
                modifier = Modifier.heightIn(min = 48.dp).testTag("timer:reset")
            ) { Text("Reset timer") }
            TextButton(onClick = editRecipe, modifier = Modifier.heightIn(min = 48.dp)) { Text("Edit recipe") }
        }
        var showWeightEntry by rememberSaveable(session?.sessionId) { mutableStateOf(false) }
        var manualWeightInput by rememberSaveable(session?.sessionId) { mutableStateOf("") }
        val manualWeight = manualWeightInput.replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() && it >= 0.0 }
        TextButton(
            onClick = { showWeightEntry = !showWeightEntry },
            modifier = Modifier.heightIn(min = 48.dp).testTag("timer:manual_weight_toggle")
        ) { Text(if (showWeightEntry) "Hide manual scale reading" else "Add manual scale reading") }
        if (session?.manualWeightRecorded == true) {
            Text(
                "Latest manual scale reading: ${session.currentWeightGrams} g",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("timer:manual_weight_value")
            )
        }
        if (showWeightEntry) {
            OutlinedTextField(
                value = manualWeightInput,
                onValueChange = { manualWeightInput = it },
                label = { Text("Scale reading (g)") },
                supportingText = {
                    Text("Enter the grams you read on your scale. This does not set measured beverage mass or TDS.")
                },
                isError = manualWeightInput.isNotBlank() && manualWeight == null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth().testTag("timer:manual_weight_input")
            )
            OutlinedButton(
                onClick = { manualWeight?.let { if (model.recordManualWeight(it)) showWeightEntry = false } },
                enabled = manualWeight != null && !completed,
                modifier = Modifier.heightIn(min = 48.dp).testTag("timer:manual_weight_record")
            ) { Text("Record reading") }
        }
        HorizontalDivider()
        Text("Pour reminders", style = MaterialTheme.typography.titleSmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            TactileFilterChip(
                selected = session?.audioEnabled ?: true,
                onClick = model::toggleSound,
                label = { Text("Sound") },
                modifier = Modifier.testTag("timer:toggle_sound")
            )
            TactileFilterChip(
                selected = session?.hapticsEnabled ?: true,
                onClick = model::toggleHaptics,
                label = { Text("Vibration") },
                modifier = Modifier.testTag("timer:toggle_haptics")
            )
            TactileFilterChip(
                selected = session?.notificationEnabled ?: true,
                onClick = {
                    val enabling = session?.notificationEnabled == false
                    model.toggleBrewNotifications()
                    if (enabling && !permissionGranted) {
                        permissionRequested = true
                        notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                },
                label = { Text("Notification") },
                modifier = Modifier.testTag("timer:toggle_notifications")
            )
            TactileFilterChip(
                selected = session?.keepScreenOn ?: false,
                onClick = model::toggleKeepScreenOn,
                label = { Text("Keep screen on") },
                modifier = Modifier.testTag("timer:toggle_awake")
            )
        }
        Text(
            "Sound and vibration use Android notifications. For silent reminders, keep Notification on and turn Sound and Vibration off. " +
                "Android notification and Do Not Disturb settings apply.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (!permissionGranted && permissionRequested) {
            Text(
                "Android notifications are blocked. Reminders are visual here; manual timing remains available.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.testTag("timer:notification_denied")
            )
            TextButton(
                onClick = {
                    context.startActivity(
                        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    )
                },
                modifier = Modifier.heightIn(min = 48.dp)
            ) { Text("Notification settings") }
        }
    }
}
