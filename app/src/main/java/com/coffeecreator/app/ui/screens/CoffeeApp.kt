@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.coffeecreator.app.ui.screens

import android.content.Intent
import android.graphics.Color as AndroidColor
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.coffeecreator.app.core.ai.personalization.AcidityTolerance
import com.coffeecreator.app.core.ai.personalization.BaristaPersonaPreset
import com.coffeecreator.app.core.ai.personalization.BitternessSensitivity
import com.coffeecreator.app.core.ai.personalization.BodyPreference
import com.coffeecreator.app.core.ai.personalization.ElevationUnit
import com.coffeecreator.app.core.ai.personalization.IngredientSafetyLevel
import com.coffeecreator.app.core.ai.personalization.PalateQuizAnswers
import com.coffeecreator.app.core.ai.personalization.RoastPreference
import com.coffeecreator.app.core.ai.personalization.SensoryFeedbackDiagnosis
import com.coffeecreator.app.core.ai.personalization.SweetnessPriority
import com.coffeecreator.app.core.ble.ScaleConnectionState
import com.coffeecreator.app.core.brew.BrewExecutionStatus
import com.coffeecreator.app.core.brew.ProximityState
import com.coffeecreator.app.core.catalog.BagInventoryItem
import com.coffeecreator.app.core.catalog.BagStatus
import com.coffeecreator.app.core.catalog.ConflictResolutionStrategy
import com.coffeecreator.app.core.catalog.DetailedBeanRecord
import com.coffeecreator.app.core.catalog.ProcessMethod
import com.coffeecreator.app.core.catalog.RoastLevel
import com.coffeecreator.app.core.catalog.RoastRestCalculator
import com.coffeecreator.app.core.filesystem.BuiltInAssets
import com.coffeecreator.app.core.journal.CafeCupEntry
import com.coffeecreator.app.core.model.CoffeeBean
import com.coffeecreator.app.core.model.Ingredient
import com.coffeecreator.app.core.model.ModelStatus
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.core.reliability.AppLanguage
import com.coffeecreator.app.core.reliability.LogLevel
import com.coffeecreator.app.core.reliability.MassUnit
import com.coffeecreator.app.core.reliability.ModuleStatus
import com.coffeecreator.app.core.reliability.OfflineHelpCatalog
import com.coffeecreator.app.core.reliability.TemperatureUnit
import com.coffeecreator.app.core.reliability.VolumeUnit
import com.coffeecreator.app.core.sensory.CuppingAnalyticsEngine
import com.coffeecreator.app.core.sensory.CuppingTemplateType
import com.coffeecreator.app.core.sensory.DegassingPhase
import com.coffeecreator.app.core.sensory.GreenCoffeeDefectEntry
import com.coffeecreator.app.core.sensory.GreenDefectType
import com.coffeecreator.app.core.sensory.MaskingMode
import com.coffeecreator.app.core.sensory.RoastRestObservation
import com.coffeecreator.app.core.sensory.SensoryTaxonomyRegistry
import com.coffeecreator.app.core.sensory.SessionStatus
import com.coffeecreator.app.ui.theme.CoffeeCreatorTheme
import com.coffeecreator.app.ui.viewmodel.CoffeeUiState
import com.coffeecreator.app.ui.viewmodel.CoffeeViewModel
import com.coffeecreator.app.ui.viewmodel.RecipeDraft
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.UUID

private data class Destination(val route: String, val label: String, val icon: ImageVector)
private const val DECIMAL_GIGABYTE_BYTES = 1_000_000_000.0
private const val LICENSE_SUMMARY_CHARS = 120
private val destinations = listOf(
    Destination("Home", "Home", Icons.Default.Home),
    Destination("Formulate", "Brew", Icons.Default.Coffee),
    Destination("AI Chat", "Chat", Icons.AutoMirrored.Filled.Chat),
    Destination("Library", "Library", Icons.AutoMirrored.Filled.LibraryBooks),
    Destination("Journal", "Journal", Icons.Default.Book)
)

@Composable
fun CoffeeApp(model: CoffeeViewModel, state: CoffeeUiState) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route ?: "Home"
    var brewResetKey by rememberSaveable { mutableStateOf(0) }
    val navigate: (String) -> Unit = { target ->
        navController.navigate(target) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
    var menu by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = route != "Home") { navigate("Home") }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val expanded = maxWidth >= 600.dp
        Scaffold(
            topBar = {
                TopAppBar(title = {
                    Column {
                        Text("Coffee Creator")
                        Text(
                            destinations.find { it.route == route }?.label ?: route,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }, actions = {
                    IconButton(onClick = { menu = true }, modifier = Modifier.testTag("nav:more")) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Open Knowledge, Models, Sensory and Settings")
                    }
                    DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                        listOf("Knowledge", "Models", "Sensory", "Settings").forEach { target ->
                            DropdownMenuItem(text = { Text(target) }, onClick = {
                                navigate(target)
                                menu = false
                            }, modifier = Modifier.testTag("nav:$target"))
                        }
                    }
                })
            },
            bottomBar = {
                if (!expanded) {
                    NavigationBar {
                        destinations.forEach { destination ->
                            NavigationBarItem(
                                selected = route == destination.route,
                                onClick = { navigate(destination.route) },
                                icon = { Icon(destination.icon, contentDescription = null) },
                                label = { Text(destination.label) },
                                modifier = Modifier.testTag("nav:${destination.route}")
                            )
                        }
                    }
                }
            }
        ) { padding ->
            Row(Modifier.fillMaxSize().padding(padding)) {
                if (expanded) {
                    NavigationRail(Modifier.fillMaxHeight()) {
                        destinations.forEach { destination ->
                            NavigationRailItem(
                                selected = route == destination.route,
                                onClick = { navigate(destination.route) },
                                icon = { Icon(destination.icon, null) },
                                label = { Text(destination.label) },
                                modifier = Modifier.testTag("nav:${destination.route}")
                            )
                        }
                    }
                }
                Column(Modifier.weight(1f)) {
                    if (state.busy) {
                        LinearProgressIndicator(Modifier.fillMaxWidth())
                        Text(state.progress, Modifier.padding(horizontal = 16.dp).semantics { liveRegion = LiveRegionMode.Polite })
                    }
                    AnimatedVisibility(
                        visible = state.error != null || state.notice != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Card(
                            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (state.error != null) {
                                    MaterialTheme.colorScheme.errorContainer
                                } else {
                                    MaterialTheme.colorScheme.secondaryContainer
                                }
                            )
                        ) {
                            Row(
                                Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    if (state.error != null) Icons.Default.Info else Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = if (state.error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    state.error ?: state.notice.orEmpty(),
                                    Modifier.weight(1f).semantics {
                                        liveRegion = LiveRegionMode.Polite
                                    },
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                TextButton(onClick = model::clearNotice) { Text("Dismiss") }
                            }
                        }
                    }
                    AnimatedVisibility(
                        visible = route == "Formulate" && state.canUndoDraft,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        TextButton(
                            onClick = model::undoDraftChange,
                            enabled = !state.busy,
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                        ) { Text("Undo draft change") }
                    }
                    NavHost(navController, startDestination = "Home", modifier = Modifier.weight(1f)) {
                        composable("Home") {
                            Home(state, model) { target ->
                                if (target == "Formulate") brewResetKey++
                                navigate(target)
                            }
                        }
                        composable("Formulate") { Formulate(state, model, brewResetKey) }
                        composable("Library") { Library(state, model) }
                        composable("Journal") {
                            Journal(state, model) { recipe ->
                                model.editRecipe(recipe)
                                brewResetKey++
                                navigate("Formulate")
                            }
                        }
                        composable("Sensory") { Sensory(state, model) }
                        composable("Knowledge") { Knowledge(state, model) }
                        composable("Models") { Models(state, model) }
                        composable("AI Chat") { Chat(state, model) }
                        composable("Settings") { Settings(state, model) }
                    }
                }
            }
        }
    }
    state.deletion?.let { deletion ->
        AlertDialog(
            onDismissRequest = model::cancelDeletion,
            title = { Text("Delete ${deletion.first}?") },
            text = {
                Text(
                    if (deletion.first ==
                        "all application records"
                    ) {
                        "Delete catalog, recipes, observations, chat and imported source records from this app? " +
                            "Model files and exported backups remain in your directories. Export current records first " +
                            "if needed."
                    } else {
                        "Delete this record and associated local data? Export a backup first if you need recovery."
                    }
                )
            },
            confirmButton = { TextButton(onClick = model::confirmDeletion, enabled = !state.busy) { Text("Delete") } },
            dismissButton = { TextButton(onClick = model::cancelDeletion) { Text("Keep record") } }
        )
    }
    state.inspectedChunk?.let { chunk ->
        val context = LocalContext.current
        AlertDialog(onDismissRequest = { model.inspectChunk(null) }, title = { Text(chunk.citationText) }, text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text("Imported source excerpt; document instructions are untrusted data.", style = MaterialTheme.typography.labelMedium)
                Text(chunk.content)
                state.sourceOpenError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        }, confirmButton = {
            TextButton(onClick = { model.openOriginal(context) }) { Text("Open original") }
        }, dismissButton = { TextButton(onClick = { model.inspectChunk(null) }) { Text("Close") } })
    }
}

@Composable
private fun Page(tag: String, modifier: Modifier = Modifier, content: LazyListScope.() -> Unit) {
    LazyColumn(
        modifier.fillMaxSize().padding(horizontal = 16.dp).testTag("page:$tag"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(Modifier.height(4.dp)) }
        content()
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun Heading(title: String, detail: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            title,
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(detail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun InfoCard(title: String, detail: String, content: @Composable () -> Unit = {}) {
    Card(
        Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy)),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, Modifier.semantics { heading() }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            if (detail.isNotBlank()) {
                Text(detail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            content()
        }
    }
}

@Composable
private fun LivePulseIndicator(isActive: Boolean, modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary) {
    if (isActive) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.6f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "pulseScale"
        )
        val pulseAlpha by infiniteTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 0.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "pulseAlpha"
        )
        Box(
            modifier = modifier.size(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = color.copy(alpha = pulseAlpha),
                    radius = (size.minDimension / 2f) * pulseScale
                )
                drawCircle(
                    color = color,
                    radius = size.minDimension / 3.2f
                )
            }
        }
    } else {
        Box(
            modifier = modifier.size(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = color.copy(alpha = 0.35f),
                    radius = size.minDimension / 3.2f
                )
            }
        }
    }
}

@Composable
private fun TactileFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "chipTactileScale"
    )
    FilterChip(
        selected = selected,
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        label = label,
        leadingIcon = leadingIcon,
        enabled = enabled,
        interactionSource = interactionSource,
        modifier = modifier
            .heightIn(min = 48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    )
}

@Composable
private fun TactileButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(8.dp),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "buttonTactileScale"
    )
    Button(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        enabled = enabled,
        colors = colors,
        shape = shape,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        modifier = modifier
            .heightIn(min = 48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        content = content
    )
}

@Composable
private fun TactileOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(8.dp),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    border: BorderStroke? = null,
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "outlinedButtonTactileScale"
    )
    OutlinedButton(
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            onClick()
        },
        enabled = enabled,
        shape = shape,
        contentPadding = contentPadding,
        border = border ?: BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
        interactionSource = interactionSource,
        modifier = modifier
            .heightIn(min = 48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        content = content
    )
}

@Composable
private fun TactileCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(12.dp),
    colors: CardColors = CardDefaults.cardColors(),
    border: BorderStroke? = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f)),
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    if (onClick != null) {
        val haptic = LocalHapticFeedback.current
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.98f else 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
            label = "cardTactileScale"
        )
        Card(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            },
            modifier = modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy)),
            shape = shape,
            colors = colors,
            border = border,
            interactionSource = interactionSource,
            content = content
        )
    } else {
        Card(
            modifier = modifier.animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy)),
            shape = shape,
            colors = colors,
            border = border,
            content = content
        )
    }
}

@Composable
private fun Field(label: String, value: String, onChange: (String) -> Unit, tag: String, numeric: Boolean = false) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().testTag(tag),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = if (numeric) KeyboardType.Decimal else KeyboardType.Text)
    )
}

@Composable
private fun Select(label: String, selected: String, choices: List<Pair<String, String>>, onSelect: (String) -> Unit, tag: String) {
    val haptic = LocalHapticFeedback.current
    var open by rememberSaveable { mutableStateOf(false) }
    val arrowRotation by animateFloatAsState(
        targetValue = if (open) 180f else 0f,
        animationSpec = tween(150),
        label = "dropdownArrow"
    )
    Column {
        TactileOutlinedButton(
            onClick = { open = true },
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag(tag),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "$label: ${choices.find { it.first == selected }?.second ?: selected.ifBlank { "Choose" }}",
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier.rotate(arrowRotation)
                )
            }
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            choices.forEach { (id, text) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = if (id == selected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = if (id == selected) {
                        { Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                    } else {
                        null
                    },
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onSelect(id)
                        open = false
                    }
                )
            }
        }
    }
}

@Composable
private fun Home(state: CoffeeUiState, model: CoffeeViewModel, navigate: (String) -> Unit) = Page("Home") {
    item {
        Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.primaryContainer) {
            Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Coffee, contentDescription = null)
                    Text("YOUR COFFEE WORKBENCH", style = MaterialTheme.typography.labelLarge)
                }
                Text(
                    "Make every brew repeatable",
                    Modifier.semantics { heading() },
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text("Plan a recipe, measure your brew, then keep what you learned.", style = MaterialTheme.typography.bodyLarge)
                Button(
                    onClick = {
                        model.newRecipe()
                        navigate("Formulate")
                    },
                    enabled = state.ready && !state.busy,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("home:new")
                ) { Text("New recipe") }
            }
        }
    }
    if (!state.onboarded) {
        item {
            InfoCard(
                "Offline by design",
                "Manual tools work without an AI model. Documents and chat stay on this device. Only a " +
                    "download you request uses the network."
            ) {
                Button(onClick = model::finishOnboarding, enabled = !state.busy) { Text("Continue offline") }
            }
        }
    }
    item {
        InfoCard(
            "Ready for your next brew",
            "${state.recipes.size} saved revisions · ${state.documents.size} imported sources"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(if (state.ready) "Local records ready" else "Opening local records", style = MaterialTheme.typography.bodyMedium)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.OfflineBolt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        state.loadedModel?.let { "Local AI loaded: $it" } ?: if (state.ready) {
                            "Manual mode ready · AI model optional"
                        } else {
                            "AI model optional · opening local records"
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
    item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "WORKBENCH MODULES",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            val navItems = listOf(
                Triple("AI Chat", "Offline coffee intelligence", Icons.AutoMirrored.Filled.Chat),
                Triple("Knowledge", "Imported guides & RAG", Icons.AutoMirrored.Filled.LibraryBooks),
                Triple("Models", "Local GGUF / ONNX assets", Icons.Default.Memory),
                Triple("Sensory", "Tasting radar & observations", Icons.Default.Psychology),
                Triple("Settings", "Storage, export & reset", Icons.Default.Settings)
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                navItems.forEach { (target, subtitle, icon) ->
                    Surface(
                        onClick = { navigate(target) },
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .heightIn(min = 48.dp)
                            .testTag("home:nav:$target")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Column {
                                Text(target, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                                Text(
                                    subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    if (state.recipes.isEmpty()) {
        item {
            InfoCard("No brews recorded", "Create a recipe and enter measurements only when you have measured them.")
        }
    }
    items(state.recipes.take(3), key = { it.id }) { recipe ->
        InfoCard(
            "${recipe.name} · v${recipe.version}",
            "${recipe.doseGrams} g coffee / ${recipe.waterGrams} g water · ${recipe.brewMethod}"
        ) {
            TextButton(onClick = {
                model.editRecipe(recipe)
                navigate("Formulate")
            }) { Text("Repeat / edit") }
        }
    }
}

@Composable
private fun Formulate(state: CoffeeUiState, model: CoffeeViewModel, resetKey: Int) {
    val context = LocalContext.current
    val fields = state.draft.fields
    var selectedSection by rememberSaveable(resetKey) { mutableStateOf(BrewSection.Recipe.name) }
    val section = BrewSection.valueOf(selectedSection)
    var ingredientId by rememberSaveable { mutableStateOf("") }
    var quantity by rememberSaveable { mutableStateOf("") }
    var unit by rememberSaveable { mutableStateOf("g") }
    var density by rememberSaveable { mutableStateOf("") }
    var science by rememberSaveable { mutableStateOf("Dilution") }
    var scienceInputs by rememberSaveable { mutableStateOf(emptyMap<String, String>()) }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val compactHeight = 480.dp
        val largeFontScale = 1.5f
        val scrollSections = maxHeight < compactHeight || LocalDensity.current.fontScale >= largeFontScale
        Column(Modifier.fillMaxSize()) {
            if (!scrollSections) {
                BrewSectionTabs(section, Modifier.padding(horizontal = 16.dp)) { selectedSection = it.name }
            }
            Box(Modifier.weight(1f).testTag("brew:content:${section.name}")) {
                androidx.compose.runtime.key(section) {
                    Page("Formulate") {
                        if (scrollSections) {
                            item(key = "brew:sections") { BrewSectionTabs(section) { selectedSection = it.name } }
                        }
                        when (section) {
                            BrewSection.Recipe -> {
                                item {
                                    BrewCard("Build your recipe", "Planned inputs. Each save keeps an immutable revision.") {
                                        Field("Recipe name", fields.getValue("name"), { model.updateField("name", it) }, "form:name")
                                        Select(
                                            "Coffee bean",
                                            fields.getValue("bean"),
                                            listOf("" to "Unspecified") + state.beans.map { it.id to it.name },
                                            { model.updateField("bean", it) },
                                            "form:bean"
                                        )
                                        var customBeanText by rememberSaveable { mutableStateOf("") }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedTextField(
                                                value = customBeanText,
                                                onValueChange = { customBeanText = it },
                                                label = { Text("Custom coffee bean") },
                                                singleLine = true,
                                                modifier = Modifier.weight(1f).testTag("form:custom_bean")
                                            )
                                            Button(
                                                onClick = {
                                                    val name = customBeanText.trim()
                                                    if (name.isNotBlank()) {
                                                        val existing = state.beans.find { it.name.equals(name, ignoreCase = true) }
                                                        if (existing != null) {
                                                            model.updateField("bean", existing.id)
                                                        } else {
                                                            val id = java.util.UUID.randomUUID().toString()
                                                            model.saveBean(
                                                                id,
                                                                mapOf(
                                                                    "name" to name,
                                                                    "variety" to "Custom",
                                                                    "provenance" to "Custom user lot"
                                                                )
                                                            )
                                                            model.updateField("bean", id)
                                                        }
                                                    }
                                                },
                                                enabled = customBeanText.isNotBlank() && !state.busy,
                                                modifier = Modifier.heightIn(min = 48.dp).testTag("form:add_custom_bean")
                                            ) {
                                                Text("Apply bean")
                                            }
                                        }
                                        Select(
                                            "Brew method preset",
                                            fields.getValue("method"),
                                            RecipeDraft.standardMethods().map { it to it },
                                            { model.updateField("method", it) },
                                            "form:method"
                                        )
                                        if (fields.getValue("method") == "Custom") {
                                            Field(
                                                "Custom brew apparatus name",
                                                fields.getValue("customMethod"),
                                                { model.updateField("customMethod", it) },
                                                "form:custom_method"
                                            )
                                        } else {
                                            Field(
                                                "Brew method (or custom apparatus)",
                                                fields.getValue("method"),
                                                { model.updateField("method", it) },
                                                "form:custom_method"
                                            )
                                        }
                                        Field("Dose (g)", fields.getValue("dose"), { model.updateField("dose", it) }, "form:dose", true)
                                        Field("Brew water (g)", fields.getValue("water"), {
                                            model.updateField("water", it)
                                        }, "form:water", true)
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Scale:", style = MaterialTheme.typography.labelMedium)
                                            OutlinedButton(
                                                onClick = { model.scaleDraft(0.5) },
                                                modifier = Modifier.heightIn(min = 36.dp).testTag("form:scale:0.5")
                                            ) { Text("0.5x") }
                                            OutlinedButton(
                                                onClick = { model.scaleDraft(1.5) },
                                                modifier = Modifier.heightIn(min = 36.dp).testTag("form:scale:1.5")
                                            ) { Text("1.5x") }
                                            OutlinedButton(
                                                onClick = { model.scaleDraft(2.0) },
                                                modifier = Modifier.heightIn(min = 36.dp).testTag("form:scale:2.0")
                                            ) { Text("2.0x") }
                                        }
                                    }
                                }
                                item {
                                    BrewCard("Brew timer", "%02d:%02d".format(state.timerSeconds / 60, state.timerSeconds % 60)) {
                                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(onClick = model::toggleTimer, modifier = Modifier.heightIn(min = 48.dp)) {
                                                Text(if (state.timerRunning) "Pause timer" else "Start timer")
                                            }
                                            OutlinedButton(onClick = model::resetTimer, modifier = Modifier.heightIn(min = 48.dp)) {
                                                Text("Reset timer")
                                            }
                                        }
                                    }
                                }
                                item {
                                    BrewCard("Equipment & notes", "Record the setup so another brew can repeat it.") {
                                        Field("Equipment", fields.getValue("equipment"), {
                                            model.updateField("equipment", it)
                                        }, "form:equipment")
                                        Field("Grind description", fields.getValue("grind"), {
                                            model.updateField("grind", it)
                                        }, "form:grind")
                                        Field("Recipe notes", fields.getValue("notes"), { model.updateField("notes", it) }, "form:notes")
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = {
                                                    val r = runCatching { state.draft.build(state.ingredients) }.getOrNull()
                                                    if (r != null) model.shareRecipe(context, r, formatJson = false)
                                                },
                                                modifier = Modifier.weight(1f).heightIn(min = 44.dp).testTag("form:share_card")
                                            ) { Text("Share Card") }
                                            OutlinedButton(
                                                onClick = {
                                                    val r = runCatching { state.draft.build(state.ingredients) }.getOrNull()
                                                    if (r != null) model.shareRecipe(context, r, formatJson = true)
                                                },
                                                modifier = Modifier.weight(1f).heightIn(min = 44.dp).testTag("form:share_json")
                                            ) { Text("Share JSON") }
                                        }
                                    }
                                }
                                item {
                                    TextButton(onClick = model::newRecipe, modifier = Modifier.heightIn(min = 48.dp).testTag("form:new")) {
                                        Text("Start a separate recipe")
                                    }
                                }
                            }
                            BrewSection.Measurements -> {
                                item {
                                    BrewCard(
                                        "Measured beverage",
                                        "Optional. Leave blank until an instrument or scale supplies the value."
                                    ) {
                                        Field("Measured beverage mass (g)", fields.getValue("beverage"), {
                                            model.updateField("beverage", it)
                                        }, "form:beverage", true)
                                        Field("Measured TDS (% w/w)", fields.getValue("tds"), {
                                            model.updateField("tds", it)
                                        }, "form:tds", true)
                                    }
                                }
                                item {
                                    BrewCard(
                                        "Optical refractometer lab (Brix → TDS)",
                                        "Convert optical refractive index (°Bx) using documented instrument calibration profiles."
                                    ) {
                                        Field(
                                            "Measured Brix (%)",
                                            fields.getValue("brix"),
                                            { model.updateField("brix", it) },
                                            "form:brix",
                                            true
                                        )
                                        Select(
                                            "Calibration profile",
                                            fields.getValue("brixProfile"),
                                            listOf(
                                                "Standard Coffee (0.85)",
                                                "Atago PAL-COFFEE (0.84)",
                                                "VST LAB Coffee III (0.857)",
                                                "Generic Sucrose Equivalent (0.85)"
                                            ).map { it to it },
                                            { model.updateField("brixProfile", it) },
                                            "form:brix_profile"
                                        )
                                        Button(
                                            onClick = model::convertDraftBrix,
                                            enabled = fields.getValue("brix").isNotBlank() && !state.busy,
                                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("form:convert_brix")
                                        ) {
                                            Text("Convert Brix → TDS & Apply")
                                        }
                                    }
                                }
                                item { InfoCard("Calculated science · not a measurement", state.calculation) }
                                item {
                                    BrewCard(
                                        "Process inputs",
                                        "Describe the brew. Estimated extraction retains method-specific assumptions."
                                    ) {
                                        Field("Brew temperature (°C)", fields.getValue("temperature"), {
                                            model.updateField("temperature", it)
                                        }, "form:temperature", true)
                                        Field("Pressure (bar), optional", fields.getValue("pressure"), {
                                            model.updateField("pressure", it)
                                        }, "form:pressure", true)
                                        Field("Brew time (seconds)", fields.getValue("seconds"), {
                                            model.updateField("seconds", it)
                                        }, "form:seconds", true)
                                        Field("Bypass water (g)", fields.getValue("bypass"), {
                                            model.updateField("bypass", it)
                                        }, "form:bypass", true)
                                        Field("Agitation / pour routine", fields.getValue("agitation"), {
                                            model.updateField("agitation", it)
                                        }, "form:agitation")
                                    }
                                }
                            }
                            BrewSection.Ingredients -> {
                                item {
                                    BrewCard(
                                        "Ingredient additions",
                                        "Only confirmed food-grade ingredients. Known density is required for mL."
                                    ) {
                                        Select("Ingredient", ingredientId, state.ingredients.map { it.id to it.name }, {
                                            ingredientId = it
                                        }, "form:ingredient")
                                        Field("Ingredient quantity", quantity, { quantity = it }, "form:quantity", true)
                                        Select("Unit", unit, listOf("g", "mg", "mL").map { it to it }, { unit = it }, "form:unit")
                                        Field("Density (g/mL), optional override", density, { density = it }, "form:density", true)
                                        OutlinedButton(
                                            onClick = { model.addLine(ingredientId, quantity, unit, density) },
                                            enabled = ingredientId.isNotBlank() && !state.busy,
                                            modifier = Modifier.heightIn(min = 48.dp)
                                        ) { Text("Add ingredient") }
                                    }
                                }
                                if (state.draft.lines.isEmpty()) {
                                    item {
                                        InfoCard(
                                            "No additions",
                                            "Coffee and brew water are recorded in Recipe. Ingredient additions are optional."
                                        )
                                    }
                                }
                                items(state.draft.lines.withIndex().toList(), key = { it.index }) { indexed ->
                                    val line = indexed.value
                                    InfoCard(
                                        state.ingredients.find { it.id == line.ingredientId }?.name ?: "Unknown ingredient",
                                        "${line.quantity} ${line.unit}; food-grade and supplier limits checked on save"
                                    ) {
                                        TextButton(
                                            onClick = { model.removeLine(indexed.index) },
                                            modifier = Modifier.heightIn(min = 48.dp)
                                        ) {
                                            Text("Remove line")
                                        }
                                    }
                                }
                                if (state.draft.lines.isNotEmpty()) {
                                    item {
                                        val allergens = state.draft.lines.flatMap { line ->
                                            state.ingredients.find { it.id == line.ingredientId }?.allergens.orEmpty()
                                        }.distinct().joinToString().ifBlank { "none declared; verify supplier" }
                                        FilterChip(
                                            selected = fields["allergens"] == "yes",
                                            onClick = { model.updateField("allergens", if (fields["allergens"] == "yes") "" else "yes") },
                                            label = { Text("Acknowledge listed ingredient allergens: $allergens") },
                                            modifier = Modifier.heightIn(min = 48.dp)
                                        )
                                    }
                                }
                            }
                            BrewSection.Tools -> {
                                item {
                                    BrewCard(
                                        "Coffee science tools",
                                        "Calculated estimates from explicit inputs. No measured values are filled in."
                                    ) {
                                        Select(
                                            "Calculation",
                                            science,
                                            listOf("Dilution", "Concentration", "Volume to mass").map { it to it },
                                            {
                                                science = it
                                                scienceInputs = emptyMap()
                                            },
                                            "science:kind"
                                        )
                                        val scienceFields = when (science) {
                                            "Dilution" -> listOf(
                                                "initialMassGrams" to "Initial beverage mass (g)",
                                                "initialTdsPercent" to "Measured initial TDS (% w/w)",
                                                "addedWaterGrams" to "Added water (g)"
                                            )
                                            "Concentration" -> listOf(
                                                "soluteMassGrams" to "Solute mass (g)",
                                                "solutionMassGrams" to "Total solution mass, including solute (g)"
                                            )
                                            else -> listOf("volumeMl" to "Volume (mL)", "densityGramsPerMl" to "Known density (g/mL)")
                                        }
                                        scienceFields.forEach { (key, label) ->
                                            Field(label, scienceInputs[key].orEmpty(), {
                                                scienceInputs = scienceInputs + (key to it)
                                            }, "science:$key", true)
                                        }
                                        Button(onClick = {
                                            val tool = when (science) {
                                                "Dilution" -> "calculate_dilution"
                                                "Concentration" -> "calculate_concentration_ppm"
                                                else -> "convert_units"
                                            }
                                            model.runManualCalculation(
                                                tool,
                                                scienceFields.associate { it.first to scienceInputs[it.first].orEmpty() }
                                            )
                                        }, enabled = !state.busy, modifier = Modifier.heightIn(min = 48.dp)) {
                                            Text("Calculate ${science.lowercase()}")
                                        }
                                    }
                                }
                                state.manualToolResult?.let { result -> item { InfoCard("Exact calculation", result) } }
                            }
                            BrewSection.Steps -> {
                                item {
                                    BrewCard("Ordered pour steps", "Action, timing, pour water, temperature and valve state.") {
                                        Button(
                                            onClick = model::addDraftStep,
                                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("steps:add")
                                        ) {
                                            Text("+ Add pour step")
                                        }
                                    }
                                }
                                if (state.draft.steps.isEmpty()) {
                                    item {
                                        InfoCard(
                                            "No custom steps defined",
                                            "Tap '+ Add pour step' to build a structured multi-pour routine or open a saved recipe."
                                        )
                                    }
                                }
                                items(state.draft.steps.withIndex().toList(), key = { it.value.stepNumber }) { (index, step) ->
                                    InfoCard(
                                        "Step ${step.stepNumber}: ${step.action} (+${step.targetWaterGrams} g)",
                                        "Cumulative: ${step.cumulativeMassGrams} g · ${step.durationSeconds} s" +
                                            (if (step.temperatureCelsius != null) " · ${step.temperatureCelsius} °C" else "") +
                                            (if (!step.valveStatus.isNullOrBlank()) " · Valve: ${step.valveStatus}" else "")
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Select(
                                                "Action",
                                                step.action,
                                                listOf("Bloom", "Pour", "Wait", "Stir", "Press", "Drawdown", "Valve").map { it to it },
                                                { model.updateDraftStep(index, step.copy(action = it)) },
                                                "step:action:$index"
                                            )
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                OutlinedTextField(
                                                    value = step.targetWaterGrams.toString(),
                                                    onValueChange = {
                                                        val w = it.toDoubleOrNull()
                                                        if (w != null && w >= 0) {
                                                            model.updateDraftStep(
                                                                index,
                                                                step.copy(targetWaterGrams = w)
                                                            )
                                                        }
                                                    },
                                                    label = { Text("Water (g)") },
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                                    modifier = Modifier.weight(1f).testTag("step:water:$index")
                                                )
                                                OutlinedTextField(
                                                    value = step.durationSeconds.toString(),
                                                    onValueChange = {
                                                        val s = it.toIntOrNull()
                                                        if (s != null && s > 0) model.updateDraftStep(index, step.copy(durationSeconds = s))
                                                    },
                                                    label = { Text("Sec") },
                                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                    modifier = Modifier.weight(1f).testTag("step:duration:$index")
                                                )
                                            }
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Select(
                                                    "Valve",
                                                    step.valveStatus.orEmpty(),
                                                    listOf("" to "N/A", "Open" to "Open", "Closed" to "Closed", "Half" to "Half"),
                                                    { model.updateDraftStep(index, step.copy(valveStatus = it.ifBlank { null })) },
                                                    "step:valve:$index"
                                                )
                                            }
                                            OutlinedTextField(
                                                value = step.description,
                                                onValueChange = { model.updateDraftStep(index, step.copy(description = it)) },
                                                label = { Text("Description") },
                                                modifier = Modifier.fillMaxWidth().testTag("step:desc:$index")
                                            )
                                            TextButton(
                                                onClick = { model.removeDraftStep(index) },
                                                modifier = Modifier.testTag("step:remove:$index")
                                            ) {
                                                Text("Remove step")
                                            }
                                        }
                                    }
                                }
                            }
                            BrewSection.Timer -> {
                                item {
                                    val session = state.brewSession
                                    val elapsedSec = state.timerSeconds
                                    val elapsedFormatted = "%02d:%02d".format(elapsedSec / 60, elapsedSec % 60)
                                    val currentProgress = session?.stepProgress

                                    BrewCard(
                                        "Active brew chronometer & scale",
                                        "Real-time countdown, step-by-step guidance, BLE scale streaming, and live flow rate."
                                    ) {
                                        // Unified Precision Laboratory Console
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("timer:display"),
                                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                            shape = RoundedCornerShape(16.dp),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f))
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                verticalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                // Header Bar: Live Pulse + State + Scale Connection Status
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        LivePulseIndicator(isActive = state.timerRunning)
                                                        Text(
                                                            text = if (state.timerRunning) {
                                                                "ACTIVE BREWING"
                                                            } else if (session?.status == BrewExecutionStatus.PAUSED) {
                                                                "PAUSED"
                                                            } else if (session?.status == BrewExecutionStatus.COMPLETED) {
                                                                "COMPLETED"
                                                            } else {
                                                                "READY / ARMED"
                                                            },
                                                            style = MaterialTheme.typography.labelMedium,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (state.timerRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = if (session?.scaleConnectionState == ScaleConnectionState.READY) {
                                                            MaterialTheme.colorScheme.primaryContainer
                                                        } else {
                                                            MaterialTheme.colorScheme.surfaceContainerHighest
                                                        }
                                                    ) {
                                                        Text(
                                                            text = session?.scaleDevice?.name ?: (session?.scaleConnectionState?.name ?: "DISCONNECTED"),
                                                            style = MaterialTheme.typography.labelSmall,
                                                            fontWeight = FontWeight.Medium,
                                                            color = if (session?.scaleConnectionState == ScaleConnectionState.READY) {
                                                                MaterialTheme.colorScheme.onPrimaryContainer
                                                            } else {
                                                                MaterialTheme.colorScheme.outline
                                                            },
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                        )
                                                    }
                                                }

                                                // Primary Telemetry Cluster: Elapsed Time & Live Mass/Flow
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = elapsedFormatted,
                                                        style = MaterialTheme.typography.displayMedium.copy(
                                                            fontFamily = FontFamily.Monospace,
                                                            fontWeight = FontWeight.Bold,
                                                            fontFeatureSettings = "tnum"
                                                        ),
                                                        maxLines = 1,
                                                        softWrap = false,
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        modifier = Modifier.semantics {
                                                            liveRegion = LiveRegionMode.Polite
                                                        }
                                                    )
                                                    Column(horizontalAlignment = Alignment.End) {
                                                        Text(
                                                            "CURRENT MASS",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = MaterialTheme.colorScheme.outline
                                                        )
                                                        Text(
                                                            text = "${"%.1f".format(
                                                                java.util.Locale.US,
                                                                session?.currentWeightGrams ?: 0.0
                                                            )} g",
                                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                                fontFamily = FontFamily.Monospace,
                                                                fontWeight = FontWeight.Bold,
                                                                fontFeatureSettings = "tnum"
                                                            ),
                                                            maxLines = 1,
                                                            softWrap = false
                                                        )
                                                        Text(
                                                            text = "${"%.2f".format(
                                                                java.util.Locale.US,
                                                                session?.currentFlowRateGps ?: 0.0
                                                            )} g/s",
                                                            style = MaterialTheme.typography.titleMedium.copy(
                                                                fontFamily = FontFamily.Monospace,
                                                                fontWeight = FontWeight.SemiBold,
                                                                fontFeatureSettings = "tnum"
                                                            ),
                                                            maxLines = 1,
                                                            softWrap = false,
                                                            color = MaterialTheme.colorScheme.secondary
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        // Step Progression Card
                                        if (currentProgress != null) {
                                            Surface(
                                                modifier = Modifier.fillMaxWidth().testTag("timer:step_card"),
                                                color = MaterialTheme.colorScheme.surface,
                                                shape = RoundedCornerShape(10.dp),
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.40f))
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(12.dp),
                                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(
                                                            text = "Step ${currentProgress.stepIndex + 1} of ${currentProgress.totalSteps}: ${currentProgress.currentStep.action}",
                                                            style = MaterialTheme.typography.titleMedium,
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                        Text(
                                                            text = "${currentProgress.stepRemainingSeconds}s remaining",
                                                            style = MaterialTheme.typography.titleMedium,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                    }
                                                    if (currentProgress.currentStep.description.isNotBlank()) {
                                                        Text(
                                                            text = currentProgress.currentStep.description,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text("Step Target: +${currentProgress.stepTargetWaterGrams}g")
                                                        Text("Cumulative: ${currentProgress.cumulativeTargetWaterGrams}g")
                                                    }
                                                    if (currentProgress.nextStep != null) {
                                                        Text(
                                                            text = "Next: ${currentProgress.nextStep.action} (+${currentProgress.nextStep.targetWaterGrams}g in ${currentProgress.nextStep.durationSeconds}s)",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.outline
                                                        )
                                                    }
                                                }
                                            }

                                            val prox = currentProgress.proximityState
                                            val (proxText, proxColor) = when (prox) {
                                                ProximityState.NORMAL -> "Pouring: ${"%.1f".format(java.util.Locale.US, currentProgress.remainingToCumulativeTargetGrams)}g remaining" to MaterialTheme.colorScheme.onSurface
                                                ProximityState.APPROACHING_10G -> "Within 10g (${"%.1f".format(java.util.Locale.US, currentProgress.remainingToCumulativeTargetGrams)}g left)" to MaterialTheme.colorScheme.tertiary
                                                ProximityState.IMMINENT_2G -> "Within 2g! Prepare cutoff" to MaterialTheme.colorScheme.error
                                                ProximityState.TARGET_MATCHED -> "Target Matched (${"%.1f".format(java.util.Locale.US, currentProgress.cumulativeTargetWaterGrams)}g)" to MaterialTheme.colorScheme.primary
                                                ProximityState.OVERSHOOT -> "Target Overshoot (+${"%.1f".format(java.util.Locale.US, (session?.currentWeightGrams ?: 0.0) - currentProgress.cumulativeTargetWaterGrams)}g)" to MaterialTheme.colorScheme.error
                                            }
                                            Surface(
                                                modifier = Modifier.fillMaxWidth().testTag("timer:proximity_badge"),
                                                color = proxColor.copy(alpha = 0.12f),
                                                shape = RoundedCornerShape(6.dp),
                                                border = BorderStroke(1.dp, proxColor)
                                            ) {
                                                Text(
                                                    text = proxText,
                                                    modifier = Modifier.padding(8.dp),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = proxColor
                                                )
                                            }
                                        }

                                        // BLE Scale Card
                                        Surface(
                                            modifier = Modifier.fillMaxWidth().testTag("timer:scale_card"),
                                            color = MaterialTheme.colorScheme.surface,
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    if (session?.scaleDevice == null) {
                                                        FilledTonalButton(
                                                            onClick = model::connectMockScale,
                                                            modifier = Modifier.weight(
                                                                1f
                                                            ).heightIn(min = 44.dp).testTag("timer:connect_scale")
                                                        ) {
                                                            Text("Connect Scale")
                                                        }
                                                    } else {
                                                        OutlinedButton(
                                                            onClick = model::disconnectScale,
                                                            modifier = Modifier.weight(
                                                                1f
                                                            ).heightIn(min = 44.dp).testTag("timer:disconnect_scale")
                                                        ) {
                                                            Text("Disconnect")
                                                        }
                                                    }
                                                    Button(
                                                        onClick = model::tareScale,
                                                        modifier = Modifier.weight(1f).heightIn(min = 44.dp).testTag("timer:tare_scale")
                                                    ) {
                                                        Text("Tare (0.0g)")
                                                    }
                                                }

                                                if (session?.scaleDevice != null) {
                                                    Text("Simulate Pouring Water:", style = MaterialTheme.typography.labelSmall)
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        listOf(10.0, 25.0, 50.0, 100.0).forEach { delta ->
                                                            OutlinedButton(
                                                                onClick = {
                                                                    model.feedSimulatedWeight(
                                                                        (session.currentWeightGrams + delta)
                                                                    )
                                                                },
                                                                modifier = Modifier.weight(
                                                                    1f
                                                                ).heightIn(min = 40.dp).testTag("timer:sim_pour_${delta.toInt()}")
                                                            ) {
                                                                Text("+${delta.toInt()}g")
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // Ergonomic Primary CTA (Start / Pause / Resume)
                                        TactileButton(
                                            onClick = {
                                                if (state.timerRunning) model.pauseBrewSession() else model.startBrewSession()
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(54.dp)
                                                .testTag("timer:toggle"),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (state.timerRunning) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                                            ),
                                            shape = RoundedCornerShape(14.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (state.timerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                            Text(
                                                text = if (state.timerRunning) "Pause Session" else if (session?.status == BrewExecutionStatus.PAUSED) "Resume Session" else "Start Session",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        // Secondary Segmented Control Row
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = model::skipBrewStep,
                                                enabled = session != null && session.status != BrewExecutionStatus.COMPLETED,
                                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                                                modifier = Modifier.weight(1f).heightIn(min = 44.dp).testTag("timer:skip_step")
                                            ) {
                                                Text("Skip", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                                            }
                                            OutlinedButton(
                                                onClick = model::restartBrewStep,
                                                enabled = session != null,
                                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                                                modifier = Modifier.weight(1.1f).heightIn(min = 44.dp).testTag("timer:restart_step")
                                            ) {
                                                Text("Restart", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                                            }
                                            OutlinedButton(
                                                onClick = model::resetBrewSession,
                                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                                                modifier = Modifier.weight(1f).heightIn(min = 44.dp).testTag("timer:reset")
                                            ) {
                                                Text("Reset", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                                            }
                                            FilledTonalButton(
                                                onClick = model::finishBrewSession,
                                                enabled = session != null && (state.timerSeconds > 0 || session.currentWeightGrams > 0.0),
                                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp),
                                                modifier = Modifier.weight(1.4f).heightIn(min = 44.dp).testTag("timer:finish_and_log")
                                            ) {
                                                Text("Finish & Log", style = MaterialTheme.typography.labelSmall, maxLines = 1)
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            FilterChip(
                                                selected = session?.keepScreenOn ?: true,
                                                onClick = model::toggleKeepScreenOn,
                                                label = { Text("Screen Awake") },
                                                modifier = Modifier.testTag("timer:toggle_awake")
                                            )
                                            FilterChip(
                                                selected = session?.audioEnabled ?: true,
                                                onClick = model::toggleSound,
                                                label = { Text("Audio") },
                                                modifier = Modifier.testTag("timer:toggle_sound")
                                            )
                                            FilterChip(
                                                selected = session?.hapticsEnabled ?: true,
                                                onClick = model::toggleHaptics,
                                                label = { Text("Haptics") },
                                                modifier = Modifier.testTag("timer:toggle_haptics")
                                            )
                                        }

                                        if (state.brewCurveRmse != null) {
                                            Text(
                                                text = "Extraction Curve RMSE: ${"%.2f".format(
                                                    java.util.Locale.US,
                                                    state.brewCurveRmse
                                                )}g deviation from planned profile",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                    }
                                }
                            }
                            BrewSection.Expert -> {
                                item {
                                    BrewCard(
                                        "Dial-in parameters & grinder setup",
                                        "Capture grinder model, click/RPM settings, first drop time and water mineral composition."
                                    ) {
                                        Field(
                                            "Grinder model",
                                            fields.getValue("grinderModel"),
                                            { model.updateField("grinderModel", it) },
                                            "form:grinder_model"
                                        )
                                        Field(
                                            "Grinder setting / clicks",
                                            fields.getValue("grinderSetting"),
                                            { model.updateField("grinderSetting", it) },
                                            "form:grinder_setting"
                                        )
                                        Field(
                                            "First drop time (seconds)",
                                            fields.getValue("firstDrop"),
                                            { model.updateField("firstDrop", it) },
                                            "form:first_drop",
                                            true
                                        )
                                        Field("Water general hardness (ppm GH)", fields.getValue("waterHardness"), {
                                            model.updateField("waterHardness", it)
                                        }, "form:water_hardness", true)
                                        Field("Water total alkalinity (ppm KH)", fields.getValue("waterAlkalinity"), {
                                            model.updateField("waterAlkalinity", it)
                                        }, "form:water_alkalinity", true)
                                        Field(
                                            "Basket & puck prep notes",
                                            fields.getValue("puckPrep"),
                                            { model.updateField("puckPrep", it) },
                                            "form:puck_prep"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Surface(
                tonalElevation = 4.dp,
                shadowElevation = 3.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val calculationSummary = state.calculation.lineSequence().firstOrNull().orEmpty()
                    val ratioLabel = if (calculationSummary.startsWith("Calculated ratio ")) {
                        "Ratio ${calculationSummary.removePrefix("Calculated ratio ")}"
                    } else {
                        "Review calculation"
                    }
                    TextButton(
                        onClick = { selectedSection = BrewSection.Measurements.name },
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("form:ratio")
                    ) {
                        AnimatedContent(
                            targetState = ratioLabel,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(150)) togetherWith fadeOut(animationSpec = tween(100))
                            },
                            label = "ratioAnimation"
                        ) { targetText ->
                            Text(targetText, maxLines = 2, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Button(
                        onClick = model::saveRecipe,
                        enabled = state.ready && !state.busy,
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("form:save")
                    ) {
                        AnimatedContent(
                            targetState = (state.draft.id == null),
                            transitionSpec = {
                                fadeIn(animationSpec = tween(150)) togetherWith fadeOut(animationSpec = tween(100))
                            },
                            label = "saveLabelAnimation"
                        ) { isNew ->
                            Text(if (isNew) "Save recipe" else "Save new revision", maxLines = 2)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BrewCard(title: String, detail: String, content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    InfoCard(title, detail) { Column(verticalArrangement = Arrangement.spacedBy(12.dp), content = content) }
}

@Composable
private fun Library(state: CoffeeUiState, model: CoffeeViewModel) {
    val context = LocalContext.current
    var query by rememberSaveable { mutableStateOf("") }
    var tab by rememberSaveable { mutableStateOf("Beans & Bags") }
    var formKind by rememberSaveable { mutableStateOf<String?>(null) }
    var editingId by rememberSaveable { mutableStateOf<String?>(null) }
    var form by rememberSaveable { mutableStateOf(emptyMap<String, String>()) }
    var processFilter by rememberSaveable { mutableStateOf("All") }
    var favoritesOnly by rememberSaveable { mutableStateOf(false) }
    var gearFilter by rememberSaveable { mutableStateOf("All") }
    var ocrDialogVisible by rememberSaveable { mutableStateOf(false) }
    var ocrSampleText by rememberSaveable { mutableStateOf("") }
    var importJsonText by rememberSaveable { mutableStateOf("") }
    var selectedStrategy by rememberSaveable { mutableStateOf(ConflictResolutionStrategy.KEEP_BOTH_RENAME) }
    var addingBagBeanId by rememberSaveable { mutableStateOf<String?>(null) }
    var bagPackageMassText by rememberSaveable { mutableStateOf("") }
    var bagRemainingMassText by rememberSaveable { mutableStateOf("") }
    var bagPriceText by rememberSaveable { mutableStateOf("") }
    var deductingBagId by rememberSaveable { mutableStateOf<String?>(null) }
    var doseDeductionText by rememberSaveable { mutableStateOf("") }

    val libraryTabs = listOf("Beans & Bags", "Grinders & Gear", "Water Chemistry", "Ingredients", "Green & Roast QC", "Import / Export")

    Page("Library") {
        item {
            Heading(
                "Beans, Gear & Formulation Catalog",
                "Verified agricultural origins, bag inventory with rest aging heuristics, precision grinder geometry, water mineral chemistry and conflict-resolving import/export."
            )
        }
        item {
            Field("Search catalog (origin, roaster, gear, varietal)", query, { query = it }, "library:query")
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                libraryTabs.forEach { label ->
                    val isSelected = tab == label || (tab == "Coffee" && label == "Beans & Bags")
                    val icon = when (label) {
                        "Beans & Bags" -> Icons.Default.Coffee
                        "Grinders & Gear" -> Icons.Default.Settings
                        "Water Chemistry" -> Icons.Default.WaterDrop
                        "Ingredients" -> Icons.Default.Science
                        "Green & Roast QC" -> Icons.Default.Spa
                        "Import / Export" -> Icons.Default.ImportExport
                        else -> Icons.Default.Coffee
                    }
                    TactileFilterChip(
                        selected = isSelected,
                        onClick = { tab = label },
                        leadingIcon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.heightIn(min = 48.dp)
                    )
                }
            }
        }

        // ====================================================================
        // TAB 1: Beans & Bags Inventory (S2-001, S2-002, S2-003, S2-008, S2-010)
        // ====================================================================
        if (tab == "Beans & Bags" || tab == "Coffee") {
            item {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            formKind = "Coffee"
                            editingId = null
                            form = emptyMap()
                        },
                        modifier = Modifier.testTag("library:add")
                    ) {
                        Text("+ Add Bean Record")
                    }
                    OutlinedButton(
                        onClick = {
                            ocrDialogVisible = true
                        }
                    ) {
                        Text("Review bag label text")
                    }
                    FilterChip(
                        selected = favoritesOnly,
                        onClick = { favoritesOnly = !favoritesOnly },
                        label = { Text(if (favoritesOnly) "★ Favorites Only" else "☆ All Beans") }
                    )
                }
            }
            item {
                val processes = listOf("All", "WASHED", "NATURAL", "HONEY", "ANAEROBIC_NATURAL", "WET_HULLED")
                Select("Process filter", processFilter, processes.map { it to it }, { processFilter = it }, "library:process")
            }

            // Detailed Beans
            val filteredDetailed = state.detailedBeans.filter { bean ->
                (processFilter == "All" || bean.process.name.equals(processFilter, ignoreCase = true)) &&
                    (!favoritesOnly || bean.isFavorite) &&
                    listOf(bean.name, bean.roaster, bean.originCountry, bean.varietal, bean.process.name).any { it.contains(query, true) }
            }

            items(filteredDetailed, key = { "detailed-${it.id}" }) { bean ->
                val analysis = bean.roastDate?.let { roastDate ->
                    RoastRestCalculator.calculateFreshness(
                        roastDate = roastDate,
                        roastLevel = bean.roastLevel,
                        process = bean.process,
                        freezeHistory = bean.bags.flatMap { it.freezeHistory }
                    )
                }

                InfoCard(
                    "${bean.roaster} — ${bean.name}",
                    listOf(
                        "Origin: ${bean.originCountry} · ${bean.region} (Farm: ${bean.farm}, Producer: ${bean.producer})",
                        "Variety: ${bean.varietal} · Process: ${bean.process.name} · Altitude: ${bean.altitudeMeters ?: "Unknown"}m",
                        analysis?.let { "Roast: ${bean.roastLevel.name} on ${bean.roastDate} (${it.effectiveRestDays} effective rest days)" }
                            ?: "Roast date: Unknown · freshness is not estimated",
                        analysis?.let { "Freshness: ${it.state.name} (Peak: Days ${it.peakStartDay}–${it.peakEndDay})" }.orEmpty(),
                        analysis?.let { "Degas Rationale: ${it.heuristicRationale}" }.orEmpty(),
                        if (bean.tastingNotes.isNotEmpty()) "Tasting Notes: ${bean.tastingNotes.joinToString(", ")}" else ""
                    ).filter { it.isNotBlank() }.joinToString("\n")
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            TextButton(onClick = { model.toggleBeanFavorite(bean.id) }) {
                                Text(if (bean.isFavorite) "★ Favorited" else "☆ Add Favorite")
                            }
                            TextButton(onClick = {
                                addingBagBeanId = bean.id
                                bagPackageMassText = ""
                                bagRemainingMassText = ""
                                bagPriceText = ""
                            }) {
                                Text("+ Add bag")
                            }
                            TextButton(onClick = { model.deleteDetailedBean(bean.id) }) {
                                Text("Delete", color = MaterialTheme.colorScheme.error)
                            }
                        }

                        // Bags Inventory List
                        if (bean.bags.isNotEmpty()) {
                            Text("Bags in Inventory (${bean.bags.size}):", style = MaterialTheme.typography.labelMedium)
                            bean.bags.forEach { bag ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                            Text(
                                                "${bag.remainingMassGrams}g / ${bag.packageMassGrams}g",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontFeatureSettings = "tnum",
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            )
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                modifier = Modifier.padding(top = 2.dp)
                                            ) {
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = if (bag.status == BagStatus.ACTIVE) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
                                                ) {
                                                    Text(
                                                        bag.status.name,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = if (bag.status == BagStatus.ACTIVE) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                                        maxLines = 1,
                                                        softWrap = false
                                                    )
                                                }
                                                if (bag.freezeHistory.any { it.isOngoing }) {
                                                    Text(
                                                        "❄ Frozen",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.tertiary,
                                                        maxLines = 1,
                                                        softWrap = false
                                                    )
                                                }
                                            }
                                        }
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            TactileOutlinedButton(
                                                onClick = { model.toggleFreezeBag(bag.id) },
                                                enabled = bag.status != BagStatus.FINISHED,
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                modifier = Modifier.heightIn(min = 48.dp)
                                            ) {
                                                Text(
                                                    if (bag.status == BagStatus.FROZEN) "Thaw" else "Freeze",
                                                    maxLines = 1,
                                                    softWrap = false,
                                                    style = MaterialTheme.typography.labelMedium
                                                )
                                            }
                                            TactileButton(
                                                onClick = {
                                                    deductingBagId = bag.id
                                                    doseDeductionText = ""
                                                },
                                                enabled = bag.status != BagStatus.FROZEN && bag.status != BagStatus.FINISHED && bag.remainingMassGrams > BigDecimal.ZERO,
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                modifier = Modifier.heightIn(min = 48.dp)
                                            ) {
                                                Text(
                                                    "Record dose",
                                                    maxLines = 1,
                                                    softWrap = false,
                                                    style = MaterialTheme.typography.labelMedium.copy(fontFeatureSettings = "tnum")
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Legacy / Curated Seed Beans
            items(
                state.beans.filter { bean ->
                    (processFilter == "All" || bean.process.equals(processFilter, ignoreCase = true)) &&
                        listOf(bean.name, bean.variety, bean.country, bean.region).any { it.contains(query, true) }
                },
                key = { "legacy-${it.id}" }
            ) { bean ->
                InfoCard(
                    bean.name,
                    if (bean.isCuratedSeed) {
                        "Reference variety · ${bean.variety}\n" +
                            "Supplier lot, origin, process, roast and sensory measurements are not supplied.\n" +
                            "Source: ${bean.provenance}"
                    } else {
                        listOf(
                            "${bean.country} · ${bean.region}",
                            "Variety ${bean.variety} · ${bean.process} · roast ${bean.roastLevel}",
                            "Producer ${bean.producer} · altitude ${bean.altitudeMeters ?: "Unknown"} m",
                            "Source: ${bean.provenance}"
                        ).joinToString("\n")
                    }
                ) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = {
                            formKind = "Coffee"
                            editingId = bean.id
                            form = beanForm(bean)
                        }) { Text("Edit coffee") }
                        if (!bean.isCuratedSeed) TextButton(onClick = { model.requestDeletion("bean", bean.id) }) { Text("Delete coffee") }
                    }
                }
            }
        }

        // ====================================================================
        // TAB 2: Grinders & Gear Catalog (S2-004, S2-005)
        // ====================================================================
        if (tab == "Grinders & Gear") {
            item {
                val gearCategories =
                    listOf("All", "GRINDER", "DRIPPER", "KETTLE", "SCALE", "ESPRESSO_MACHINE", "PORTAFILTER_BASKET", "PUCK_PREP_TOOL")
                Select("Category filter", gearFilter, gearCategories.map { it to it }, { gearFilter = it }, "library:gear_filter")
            }

            if (gearFilter == "All" || gearFilter == "GRINDER") {
                item {
                    Text("Precision Grinders & Burr Geometry (S2-004)", style = MaterialTheme.typography.titleMedium)
                }
                items(
                    state.grinders.filter { listOf(it.brand, it.model).any { s -> s.contains(query, true) } },
                    key = { "grinder-${it.id}" }
                ) { grinder ->
                    InfoCard(
                        "${grinder.brand} — ${grinder.model}",
                        listOf(
                            "Calibration Unit: ${grinder.calibrationUnit.name}",
                            "Burrs: ${grinder.burrProfile.diameterMm}mm ${grinder.burrProfile.burrType.name} (${grinder.burrProfile.material.name})",
                            "Burr Model: ${grinder.burrProfile.burrModelName}",
                            "Zero Point Offset: ${grinder.zeroPointOffset} ${grinder.calibrationUnit.name.lowercase()}",
                            if (grinder.clicksPerRotation != null) "Clicks/Rotation: ${grinder.clicksPerRotation}" else "",
                            if (grinder.typicalRpm != null) "Motor: ${grinder.typicalRpm} RPM" else "",
                            if (grinder.notes.isNotBlank()) "Notes: ${grinder.notes}" else ""
                        ).filter { it.isNotBlank() }.joinToString("\n")
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = { model.toggleGrinderFavorite(grinder.id) }) {
                                Text(if (grinder.isFavorite) "★ Favorited" else "☆ Add Favorite")
                            }
                            TextButton(onClick = { model.deleteGrinder(grinder.id) }) {
                                Text("Delete", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }

            if (gearFilter != "GRINDER") {
                item {
                    Text("Brewing Gear & Equipment Catalog (S2-005)", style = MaterialTheme.typography.titleMedium)
                }
                val filteredEquip = state.equipmentCatalog.filter { eq ->
                    (gearFilter == "All" || eq.category.name == gearFilter) &&
                        listOf(eq.brand, eq.model, eq.category.name).any { it.contains(query, true) }
                }
                items(filteredEquip, key = { "equip-${it.id}" }) { item ->
                    InfoCard(
                        "[${item.category.name}] ${item.brand} ${item.model}",
                        item.parameters.entries.joinToString("\n") { "${it.key}: ${it.value}" }.ifBlank { "No special parameters" }
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = { model.toggleEquipmentFavorite(item.id) }) {
                                Text(if (item.isFavorite) "★ Favorited" else "☆ Add Favorite")
                            }
                            TextButton(onClick = { model.deleteEquipmentItem(item.id) }) {
                                Text("Delete", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }

        // ====================================================================
        // TAB 3: Water Mineral Profile & Chemistry (S2-006)
        // ====================================================================
        if (tab == "Water Chemistry") {
            item {
                Text("Water Chemistry & Mineral Presets (S2-006)", style = MaterialTheme.typography.titleMedium)
            }
            items(state.waterProfiles, key = { "water-${it.id}" }) { water ->
                InfoCard(
                    water.name,
                    listOf(
                        "Calcium (Ca2+): ${water.calciumPpm} ppm · Magnesium (Mg2+): ${water.magnesiumPpm} ppm",
                        "Sodium (Na+): ${water.sodiumPpm} ppm · Bicarbonate (HCO3-): ${water.bicarbonatePpm} ppm",
                        "Total Hardness (GH): ${water.totalHardnessPpmCaCO3} ppm CaCO3",
                        "Total Alkalinity (KH): ${water.totalAlkalinityPpmCaCO3} ppm CaCO3",
                        "Calculated TDS: ${water.calculatedTdsPpm} ppm · pH: ${water.ph ?: "7.0"}",
                        "Preset Type: ${water.preset.name}"
                    ).joinToString("\n")
                ) {
                    TextButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, water.name)
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Water Profile: ${water.name}\nHardness: ${water.totalHardnessPpmCaCO3} ppm CaCO3\nAlkalinity: ${water.totalAlkalinityPpmCaCO3} ppm CaCO3\nTDS: ${water.calculatedTdsPpm} ppm"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Water Profile"))
                    }) {
                        Text("Share Profile")
                    }
                }
            }
        }

        // ====================================================================
        // TAB 4: Ingredients & Food-Grade Catalog (S2-007)
        // ====================================================================
        if (tab == "Ingredients") {
            item {
                Text("Food-Grade Verified Solutions & Additives (S2-007)", style = MaterialTheme.typography.titleMedium)
            }
            items(state.foodGradeIngredients, key = { "food-ing-${it.id}" }) { ing ->
                InfoCard(
                    ing.name,
                    listOf(
                        "Density: ${ing.densityGramsPerMl} g/mL (Required for volume-mass conversion)",
                        "Food Grade Verified: ${if (ing.isFoodGradeVerified) "Yes (Food-Safe)" else "NO - FORBIDDEN"}",
                        "Supplier Max Limit: ${ing.supplierMaxPercent ?: "None"}% w/w",
                        "Allergens: ${ing.allergens.joinToString().ifBlank { "None declared" }}",
                        if (ing.casNumber != null) "CAS Registry: ${ing.casNumber}" else "",
                        "Provenance: ${ing.provenance}"
                    ).filter { it.isNotBlank() }.joinToString("\n")
                )
            }
            items(state.ingredients, key = { "legacy-ing-${it.id}" }) { ingredient ->
                InfoCard(
                    ingredient.name,
                    listOf(
                        "Food grade: ${ingredient.foodGrade?.toString() ?: "Unknown"}",
                        "Density: ${ingredient.densityGramsPerMl ?: "Unknown"} g/mL",
                        "Supplier maximum: ${ingredient.supplierMaxPercent ?: "Unknown"}% w/w",
                        "Allergens: ${ingredient.allergens.joinToString().ifBlank { "not declared; verify supplier" }}",
                        "Source: ${ingredient.provenance}"
                    ).joinToString("\n")
                )
            }
        }

        // ====================================================================
        // TAB 5: Green Coffee QC & Roast Batches (S2-011, S2-012)
        // ====================================================================
        if (tab == "Green & Roast QC") {
            item {
                Text("Green Coffee Physical QC (S2-011)", style = MaterialTheme.typography.titleMedium)
            }
            items(state.greenCoffeeLots, key = { "green-${it.id}" }) { lot ->
                InfoCard(
                    "Lot: ${lot.lotNumber} (${lot.originCountry} - ${lot.producer})",
                    listOf(
                        "Moisture: ${lot.moisturePercent}% (Target: 10.0-12.0%) · Water Activity: ${lot.waterActivityAw} aw",
                        "Bulk Density: ${lot.bulkDensityGramsPerLiter} g/L",
                        "Defects: Cat 1 = ${lot.category1DefectCount}, Cat 2 = ${lot.category2DefectCount} (Total Points = ${lot.totalDefectPoints})",
                        "Grade Status: ${if (lot.isSpecialtyGradeEligible) "★ Specialty Grade 1 Eligible" else "Commercial / Off-Grade"}",
                        "Status: ${lot.sampleStatus.name} (Harvest: ${lot.harvestYear})"
                    ).joinToString("\n")
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
                Text("Roast Batches, Curves & Blends (S2-012)", style = MaterialTheme.typography.titleMedium)
            }
            items(state.roastBatches, key = { "roast-${it.id}" }) { batch ->
                InfoCard(
                    "Batch #${batch.batchNumber}",
                    listOf(
                        "Charge Temp: ${batch.chargeTemperatureCelsius}°C · Drop Temp: ${batch.dropTemperatureCelsius}°C",
                        "Duration: ${batch.totalRoastDurationSeconds}s · First Crack: ${batch.firstCrackTimeSeconds}s",
                        "Development Time Ratio (DTR): ${batch.developmentTimeRatioPercent}%",
                        "Curve Data Points: ${batch.curvePoints.size} samples (Rate of Rise RoR available)",
                        "Blend Formulations: ${batch.blendComponents.joinToString { "${it.beanId}: ${it.componentPercentage}%" }}"
                    ).joinToString("\n")
                )
            }
        }

        // ====================================================================
        // TAB 6: Bulk Import/Export & Conflicts (S2-009)
        // ====================================================================
        if (tab == "Import / Export") {
            item {
                Heading(
                    "Catalog Data Portability & Conflict Resolution (S2-009)",
                    "Export canonical JSON/CSV or import backup data with deterministic duplicate resolution strategies."
                )
            }
            item {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        val json = model.exportCatalogJson()
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/json"
                            putExtra(Intent.EXTRA_SUBJECT, "coffee_creator_catalog.json")
                            putExtra(Intent.EXTRA_TEXT, json)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Export Catalog JSON"))
                    }) {
                        Text("Export Catalog (JSON)")
                    }
                    OutlinedButton(onClick = {
                        val csv = model.exportBeansCsv()
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/csv"
                            putExtra(Intent.EXTRA_SUBJECT, "beans_export.csv")
                            putExtra(Intent.EXTRA_TEXT, csv)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Export Beans (CSV)"))
                    }) {
                        Text("Export Beans (CSV)")
                    }
                }
            }
            item {
                Spacer(Modifier.height(12.dp))
                Text("Import Catalog JSON Payload:", style = MaterialTheme.typography.titleMedium)
            }
            item {
                OutlinedTextField(
                    value = importJsonText,
                    onValueChange = {
                        importJsonText = it
                        if (it.isNotBlank()) model.previewCatalogImport(it)
                    },
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    label = { Text("Paste Catalog JSON here") }
                )
            }

            state.catalogImportPreview?.let { preview ->
                item {
                    InfoCard(
                        "Import Dry-Run Preview",
                        "Total records: ${preview.totalRecords}\n" +
                            "New records: ${preview.newRecordsCount}\n" +
                            "Conflicting records: ${preview.conflictingRecordsCount}\n" +
                            preview.conflicts.joinToString("\n")
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Conflict Resolution Strategy:", style = MaterialTheme.typography.labelMedium)
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ConflictResolutionStrategy.values().forEach { strategy ->
                                    FilterChip(
                                        selected = selectedStrategy == strategy,
                                        onClick = { selectedStrategy = strategy },
                                        label = { Text(strategy.name) }
                                    )
                                }
                            }
                            Button(
                                onClick = {
                                    model.executeCatalogImport(importJsonText, selectedStrategy)
                                    importJsonText = ""
                                },
                                enabled = preview.totalRecords > 0
                            ) {
                                Text("Execute Import (${selectedStrategy.name})")
                            }
                        }
                    }
                }
            }
        }
    }

    addingBagBeanId?.let { beanId ->
        val packageMass = bagPackageMassText.toBigDecimalOrNull()?.takeIf { it > BigDecimal.ZERO }
        val remainingMass = bagRemainingMassText.toBigDecimalOrNull()
            ?.takeIf { value -> value >= BigDecimal.ZERO && packageMass != null && value <= packageMass }
        AlertDialog(
            onDismissRequest = { addingBagBeanId = null },
            title = { Text("Add bag inventory") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter values from the package and the mass currently remaining. No bag size or balance is assumed.")
                    Field("Package mass (g)", bagPackageMassText, { bagPackageMassText = it }, "library:bag_package_mass", numeric = true)
                    Field(
                        "Current remaining mass (g)",
                        bagRemainingMassText,
                        { bagRemainingMassText = it },
                        "library:bag_remaining_mass",
                        numeric = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        model.addBagToBean(beanId, requireNotNull(packageMass), requireNotNull(remainingMass))
                        addingBagBeanId = null
                    },
                    enabled = packageMass != null && remainingMass != null,
                    modifier = Modifier.heightIn(min = 48.dp).testTag("library:add_bag_confirm")
                ) { Text("Save inventory") }
            },
            dismissButton = { TextButton(onClick = { addingBagBeanId = null }) { Text("Cancel") } }
        )
    }

    deductingBagId?.let { bagId ->
        val doseMass = doseDeductionText.toBigDecimalOrNull()?.takeIf { it > BigDecimal.ZERO }
        AlertDialog(
            onDismissRequest = { deductingBagId = null },
            title = { Text("Record used coffee") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter the measured dose to deduct from this bag.")
                    Field("Used dose (g)", doseDeductionText, { doseDeductionText = it }, "library:bag_dose_mass", numeric = true)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (model.deductDoseFromBag(bagId, requireNotNull(doseMass))) deductingBagId = null
                    },
                    enabled = doseMass != null,
                    modifier = Modifier.heightIn(min = 48.dp).testTag("library:bag_dose_confirm")
                ) { Text("Record dose") }
            },
            dismissButton = { TextButton(onClick = { deductingBagId = null }) { Text("Cancel") } }
        )
    }

    // OCR Modal Dialog
    if (ocrDialogVisible) {
        AlertDialog(
            onDismissRequest = { ocrDialogVisible = false },
            title = { Text("Offline Bag Label Text Review") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Paste text recognized by an offline OCR tool, or type label facts. This screen does not capture images or assign confidence scores.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = ocrSampleText,
                        onValueChange = { ocrSampleText = it },
                        modifier = Modifier.fillMaxWidth().height(120.dp)
                    )
                    Button(
                        onClick = {
                            model.parseBagOcr(null, ocrSampleText)
                            ocrDialogVisible = false
                        }
                    ) {
                        Text("Review label facts")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { ocrDialogVisible = false }) { Text("Cancel") }
            }
        )
    }

    // OCR Draft Confirmation Gate (User MUST confirm before saving)
    state.pendingOcrDraft?.let { draft ->
        var editRoaster by rememberSaveable(draft.photoUri, draft.rawExtractedText) { mutableStateOf(draft.roaster.value ?: "") }
        var editName by rememberSaveable(draft.photoUri, draft.rawExtractedText) { mutableStateOf(draft.beanName.value ?: "") }
        var editOrigin by rememberSaveable(draft.photoUri, draft.rawExtractedText) { mutableStateOf(draft.origin.value ?: "") }
        var editAltitude by rememberSaveable(draft.photoUri, draft.rawExtractedText) {
            mutableStateOf(draft.altitudeMeters.value?.toString().orEmpty())
        }
        var editRoastDate by rememberSaveable(draft.photoUri, draft.rawExtractedText) {
            mutableStateOf(draft.roastDate.value?.toString().orEmpty())
        }
        var editProcess by rememberSaveable(draft.photoUri, draft.rawExtractedText) {
            mutableStateOf(draft.process.value?.name ?: ProcessMethod.UNKNOWN.name)
        }
        var editPackageMass by rememberSaveable(draft.photoUri, draft.rawExtractedText) {
            mutableStateOf(draft.packageMassGrams.value?.toPlainString().orEmpty())
        }
        var editRemainingMass by rememberSaveable(draft.photoUri, draft.rawExtractedText) { mutableStateOf("") }
        val packageMass = editPackageMass.toBigDecimalOrNull()?.takeIf { it > BigDecimal.ZERO }
        val remainingMass = editRemainingMass.toBigDecimalOrNull()
            ?.takeIf { value -> value >= BigDecimal.ZERO && packageMass != null && value <= packageMass }
        val validInventory = (editPackageMass.isBlank() && editRemainingMass.isBlank()) ||
            (packageMass != null && remainingMass != null)
        val altitude = editAltitude.toIntOrNull()?.takeIf { it > 0 }
        val validAltitude = editAltitude.isBlank() || altitude != null
        val roastDate = runCatching { LocalDate.parse(editRoastDate) }.getOrNull()
        val validRoastDate = editRoastDate.isBlank() || roastDate != null

        AlertDialog(
            onDismissRequest = { model.dismissOcrDraft() },
            title = { Text("Confirm OCR Extracted Bean (Gate)") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Verify extracted fields. AI drafts require explicit human review before saving.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Field("Roaster", editRoaster, { editRoaster = it }, "ocr:roaster")
                    Field("Bean Name", editName, { editName = it }, "ocr:name")
                    Field("Origin Country", editOrigin, { editOrigin = it }, "ocr:origin")
                    Select(
                        "Process",
                        editProcess,
                        ProcessMethod.values().map { it.name to it.name.replace('_', ' ') },
                        { editProcess = it },
                        "ocr:process"
                    )
                    Field("Altitude (m)", editAltitude, { editAltitude = it }, "ocr:altitude")
                    Field("Roast Date (YYYY-MM-DD)", editRoastDate, { editRoastDate = it }, "ocr:roastDate")
                    Field("Package mass from label (g)", editPackageMass, { editPackageMass = it }, "ocr:package_mass", numeric = true)
                    Field("Current remaining mass (g)", editRemainingMass, { editRemainingMass = it }, "ocr:remaining_mass", numeric = true)
                    Text(
                        "A bag is added to inventory only when both package mass and current remaining mass are confirmed.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val beanId = UUID.randomUUID().toString()
                        val bags = if (packageMass != null && remainingMass != null) {
                            listOf(
                                BagInventoryItem(
                                    id = UUID.randomUUID().toString(),
                                    beanId = beanId,
                                    packageMassGrams = packageMass,
                                    remainingMassGrams = remainingMass,
                                    status = when {
                                        remainingMass == BigDecimal.ZERO -> BagStatus.FINISHED
                                        remainingMass < packageMass -> BagStatus.ACTIVE
                                        else -> BagStatus.PURCHASED
                                    }
                                )
                            )
                        } else {
                            emptyList()
                        }
                        val confirmed = DetailedBeanRecord(
                            id = beanId,
                            roaster = editRoaster.ifBlank { "Unknown" },
                            name = editName.trim(),
                            originCountry = editOrigin.ifBlank { "Unknown" },
                            altitudeMeters = altitude,
                            roastDate = roastDate,
                            process = runCatching { ProcessMethod.valueOf(editProcess) }.getOrDefault(ProcessMethod.UNKNOWN),
                            bags = bags
                        )
                        model.confirmOcrDraft(confirmed)
                    },
                    enabled = editName.isNotBlank() && validAltitude && validRoastDate && validInventory
                ) {
                    Text("Confirm & Save Bean")
                }
            },
            dismissButton = {
                TextButton(onClick = { model.dismissOcrDraft() }) { Text("Discard Draft") }
            }
        )
    }

    // Existing Bean / Ingredient CRUD Form Dialog
    formKind?.let { kind ->
        val validCoffeeMetadata = kind != "Coffee" || (
            (form["altitude"].orEmpty().isBlank() || form["altitude"].orEmpty().toIntOrNull()?.let { it > 0 } == true) &&
                (form["roastDate"].orEmpty().isBlank() || runCatching { LocalDate.parse(form["roastDate"].orEmpty()) }.isSuccess) &&
                (
                    form["process"].orEmpty().isBlank() || runCatching {
                        ProcessMethod.valueOf(form["process"].orEmpty().uppercase())
                    }.isSuccess
                    ) &&
                (
                    form["roast"].orEmpty().isBlank() || runCatching {
                        RoastLevel.valueOf(form["roast"].orEmpty().uppercase())
                    }.isSuccess
                    )
            )
        val keys = if (kind == "Coffee") {
            listOf(
                "name" to "Name", "country" to "Country", "region" to "Region", "producer" to "Producer",
                "variety" to "Variety", "process" to "Process", "roast" to "Roast", "altitude" to "Altitude (m)",
                "roastDate" to "Roast date YYYY-MM-DD", "degasDays" to "Degas days", "density" to "Known density (g/mL)",
                "provenance" to "Source / provenance"
            )
        } else {
            listOf(
                "name" to "Name",
                "density" to "Known density (g/mL)",
                "allergens" to "Allergens, comma separated",
                "limit" to "Supplier maximum (% w/w)",
                "provenance" to "Supplier / provenance"
            )
        }
        AlertDialog(
            onDismissRequest = { formKind = null },
            title = { Text("${if (editingId == null) "Add" else "Edit"} ${if (kind == "Coffee") "coffee" else "ingredient"}") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    keys.forEach { (key, label) -> Field(label, form[key].orEmpty(), { form = form + (key to it) }, "catalog:$key") }
                    if (kind != "Coffee") {
                        Select(
                            "Food-grade evidence",
                            form["foodGrade"].orEmpty(),
                            listOf("Unknown", "Confirmed food grade", "Not food grade").map { it to it },
                            { form = form + ("foodGrade" to it) },
                            "catalog:foodGrade"
                        )
                    }
                    Text("Leave unknown facts blank. Unknown food-grade ingredients cannot be used in a drink.")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val values = form + ("name" to form["name"].orEmpty())
                        if (kind == "Coffee") {
                            model.saveBean(editingId, values)
                            val detailed = DetailedBeanRecord(
                                id = editingId ?: UUID.randomUUID().toString(),
                                roaster = form["producer"].orEmpty().ifBlank { "Unknown" },
                                name = form["name"].orEmpty(),
                                originCountry = form["country"].orEmpty().ifBlank { "Unknown" },
                                region = form["region"].orEmpty(),
                                varietal = form["variety"].orEmpty(),
                                process = runCatching {
                                    ProcessMethod.valueOf(
                                        form["process"].orEmpty().uppercase()
                                    )
                                }.getOrDefault(ProcessMethod.UNKNOWN),
                                altitudeMeters = form["altitude"]?.toIntOrNull(),
                                roastDate = runCatching { LocalDate.parse(form["roastDate"].orEmpty()) }.getOrNull(),
                                roastLevel = runCatching { RoastLevel.valueOf(form["roast"].orEmpty().uppercase()) }
                                    .getOrDefault(RoastLevel.UNKNOWN)
                            )
                            model.addDetailedBean(detailed)
                        } else {
                            model.saveIngredient(editingId, values)
                        }
                        formKind = null
                    },
                    enabled = form["name"].orEmpty().isNotBlank() && validCoffeeMetadata && !state.busy,
                    modifier = Modifier.testTag("catalog:save")
                ) {
                    Text("Save entry")
                }
            },
            dismissButton = { TextButton(onClick = { formKind = null }) { Text("Cancel") } }
        )
    }
}

private fun beanForm(bean: CoffeeBean) = mapOf(
    "name" to bean.name, "country" to bean.country, "region" to bean.region,
    "producer" to bean.producer, "variety" to bean.variety, "process" to bean.process, "roast" to bean.roastLevel,
    "altitude" to bean.altitudeMeters?.toString().orEmpty(), "roastDate" to bean.roastDate.orEmpty(),
    "degasDays" to bean.degasDays?.toString().orEmpty(),
    "density" to bean.densityGramsPerMl?.toString().orEmpty(), "provenance" to bean.provenance
)
private fun ingredientForm(ingredient: Ingredient) = mapOf(
    "name" to ingredient.name,
    "density" to ingredient.densityGramsPerMl?.toString().orEmpty(),
    "allergens" to ingredient.allergens.joinToString(),
    "limit" to ingredient.supplierMaxPercent?.toString().orEmpty(),
    "provenance" to ingredient.provenance,
    "foodGrade" to when (ingredient.foodGrade) {
        true -> "Confirmed food grade"
        false -> "Not food grade"
        null -> "Unknown"
    }
)

private enum class JournalTab {
    BREWS,
    DIAL_IN,
    COMPARISON,
    ANALYTICS,
    CAFE_CUPS,
    TOOLS
}

@Composable
private fun Journal(state: CoffeeUiState, model: CoffeeViewModel, edit: (Recipe) -> Unit) {
    val context = LocalContext.current
    var activeTab by rememberSaveable { mutableStateOf(JournalTab.BREWS) }
    var query by rememberSaveable { mutableStateOf("") }
    var showArchived by rememberSaveable { mutableStateOf(false) }
    var selectedMethodFilter by rememberSaveable { mutableStateOf("All") }
    var comparing by rememberSaveable { mutableStateOf<String?>(null) }
    var newCafeName by rememberSaveable { mutableStateOf("") }
    var newDrinkName by rememberSaveable { mutableStateOf("") }
    var newCafeRoaster by rememberSaveable { mutableStateOf("") }
    var newCafePrice by rememberSaveable { mutableStateOf("") }
    var newCafeRating by rememberSaveable { mutableStateOf("") }
    var newCafeNotes by rememberSaveable { mutableStateOf("") }
    var showAddCafeDialog by rememberSaveable { mutableStateOf(false) }
    var beanconquerorJsonInput by rememberSaveable { mutableStateOf("") }
    var backupJsonInput by rememberSaveable { mutableStateOf("") }
    var caffeineDoseInput by rememberSaveable { mutableStateOf(18.0) }
    var caffeineArabicaFraction by rememberSaveable { mutableStateOf(1.0) }
    var editingJournalNotesEntry by rememberSaveable { mutableStateOf<String?>(null) }

    Page("Journal") {
        item {
            Heading(
                "Brew journal & analytics",
                "Deterministic brew history, espresso dial-in progression, single-variable guidance, and local streaks."
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TactileFilterChip(
                    selected = activeTab == JournalTab.BREWS,
                    onClick = { activeTab = JournalTab.BREWS },
                    label = {
                        Text(
                            "Brews & Logs",
                            fontWeight = if (activeTab == JournalTab.BREWS) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Book, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("journal:tab_brews").heightIn(min = 48.dp)
                )
                TactileFilterChip(
                    selected = activeTab == JournalTab.DIAL_IN,
                    onClick = { activeTab = JournalTab.DIAL_IN },
                    label = {
                        Text(
                            "Espresso Dial-In",
                            fontWeight = if (activeTab == JournalTab.DIAL_IN) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Tune, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("journal:tab_dialin").heightIn(min = 48.dp)
                )
                TactileFilterChip(
                    selected = activeTab == JournalTab.COMPARISON,
                    onClick = { activeTab = JournalTab.COMPARISON },
                    label = {
                        Text(
                            "Compare (${if (state.activeComparison != null) "Active" else "Select"})",
                            fontWeight = if (activeTab == JournalTab.COMPARISON) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.CompareArrows, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("journal:tab_compare").heightIn(min = 48.dp)
                )
                TactileFilterChip(
                    selected = activeTab == JournalTab.ANALYTICS,
                    onClick = {
                        activeTab = JournalTab.ANALYTICS
                        model.refreshStreakAndStats()
                        model.estimateCaffeineForDose(caffeineDoseInput, caffeineArabicaFraction)
                    },
                    label = {
                        Text(
                            "Analytics & Streak",
                            fontWeight = if (activeTab == JournalTab.ANALYTICS) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.BarChart, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("journal:tab_analytics").heightIn(min = 48.dp)
                )
                TactileFilterChip(
                    selected = activeTab == JournalTab.CAFE_CUPS,
                    onClick = { activeTab = JournalTab.CAFE_CUPS },
                    label = {
                        Text(
                            "Cafe Cups (${state.cafeCupEntries.size})",
                            fontWeight = if (activeTab == JournalTab.CAFE_CUPS) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.LocalCafe, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("journal:tab_cafecups").heightIn(min = 48.dp)
                )
                TactileFilterChip(
                    selected = activeTab == JournalTab.TOOLS,
                    onClick = { activeTab = JournalTab.TOOLS },
                    label = {
                        Text(
                            "Export & Tools",
                            fontWeight = if (activeTab == JournalTab.TOOLS) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.FileDownload, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("journal:tab_tools").heightIn(min = 48.dp)
                )
            }
        }

        when (activeTab) {
            JournalTab.BREWS -> {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.weight(1f)) {
                            Field("Search brews / beans / notes", query, { query = it }, "journal:query")
                        }
                        FilterChip(
                            selected = showArchived,
                            onClick = { showArchived = !showArchived },
                            label = { Text("Archived") },
                            modifier = Modifier.testTag("journal:show_archived")
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "V60", "Espresso", "AeroPress", "Chemex", "High Rated").forEach { filter ->
                            TactileFilterChip(
                                selected = selectedMethodFilter == filter,
                                onClick = { selectedMethodFilter = filter },
                                label = {
                                    Text(
                                        filter,
                                        fontWeight = if (selectedMethodFilter == filter) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                },
                                modifier = Modifier.heightIn(min = 44.dp)
                            )
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { model.repeatLastBrew() },
                            modifier = Modifier.weight(1f).testTag("journal:repeat_last_brew")
                        ) {
                            Text("⚡ Repeat Last Brew")
                        }
                        OutlinedButton(
                            onClick = { model.exportJournalCsv(context) },
                            modifier = Modifier.testTag("journal:export_csv_quick")
                        ) {
                            Text("Export CSV")
                        }
                    }
                }

                val visibleEntries = state.journalEntries.filter { entry ->
                    val matchesQuery = query.isBlank() ||
                        entry.beanName.contains(query, ignoreCase = true) ||
                        entry.brewMethod.contains(query, ignoreCase = true) ||
                        entry.generalNotes.contains(query, ignoreCase = true) ||
                        entry.tags.any { it.contains(query, ignoreCase = true) }

                    val matchesMethod = when (selectedMethodFilter) {
                        "All" -> true
                        "High Rated" -> (entry.rating ?: 0.0) >= 8.5
                        else -> entry.brewMethod.contains(selectedMethodFilter, ignoreCase = true)
                    }
                    matchesQuery && matchesMethod
                }

                if (visibleEntries.isEmpty()) {
                    item {
                        InfoCard(
                            "Your brew history is empty",
                            "Complete a timer session or add a brew log with the values you observed."
                        )
                    }
                }

                items(visibleEntries, key = { it.id }) { entry ->
                    InfoCard(
                        "${entry.beanName} · ${entry.brewMethod}",
                        listOf(
                            "${entry.doseGrams}g dose / " +
                                (if (entry.timerOnly) "recipe target " else "") +
                                "${entry.waterGrams}g water (1:${entry.ratioDenominator}) · " +
                                "${entry.brewDurationSeconds}s · ${entry.waterTemperatureCelsius}°C",
                            "Grind: ${entry.grinderModel.ifBlank { "Burr Grinder" }} [${entry.grindSetting}] · TDS: ${entry.measuredTdsPercent?.let { "$it%" } ?: "—"} · Approx EY: ${entry.extractionYieldPercent?.let { "$it%" } ?: "—"}",
                            "Rating: ⭐ ${entry.rating ?: "—"}/10 · Tags: ${entry.tags.joinToString(", ")}",
                            if (entry.tasteNotes.isNotEmpty()) "Notes: ${entry.tasteNotes.joinToString(", ")}" else "",
                            if (entry.generalNotes.isNotBlank()) "\"${entry.generalNotes}\"" else ""
                        ).filter { it.isNotBlank() }.joinToString("\n")
                    ) {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            val recipe = state.recipes.find { it.id == entry.recipeId }
                            if (recipe != null) {
                                TextButton(onClick = { edit(recipe) }, modifier = Modifier.testTag("journal:edit:${recipe.id}")) {
                                    Text("Repeat / edit")
                                }
                            }
                            TextButton(onClick = {
                                model.selectComparisonSlot(entry, "A")
                                activeTab = JournalTab.COMPARISON
                            }) { Text("Set Slot A") }
                            TextButton(onClick = {
                                model.selectComparisonSlot(entry, "B")
                                activeTab = JournalTab.COMPARISON
                            }) { Text("Set Slot B") }
                            TextButton(onClick = {
                                model.evaluateOvatForEntry(entry)
                                activeTab = JournalTab.DIAL_IN
                            }) { Text("OVAT Coach") }
                            TextButton(onClick = {
                                model.saveEntryAsTemplate(entry, "${entry.beanName} ${entry.brewMethod}")
                            }) { Text("Save Template") }
                            TextButton(onClick = { editingJournalNotesEntry = entry.id }) { Text("Wheel Notes") }
                            TextButton(onClick = { model.deleteJournalEntry(entry.id) }) { Text("Delete") }
                        }
                    }
                }

                if (state.recipes.isNotEmpty()) {
                    item {
                        Heading("Formulated Recipes & Revisions", "Stored recipes with exact measurements and revision history.")
                    }
                    items(state.recipes, key = { "recipe_${it.id}" }) { recipe ->
                        val ratioStr = if (recipe.doseGrams > 0.0) {
                            String.format(
                                java.util.Locale.US,
                                "1:%.1f",
                                recipe.waterGrams / recipe.doseGrams
                            )
                        } else {
                            "—"
                        }
                        InfoCard(
                            "${recipe.name} · v${recipe.version}",
                            listOf(
                                "${recipe.doseGrams}g dose / ${recipe.waterGrams}g water ($ratioStr) · ${recipe.temperatureCelsius}°C",
                                "TDS: ${recipe.measuredTdsPercent?.let { "$it%" } ?: "not measured"} · Beverage: ${recipe.beverageMassGrams?.let { "${it}g" } ?: "not measured"}",
                                "Method: ${recipe.brewMethod} · Time: ${recipe.brewTimeSeconds}s"
                            ).joinToString("\n")
                        ) {
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { edit(recipe) },
                                    modifier = Modifier.testTag("journal:edit:${recipe.id}")
                                ) {
                                    Text("Edit / Revision")
                                }
                                if (state.recipes.count { it.revisionGroupId == recipe.revisionGroupId } > 1) {
                                    OutlinedButton(onClick = { comparing = recipe.revisionGroupId }) {
                                        Text("Compare versions")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            JournalTab.DIAL_IN -> {
                item {
                    InfoCard(
                        "Espresso Dial-In Progression",
                        "Evaluate target vs actual extraction parameters, track flow rate dynamics, and receive single-variable adjustment advice."
                    )
                }

                state.activeOvatRecommendation?.let { rec ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("🎯 One-Variable-at-a-Time (OVAT) Recommendation", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Target Variable: ${rec.targetVariable.name} → ${rec.direction.name}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text("Recommended Adjustment: ${rec.suggestedMagnitude}", style = MaterialTheme.typography.bodyMedium)
                                Text("Physical Mechanism: ${rec.physicalReasoning}", style = MaterialTheme.typography.bodySmall)
                                Text("Expected Extraction Impact: ${rec.expectedImpact}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                val espressoEntries = state.journalEntries.filter { it.isEspresso || it.brewMethod.contains("Espresso", true) }
                if (espressoEntries.isEmpty()) {
                    item { InfoCard("No espresso shots logged", "Complete an espresso timer or add a shot using values you observed.") }
                }

                items(espressoEntries, key = { it.id }) { shot ->
                    val esp = shot.espressoDetails
                    InfoCard(
                        "Espresso Shot: ${shot.beanName}",
                        listOf(
                            "Dose: ${shot.doseGrams}g ${if (esp != null) "(Target: ${esp.targetDoseGrams}g, Δ: ${(shot.doseGrams - esp.targetDoseGrams).let { if (it >= 0) "+$it" else "$it" }}g)" else ""}",
                            "Liquid Yield: ${shot.beverageMassGrams ?: shot.waterGrams}g ${if (esp != null) "(Target: ${esp.targetYieldGrams}g, Δ: ${((shot.beverageMassGrams ?: shot.waterGrams) - esp.targetYieldGrams).let { if (it >= 0) "+$it" else "$it" }}g)" else ""}",
                            "Shot Time: ${shot.brewDurationSeconds}s ${if (esp != null) "(Target: ${esp.targetDurationSeconds}s, Δ: ${shot.brewDurationSeconds - esp.targetDurationSeconds}s)" else ""}",
                            "Mean Flow Rate: ${esp?.meanFlowRateGps ?: "—"} g/s · TDS: ${shot.measuredTdsPercent ?: "—"}%",
                            "Puck Prep: ${esp?.puckPrepMethods?.joinToString(", ") ?: "Standard Tamp"}",
                            "Grind Setting: ${shot.grinderModel} [${shot.grindSetting}]"
                        ).joinToString("\n")
                    ) {
                        Button(onClick = { model.evaluateOvatForEntry(shot) }) {
                            Text("Diagnose with OVAT Coach")
                        }
                    }
                }
            }

            JournalTab.COMPARISON -> {
                item {
                    InfoCard(
                        "Side-by-Side Session Comparison",
                        "Compare two brewing sessions with deterministic numeric deltas across dose, yield, ratio, time, TDS, and EY."
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(Modifier.weight(1f)) {
                            Column(Modifier.padding(12.dp)) {
                                Text("Slot A (Baseline)", style = MaterialTheme.typography.titleSmall)
                                Text(
                                    state.selectedComparisonA?.let { "${it.beanName}\n${it.brewMethod} (${it.doseGrams}g/${it.waterGrams}g)" } ?: "No brew selected",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        Card(Modifier.weight(1f)) {
                            Column(Modifier.padding(12.dp)) {
                                Text("Slot B (Target)", style = MaterialTheme.typography.titleSmall)
                                Text(
                                    state.selectedComparisonB?.let { "${it.beanName}\n${it.brewMethod} (${it.doseGrams}g/${it.waterGrams}g)" } ?: "No brew selected",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { model.clearComparison() }) {
                            Text("Clear Comparison")
                        }
                    }
                }

                state.activeComparison?.let { comp ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("📊 Deterministic Numeric Deltas (B minus A)", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "• Coffee Dose Δ: ${if (comp.doseDeltaGrams >= 0) "+${comp.doseDeltaGrams}" else "${comp.doseDeltaGrams}"} g",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "• Water Mass Δ: ${if (comp.waterDeltaGrams >= 0) "+${comp.waterDeltaGrams}" else "${comp.waterDeltaGrams}"} g",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "• Brew Ratio Δ: ${if (comp.ratioDelta >= 0) "+${comp.ratioDelta}" else "${comp.ratioDelta}"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "• Duration Δ: ${if (comp.durationDeltaSeconds >= 0) "+${comp.durationDeltaSeconds}" else "${comp.durationDeltaSeconds}"} s",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "• Measured TDS Δ: ${comp.tdsDeltaPercent?.let { "${if (it >= 0) "+$it" else "$it"}%" } ?: "N/A"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "• Extraction Yield Δ: ${comp.eyDeltaPercent?.let { "${if (it >= 0) "+$it" else "$it"}%" } ?: "N/A"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "• Rating Δ: ${comp.ratingDelta?.let { "${if (it >= 0) "+$it" else "$it"}" } ?: "N/A"}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "• Water Temp Δ: ${if (comp.temperatureDeltaCelsius >= 0) "+${comp.temperatureDeltaCelsius}" else "${comp.temperatureDeltaCelsius}"} °C",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                item {
                    Text("Select a brew below to assign to Slot A or Slot B:", style = MaterialTheme.typography.titleSmall)
                }

                items(state.journalEntries.take(6), key = { it.id }) { entry ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${entry.beanName} (${entry.brewMethod})", Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                        TextButton(onClick = { model.selectComparisonSlot(entry, "A") }) { Text("Slot A") }
                        TextButton(onClick = { model.selectComparisonSlot(entry, "B") }) { Text("Slot B") }
                    }
                }
            }

            JournalTab.ANALYTICS -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("🔥 Local Brewing Streak", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Current Streak: ${state.brewStreak?.currentStreakDays ?: 0} Days Active",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                "Longest Recorded Streak: ${state.brewStreak?.longestStreakDays ?: 0} Days",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "Total Days Brewed: ${state.brewStreak?.activeDaysCount ?: 0} Days",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            val stats = state.monthlyStats
                            Text(
                                "📅 Monthly Consumption Stats (${stats?.year ?: 2026}-${stats?.month ?: 10})",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text("• Total Brew Sessions: ${stats?.totalBrewsCount ?: 0}", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "• Total Dry Coffee Consumed: ${stats?.totalDoseGrams ?: 0.0} g",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "• Preferred Brewing Method: ${stats?.topBrewMethod ?: "None"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "• Average Rating: ${stats?.averageRating?.let { "⭐ $it / 10" } ?: "Not rated"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "• Average Extraction Yield: ${stats?.averageExtractionYield?.let { "$it%" } ?: "Not measured"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("☕ Caffeine Intake Estimator (S2-050)", style = MaterialTheme.typography.titleMedium)
                            Text("Adjust dose and species blend to estimate dissolved caffeine mass:")
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Dose: ${"%.1f".format(java.util.Locale.US, caffeineDoseInput)} g", Modifier.weight(1f))
                                Slider(
                                    value = caffeineDoseInput.toFloat(),
                                    onValueChange = {
                                        caffeineDoseInput = it.toDouble()
                                        model.estimateCaffeineForDose(caffeineDoseInput, caffeineArabicaFraction)
                                    },
                                    valueRange = 10f..35f,
                                    steps = 24,
                                    modifier = Modifier.weight(2f)
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                FilterChip(
                                    selected = caffeineArabicaFraction >= 0.99,
                                    onClick = {
                                        caffeineArabicaFraction = 1.0
                                        model.estimateCaffeineForDose(caffeineDoseInput, 1.0)
                                    },
                                    label = { Text("100% Arabica") }
                                )
                                FilterChip(
                                    selected = caffeineArabicaFraction in 0.4..0.6,
                                    onClick = {
                                        caffeineArabicaFraction = 0.5
                                        model.estimateCaffeineForDose(caffeineDoseInput, 0.5)
                                    },
                                    label = { Text("50/50 Blend") }
                                )
                                FilterChip(
                                    selected = caffeineArabicaFraction <= 0.01,
                                    onClick = {
                                        caffeineArabicaFraction = 0.0
                                        model.estimateCaffeineForDose(caffeineDoseInput, 0.0)
                                    },
                                    label = { Text("100% Robusta") }
                                )
                            }
                            state.activeCaffeineEstimate?.let { est ->
                                Text("Estimated Caffeine: ~${est.estimatedCaffeineMg} mg", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Uncertainty Range: [${est.uncertaintyRangeMg.first} mg — ${est.uncertaintyRangeMg.second} mg]",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text("Species Basis: ${est.speciesRatioDescription}", style = MaterialTheme.typography.bodySmall)
                                Text(est.disclaimer, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            JournalTab.CAFE_CUPS -> {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                            Text("Cafe Cup Logbook", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(
                                "Manual entry · Zero cloud maps",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TactileButton(
                            onClick = { showAddCafeDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Add Cup", maxLines = 1, softWrap = false, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                if (showAddCafeDialog) {
                    item {
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Log Specialty Cafe Visit", style = MaterialTheme.typography.titleMedium)
                                Field("Cafe Name", newCafeName, { newCafeName = it }, "cafecup:name")
                                Field("Roaster", newCafeRoaster, { newCafeRoaster = it }, "cafecup:roaster")
                                Field("Drink Name", newDrinkName, { newDrinkName = it }, "cafecup:drink")
                                Field("Price Paid", newCafePrice, { newCafePrice = it }, "cafecup:price")
                                Field("Rating (1-10)", newCafeRating, { newCafeRating = it }, "cafecup:rating")
                                Field("Tasting Notes", newCafeNotes, { newCafeNotes = it }, "cafecup:notes")
                                val cafeRating = newCafeRating.toDoubleOrNull()?.takeIf { it.isFinite() && it in 1.0..10.0 }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = {
                                        if (newCafeName.isNotBlank() && newDrinkName.isNotBlank() && cafeRating != null) {
                                            model.saveCafeCup(
                                                CafeCupEntry(
                                                    id = UUID.randomUUID().toString(),
                                                    cafeName = newCafeName,
                                                    roaster = newCafeRoaster,
                                                    drinkName = newDrinkName,
                                                    pricePaid = newCafePrice,
                                                    rating = cafeRating,
                                                    notes = newCafeNotes
                                                )
                                            )
                                            newCafeName = ""
                                            newDrinkName = ""
                                            newCafeRoaster = ""
                                            newCafePrice = ""
                                            newCafeRating = ""
                                            newCafeNotes = ""
                                            showAddCafeDialog = false
                                        }
                                    }, enabled = newCafeName.isNotBlank() && newDrinkName.isNotBlank() && cafeRating != null) {
                                        Text("Save Cafe Cup")
                                    }
                                    TextButton(onClick = { showAddCafeDialog = false }) { Text("Cancel") }
                                }
                            }
                        }
                    }
                }

                if (state.cafeCupEntries.isEmpty()) {
                    item { InfoCard("No cafe visits logged yet", "Tap '+ Add Cafe Cup' to record an artisan coffee experience.") }
                }

                items(state.cafeCupEntries, key = { it.id }) { cup ->
                    InfoCard(
                        "${cup.drinkName} @ ${cup.cafeName}",
                        listOf(
                            "Roaster: ${cup.roaster.ifBlank { "Not recorded" }} · Rating: ⭐ ${cup.rating}/10",
                            "Price: ${cup.pricePaid.ifBlank { "—" }}",
                            if (cup.notes.isNotBlank()) "\"${cup.notes}\"" else ""
                        ).filter { it.isNotBlank() }.joinToString("\n")
                    ) {
                        TextButton(onClick = { model.deleteCafeCup(cup.id) }) { Text("Delete") }
                    }
                }
            }

            JournalTab.TOOLS -> {
                item {
                    InfoCard(
                        "Data Portability & Migration (S2-047, S2-048)",
                        "Export complete journal history to standard CSV or import JSON backups from Beanconqueror."
                    ) {
                        Button(onClick = { model.exportJournalCsv(context) }) {
                            Text("Export Journal to CSV")
                        }
                    }
                }

                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("📥 Beanconqueror JSON Importer (S2-048)", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Paste Beanconqueror export JSON below to migrate beans and brew logs into Coffee Creator:",
                                style = MaterialTheme.typography.bodySmall
                            )
                            OutlinedTextField(
                                value = beanconquerorJsonInput,
                                onValueChange = { beanconquerorJsonInput = it },
                                label = { Text("Beanconqueror JSON payload") },
                                modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp)
                            )
                            Button(
                                onClick = {
                                    if (beanconquerorJsonInput.isNotBlank()) {
                                        model.importBeanconquerorJson(beanconquerorJsonInput)
                                        beanconquerorJsonInput = ""
                                    }
                                },
                                enabled = beanconquerorJsonInput.isNotBlank()
                            ) {
                                Text("Import Beanconqueror Records")
                            }
                        }
                    }
                }

                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🔍 Backup Import Conflict Preview (S2-047)", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Paste Coffee Creator JSON backup to detect 3-way conflicts before writing:",
                                style = MaterialTheme.typography.bodySmall
                            )
                            OutlinedTextField(
                                value = backupJsonInput,
                                onValueChange = { backupJsonInput = it },
                                label = { Text("Backup JSON envelope") },
                                modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { model.previewBackup(backupJsonInput) },
                                    enabled = backupJsonInput.isNotBlank()
                                ) {
                                    Text("Preview Conflicts")
                                }
                                if (state.importConflictPreview != null) {
                                    TextButton(onClick = { model.clearConflictPreview() }) {
                                        Text("Clear Preview")
                                    }
                                }
                            }

                            state.importConflictPreview?.let { preview ->
                                Text(
                                    "New Records: ${preview.newRecordsCount} · Identical: ${preview.identicalCount} · Conflicting: ${preview.conflictCount}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                preview.items.take(5).forEach { item ->
                                    Text("• [${item.status}] ID: ${item.recordId}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    comparing?.let { group ->
        AlertDialog(onDismissRequest = { comparing = null }, title = { Text("Revision comparison") }, text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                state.recipes.filter { it.revisionGroupId == group }.sortedBy { it.version }.forEach { recipe ->
                    Text(
                        "v${recipe.version}: ${recipe.doseGrams} g / ${recipe.waterGrams} g · ${recipe.temperatureCelsius} °C\n" +
                            "TDS ${recipe.measuredTdsPercent ?: "not measured"} · beverage ${recipe.beverageMassGrams ?: "not measured"} g"
                    )
                    state.observations.filter {
                        it.recipeId == recipe.id
                    }.forEach { observation ->
                        Text(
                            "Observed by ${observation.assessor.ifBlank {
                                "unspecified assessor"
                            }}: ${observation.tastingDescriptors.joinToString()}\n${observation.notes}"
                        )
                    }
                    state.estimates.filter { it.recipeId == recipe.id }.forEach { estimated ->
                        Text("ESTIMATED: ${estimated.profile.basis}\n${estimated.profile.uncertaintyDisclaimer}")
                    }
                    HorizontalDivider()
                }
            }
        }, confirmButton = { TextButton(onClick = { comparing = null }) { Text("Close") } })
    }

    if (editingJournalNotesEntry != null) {
        val entryToEdit = state.journalEntries.find { it.id == editingJournalNotesEntry }
        if (entryToEdit != null) {
            FlavorWheelPickerDialog(
                initialSelected = entryToEdit.tasteNotes,
                onDismissRequest = { editingJournalNotesEntry = null },
                onConfirm = { updatedNotes ->
                    model.updateJournalEntryTasteNotes(entryToEdit.id, updatedNotes)
                    editingJournalNotesEntry = null
                }
            )
        } else {
            editingJournalNotesEntry = null
        }
    }
}

private enum class SensoryTab {
    WHEEL,
    ACCESSIBLE_LIST,
    SESSIONS,
    CONSENSUS,
    PHYSICAL_QC,
    CALIBRATION
}

@Composable
private fun Sensory(state: CoffeeUiState, model: CoffeeViewModel) {
    var activeTab by rememberSaveable { mutableStateOf(SensoryTab.WHEEL) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategoryFilter by rememberSaveable { mutableStateOf("All") }

    // Cupping session dialogs
    var showCreateSessionDialog by rememberSaveable { mutableStateOf(false) }
    var newSessionTitle by rememberSaveable { mutableStateOf("") }
    var newSessionLocation by rememberSaveable { mutableStateOf("") }
    var newSessionTemplate by rememberSaveable { mutableStateOf(CuppingTemplateType.SPECIALTY_SCA_EQUIVALENT) }
    var newSessionMasking by rememberSaveable { mutableStateOf(MaskingMode.SINGLE_BLIND_EVALUATOR) }
    var newSessionEvaluators by rememberSaveable { mutableStateOf("") }
    var newSessionSamples by rememberSaveable { mutableStateOf("") }

    // Scoring dialog
    var scoringSample by rememberSaveable { mutableStateOf<String?>(null) }
    var evaluatorIdInput by rememberSaveable { mutableStateOf("") }
    var scoreDescriptors by rememberSaveable { mutableStateOf("") }
    var scoreTaints by rememberSaveable { mutableStateOf("") }
    var scoreFaults by rememberSaveable { mutableStateOf("") }
    var scoreNotes by rememberSaveable { mutableStateOf("") }
    var showWheelPickerForSample by rememberSaveable { mutableStateOf(false) }

    // Reopen dialog
    var showReopenDialog by rememberSaveable { mutableStateOf(false) }
    var reopenReasonInput by rememberSaveable { mutableStateOf("") }

    // Physical QC State
    var greenMoistureInput by rememberSaveable { mutableStateOf("") }
    var greenAwInput by rememberSaveable { mutableStateOf("") }
    var greenFullBlackInput by rememberSaveable { mutableStateOf("") }
    var greenPartialBlackInput by rememberSaveable { mutableStateOf("") }
    var greenBrokenInput by rememberSaveable { mutableStateOf("") }
    var agtronWholeInput by rememberSaveable { mutableStateOf("") }
    var agtronGroundInput by rememberSaveable { mutableStateOf("") }

    // Calibration State
    var calibrationBatchVolumeInput by rememberSaveable { mutableStateOf("") }

    // Roast Rest Dialog
    var showLogObsDialog by rememberSaveable { mutableStateOf(false) }
    var obsBagIdInput by rememberSaveable { mutableStateOf("") }
    var obsDaysInput by rememberSaveable { mutableStateOf("") }
    var obsScoreInput by rememberSaveable { mutableStateOf("") }
    var obsAromaInput by rememberSaveable { mutableStateOf("") }
    var obsAcidityInput by rememberSaveable { mutableStateOf("") }
    var obsSweetnessInput by rememberSaveable { mutableStateOf("") }
    var obsRoastLevelInput by rememberSaveable { mutableStateOf("") }
    var obsProcessInput by rememberSaveable { mutableStateOf("") }
    var obsPerceivedCarbonic by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var obsNotesInput by rememberSaveable { mutableStateOf("") }

    val activeSession = state.cuppingSessions.find { it.id == state.activeCuppingSessionId }
        ?: state.cuppingSessions.firstOrNull()

    Page("Sensory") {
        item {
            Heading(
                "Sensory wheel & professional cupping",
                "Deterministic flavor taxonomy, blind cupping sessions, multi-evaluator consensus, physical QC, and roast-rest tracking."
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TactileFilterChip(
                    selected = activeTab == SensoryTab.WHEEL,
                    onClick = { activeTab = SensoryTab.WHEEL },
                    label = {
                        Text(
                            "Flavor Wheel",
                            fontWeight = if (activeTab == SensoryTab.WHEEL) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Psychology, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("sensory:tab_wheel").heightIn(min = 48.dp)
                )
                TactileFilterChip(
                    selected = activeTab == SensoryTab.ACCESSIBLE_LIST,
                    onClick = { activeTab = SensoryTab.ACCESSIBLE_LIST },
                    label = {
                        Text(
                            "Accessible List",
                            fontWeight = if (activeTab == SensoryTab.ACCESSIBLE_LIST) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.MenuBook, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("sensory:tab_accessible").heightIn(min = 48.dp)
                )
                TactileFilterChip(
                    selected = activeTab == SensoryTab.SESSIONS,
                    onClick = { activeTab = SensoryTab.SESSIONS },
                    label = {
                        Text(
                            "Cupping Sessions (${state.cuppingSessions.size})",
                            fontWeight = if (activeTab == SensoryTab.SESSIONS) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Coffee, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("sensory:tab_sessions").heightIn(min = 48.dp)
                )
                TactileFilterChip(
                    selected = activeTab == SensoryTab.CONSENSUS,
                    onClick = { activeTab = SensoryTab.CONSENSUS },
                    label = {
                        Text(
                            "Consensus & Radar",
                            fontWeight = if (activeTab == SensoryTab.CONSENSUS) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.BarChart, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("sensory:tab_consensus").heightIn(min = 48.dp)
                )
                TactileFilterChip(
                    selected = activeTab == SensoryTab.PHYSICAL_QC,
                    onClick = { activeTab = SensoryTab.PHYSICAL_QC },
                    label = {
                        Text(
                            "Physical QC & Rest",
                            fontWeight = if (activeTab == SensoryTab.PHYSICAL_QC) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Tune, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("sensory:tab_physical").heightIn(min = 48.dp)
                )
                TactileFilterChip(
                    selected = activeTab == SensoryTab.CALIBRATION,
                    onClick = { activeTab = SensoryTab.CALIBRATION },
                    label = {
                        Text(
                            "Calibration Lab",
                            fontWeight = if (activeTab == SensoryTab.CALIBRATION) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Star, null, Modifier.padding(end = 4.dp)) },
                    modifier = Modifier.testTag("sensory:tab_calibration").heightIn(min = 48.dp)
                )
            }
        }

        when (activeTab) {
            SensoryTab.WHEEL -> {
                item {
                    SensoryWheelVisualizer(
                        selectedCategory = SensoryTaxonomyRegistry.DEFAULT_CATEGORIES.firstOrNull {
                            it.name.equals(
                                selectedCategoryFilter,
                                ignoreCase = true
                            )
                        },
                        onCategorySelected = { cat ->
                            selectedCategoryFilter = cat.name
                        },
                        onDescriptorTapped = { desc ->
                            searchQuery = desc.name
                        }
                    )
                }

                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search flavor wheel & descriptors") },
                        placeholder = { Text("e.g. Grapefruit, Jasmine, Blackberry, Citrus...") },
                        modifier = Modifier.fillMaxWidth().testTag("sensory:wheel_search")
                    )
                }

                item {
                    val categories = listOf("All") + SensoryTaxonomyRegistry.DEFAULT_CATEGORIES.map { it.name }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            TactileFilterChip(
                                selected = selectedCategoryFilter == cat,
                                onClick = { selectedCategoryFilter = cat },
                                label = {
                                    Text(
                                        cat,
                                        fontWeight = if (selectedCategoryFilter == cat) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                },
                                modifier = Modifier.heightIn(min = 44.dp)
                            )
                        }
                    }
                }

                val allResults = SensoryTaxonomyRegistry.searchDescriptors(searchQuery)
                val filtered = if (selectedCategoryFilter == "All") {
                    allResults
                } else {
                    allResults.filter { desc ->
                        val cat = SensoryTaxonomyRegistry.DEFAULT_CATEGORIES.find { it.id == desc.categoryId }
                        cat?.name.equals(selectedCategoryFilter, ignoreCase = true)
                    }
                }

                item {
                    Text(
                        "Showing ${filtered.size} flavor descriptors:",
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                items(filtered, key = { it.id }) { desc ->
                    val color: Color = try {
                        Color(AndroidColor.parseColor(desc.colorHex))
                    } catch (_: Exception) {
                        MaterialTheme.colorScheme.primary
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .height(28.dp)
                                    .padding(horizontal = 4.dp)
                                    .animateContentSize(),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = color),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        " ${desc.name} ",
                                        color = if (desc.colorHex == "#FFF9C4" || desc.colorHex == "#FDD835" || desc.colorHex == "#FFF59D") Color.Black else Color.White,
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${desc.categoryId.removePrefix(
                                        "cat_"
                                    ).replaceFirstChar { it.uppercase() }} · ${desc.subcategoryId.removePrefix(
                                        "sub_"
                                    ).replace('_', ' ').replaceFirstChar { it.uppercase() }}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(desc.sensoryNotes, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            SensoryTab.ACCESSIBLE_LIST -> {
                item {
                    InfoCard(
                        "Accessible Flavor Table Alternative (S2-051)",
                        "Meets TalkBack screen reader standards with clear semantic roles, high-contrast color names, and touch targets >= 48dp. No interactive features rely solely on visual color hue."
                    )
                }

                SensoryTaxonomyRegistry.DEFAULT_CATEGORIES.forEach { category ->
                    item {
                        Text(
                            "${category.name.uppercase()} CATEGORY (${category.colorHex})",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                        )
                    }
                    val descriptors = category.subcategories.flatMap { it.descriptors }
                    items(descriptors, key = { it.id }) { desc ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 52.dp)
                                .padding(vertical = 4.dp)
                                .semantics {
                                    stateDescription = desc.accessibilityDescription
                                },
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(desc.name, style = MaterialTheme.typography.titleSmall)
                                    Text(desc.colorHex, style = MaterialTheme.typography.labelSmall)
                                }
                                Text(desc.accessibilityDescription, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            SensoryTab.SESSIONS -> {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                            Text("Cupping Sessions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(
                                "SCA / CoE protocol evaluations",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TactileButton(
                            onClick = { showCreateSessionDialog = true },
                            modifier = Modifier.height(36.dp).testTag("sensory:btn_new_session"),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("New Session", maxLines = 1, softWrap = false, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                if (state.cuppingSessions.isEmpty()) {
                    item {
                        InfoCard("No Cupping Sessions", "Click 'New Session' to initialize an evaluation panel.")
                    }
                } else {
                    items(state.cuppingSessions, key = { it.id }) { session ->
                        val isSelected = session.id == activeSession?.id
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(session.title, style = MaterialTheme.typography.titleSmall)
                                        Text("${session.location} · ${session.template.title}", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = when (session.status) {
                                                SessionStatus.OPEN -> MaterialTheme.colorScheme.primaryContainer
                                                SessionStatus.CLOSED -> MaterialTheme.colorScheme.surfaceVariant
                                                SessionStatus.REOPENED -> MaterialTheme.colorScheme.errorContainer
                                                SessionStatus.DRAFT -> MaterialTheme.colorScheme.secondaryContainer
                                            }
                                        )
                                    ) {
                                        Text(
                                            " ${session.status.name} ",
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.padding(4.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Evaluators: ${session.evaluators.joinToString { it.name }} | Masking: ${session.maskingMode.name}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    FilledTonalButton(onClick = { model.selectCuppingSession(session.id) }) {
                                        Text(if (isSelected) "Active Session" else "Select Session", maxLines = 1, softWrap = false)
                                    }
                                    if (session.status == SessionStatus.OPEN) {
                                        OutlinedButton(onClick = { model.closeCuppingSession(session.id) }) {
                                            Text("Close & Lock", maxLines = 1, softWrap = false)
                                        }
                                    } else if (session.status == SessionStatus.CLOSED) {
                                        OutlinedButton(onClick = { showReopenDialog = true }) {
                                            Text("Reopen...", maxLines = 1, softWrap = false)
                                        }
                                    }
                                    Button(onClick = { model.exportSessionQcReport(session.id) }) {
                                        Text("Export QC", maxLines = 1, softWrap = false)
                                    }
                                }
                            }
                        }
                    }

                    if (activeSession != null) {
                        item {
                            HorizontalDivider(Modifier.padding(vertical = 12.dp))
                            Text(
                                "Samples for '${activeSession.title}'",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        items(activeSession.samples, key = { it.sampleId }) { sample ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                    ) {
                                        Text(
                                            sample.blindCode,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        if (sample.isRevealed) {
                                            Text("Revealed Identity", style = MaterialTheme.typography.labelSmall)
                                        } else {
                                            OutlinedButton(onClick = { model.revealCuppingSample(activeSession.id, sample.sampleId) }) {
                                                Text("Reveal Identity")
                                            }
                                        }
                                    }

                                    if (sample.isRevealed) {
                                        Text(
                                            listOfNotNull(
                                                sample.beanName,
                                                sample.origin?.let { "Origin: $it" },
                                                sample.process?.let { "Process: $it" },
                                                sample.roaster?.let { "Roaster: $it" }
                                            ).joinToString(" · ").ifBlank { "Bean details not recorded" },
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    } else {
                                        Text(
                                            "Identity Masked (Blind Protocol Enforced). Evaluators score purely by cup presentation.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    val sheets = activeSession.sheets.filter { it.sampleId == sample.sampleId }
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        "Submitted Evaluator Sheets: ${sheets.size} / ${activeSession.evaluators.size}",
                                        style = MaterialTheme.typography.labelSmall
                                    )

                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                scoringSample = sample.sampleId
                                                evaluatorIdInput = activeSession.evaluators.firstOrNull()?.id ?: ""
                                            },
                                            enabled = activeSession.status == SessionStatus.OPEN || activeSession.status == SessionStatus.REOPENED
                                        ) {
                                            Text("Score Sample", maxLines = 1, softWrap = false)
                                        }
                                        FilledTonalButton(onClick = {
                                            model.selectCuppingSession(activeSession.id)
                                            model.selectCuppingSample(sample.sampleId)
                                            activeTab = SensoryTab.CONSENSUS
                                        }) {
                                            Text("View Consensus", maxLines = 1, softWrap = false)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            SensoryTab.CONSENSUS -> {
                if (activeSession == null || activeSession.samples.isEmpty()) {
                    item {
                        InfoCard("No Active Session", "Select or create a cupping session first to view consensus calculus.")
                    }
                } else {
                    val sampleId = state.selectedCuppingSampleId ?: activeSession.samples.first().sampleId
                    val consensus = CuppingAnalyticsEngine.computeConsensus(activeSession, sampleId)
                    val sample = activeSession.samples.first { it.sampleId == sampleId }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    "Sample: ${consensus.blindCode}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Consensus Score", style = MaterialTheme.typography.labelSmall)
                                        Text("${consensus.finalConsensusScore} / 100", style = MaterialTheme.typography.headlineMedium)
                                    }
                                    Column {
                                        Text("Panel Agreement (IRAI)", style = MaterialTheme.typography.labelSmall)
                                        Text(
                                            "${consensus.overallPanelAgreementIndex} / 1.00",
                                            style = MaterialTheme.typography.headlineMedium
                                        )
                                    }
                                }
                                Spacer(Modifier.height(6.dp))
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Text(
                                        " [HUMAN OBSERVATION] Empirical Cupper Panel. No uncalibrated AI predictions in official consensus score. ",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(6.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text("Attribute Consensus Breakdown", style = MaterialTheme.typography.titleSmall)
                    }

                    items(consensus.attributeConsensus.entries.toList(), key = { it.key }) { (key, c) ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        key.replace('_', ' ').replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        "Mean: ${c.mean} · Median: ${c.median} · StdDev: ${c.stdDev}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                                    Text("IRAI: ${c.agreementIndex}", style = MaterialTheme.typography.labelMedium)
                                    if (c.outlierEvaluatorIds.isNotEmpty()) {
                                        Text(
                                            "Outlier: ${c.outlierEvaluatorIds.joinToString()}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            "Consensus Descriptor Cloud",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }

                    item {
                        if (consensus.descriptorCloud.isEmpty()) {
                            Text("No descriptors logged for this sample.", style = MaterialTheme.typography.bodySmall)
                        } else {
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                consensus.descriptorCloud.forEach { desc ->
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (desc.isConsensus) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    ) {
                                        Text(
                                            " ${desc.descriptor} (${(desc.selectionFrequency * BigDecimal(100)).toInt()}%) ",
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.padding(6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            "Pairwise Evaluator Jaccard Overlap Matrix",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }

                    items(consensus.overlapMatrix) { pair ->
                        Text(
                            "${pair.evaluatorAId} ↔ ${pair.evaluatorBId}: Jaccard Similarity ${(pair.jaccardSimilarity * BigDecimal(100)).toInt()}%	",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    item {
                        Spacer(Modifier.height(12.dp))
                        SensoryRadar(
                            state.observations.lastOrNull { it.recipeId == state.selectedRecipeId },
                            state.estimates.firstOrNull { it.recipeId == state.selectedRecipeId }?.profile
                        )
                    }
                }
            }

            SensoryTab.PHYSICAL_QC -> {
                item {
                    InfoCard(
                        "Physical Green & Roasted QC (S2-061)",
                        "Enter measured moisture, water activity, and defect counts to calculate a grade. Agtron delta appears after both readings are entered."
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = greenMoistureInput,
                            onValueChange = { greenMoistureInput = it },
                            label = { Text("Moisture %") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = greenAwInput,
                            onValueChange = { greenAwInput = it },
                            label = { Text("Water Activity (aw)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Text("Defect Counts (raw full count):", style = MaterialTheme.typography.titleSmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = greenFullBlackInput,
                            onValueChange = { greenFullBlackInput = it },
                            label = { Text("Full Black") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = greenPartialBlackInput,
                            onValueChange = { greenPartialBlackInput = it },
                            label = { Text("Partial Black") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = greenBrokenInput,
                            onValueChange = { greenBrokenInput = it },
                            label = { Text("Broken/Chipped") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    val moisture = greenMoistureInput.toBigDecimalOrNull()
                        ?.takeIf { it >= BigDecimal.ZERO && it <= BigDecimal("100") }
                    val waterActivity = greenAwInput.toBigDecimalOrNull()
                        ?.takeIf { it >= BigDecimal.ZERO && it <= BigDecimal.ONE }
                    val defectCounts = listOf(
                        greenFullBlackInput.toIntOrNull(),
                        greenPartialBlackInput.toIntOrNull(),
                        greenBrokenInput.toIntOrNull()
                    )
                    val validDefects = defectCounts.all { it != null && it >= 0 }
                    val gradeResult = if (moisture != null && waterActivity != null && validDefects) {
                        CuppingAnalyticsEngine.gradeGreenPhysicalLot(
                            moisture,
                            waterActivity,
                            listOf(
                                GreenCoffeeDefectEntry(GreenDefectType.FULL_BLACK, defectCounts[0]!!),
                                GreenCoffeeDefectEntry(GreenDefectType.PARTIAL_BLACK, defectCounts[1]!!),
                                GreenCoffeeDefectEntry(GreenDefectType.BROKEN_CHIPPED, defectCounts[2]!!)
                            )
                        )
                    } else null
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (gradeResult == null) {
                                Text("Enter valid measurements and defect counts to calculate a grade.", style = MaterialTheme.typography.bodyMedium)
                            } else {
                                Text("Calculated Grade: ${gradeResult.first}", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "Primary Defects: ${gradeResult.second.first} | Secondary Equivalent: ${gradeResult.second.second}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        "Roasted Color QC (Agtron Gourmet Scale)",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = agtronWholeInput,
                            onValueChange = { agtronWholeInput = it },
                            label = { Text("Whole Bean Agtron") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = agtronGroundInput,
                            onValueChange = { agtronGroundInput = it },
                            label = { Text("Ground Agtron") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    val whole = agtronWholeInput.toDoubleOrNull()?.takeIf { it.isFinite() }
                    val ground = agtronGroundInput.toDoubleOrNull()?.takeIf { it.isFinite() }
                    if (whole != null && ground != null) {
                        Text(
                            "Roast Delta (Ground - Whole): ${String.format(java.util.Locale.US, "%.1f", ground - whole)} Agtron points",
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        Text("Enter both Agtron readings to calculate the color delta.", style = MaterialTheme.typography.bodySmall)
                    }
                }

                item {
                    HorizontalDivider(Modifier.padding(vertical = 12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                            Text("Roast-Rest Kinetics", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(
                                "S2-062 degassing curve & logs",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TactileButton(
                            onClick = { showLogObsDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Log Obs", maxLines = 1, softWrap = false, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                item {
                    val prediction = CuppingAnalyticsEngine.predictRoastRestCurve("bag_geisha_lot1", "LIGHT", "WASHED")
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Light Washed Kinetic Curve Prediction", style = MaterialTheme.typography.titleSmall)
                            Text(
                                "Peak Flavor Window: Day ${prediction.estimatedPeakWindowStartDay} to Day ${prediction.estimatedPeakWindowEndDay} post-roast",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "Expected Oxidation / Staling Threshold: Day ${prediction.expectedStalingDay}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "[AI HEURISTIC] ${prediction.provenanceBadge.methodDisclaimer}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                items(state.roastRestObservations, key = { it.id }) { obs ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Day ${obs.daysPostRoast} Post-Roast · Score ${obs.cuppingScore}",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = when (obs.observedPhase) {
                                            DegassingPhase.PEAK_FLAVOR_WINDOW -> MaterialTheme.colorScheme.primaryContainer
                                            DegassingPhase.TOO_FRESH_CO2_INTERFERENCE -> MaterialTheme.colorScheme.errorContainer
                                            DegassingPhase.STABLE_POST_PEAK -> MaterialTheme.colorScheme.secondaryContainer
                                            DegassingPhase.STALE_OXIDIZED -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    )
                                ) {
                                    Text(
                                        " ${obs.observedPhase.label} ",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }
                            Text(
                                "Aroma: ${obs.observedAroma} · Acidity: ${obs.observedAcidity} · Sweetness: ${obs.observedSweetness} | Carbonic Sharpness: ${if (obs.perceivedCarbonicAcid) "Yes" else "No"}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(obs.qualitativeNotes, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            SensoryTab.CALIBRATION -> {
                item {
                    InfoCard(
                        "Safe Food-Grade Calibration Reference Lab (S2-052)",
                        "Standard sensory training exercises for palate calibration with rigorous food-grade purity standards and chemical safety boundaries."
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text("Solution Batch Volume: ", style = MaterialTheme.typography.titleSmall)
                        OutlinedTextField(
                            value = calibrationBatchVolumeInput,
                            onValueChange = { calibrationBatchVolumeInput = it },
                            label = { Text("Batch mL") },
                            modifier = Modifier.width(120.dp)
                        )
                    }
                }

                val volMl = calibrationBatchVolumeInput.toBigDecimalOrNull()?.takeIf { it > BigDecimal.ZERO }

                if (volMl == null) {
                    item {
                        Text(
                            "Enter a positive batch volume to calculate ingredient masses.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else items(SensoryTaxonomyRegistry.STANDARD_CALIBRATION_EXERCISES, key = { it.id }) { ex ->
                    val massG = SensoryTaxonomyRegistry.calculateCalibrationBatch(ex, volMl)
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(ex.compoundName, style = MaterialTheme.typography.titleSmall)
                                    Text(
                                        "${ex.chemicalFormula} · Grade: ${ex.requiredGrade.name}",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    Text(
                                        " $massG g / ${volMl.toInt()} mL ",
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier.padding(6.dp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(ex.preparationInstructions, style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(4.dp))
                            Text("Sensory Cue: ${ex.sensoryCueDescription}", style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(6.dp))
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        "⚠️ SAFETY BOUNDARY: ${ex.safetyWarning}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        "Max Daily Tasting Limit: ${ex.maxDailyConsumptionLimitMl} mL | Allergen: ${ex.allergenNotice}",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog: Create Cupping Session
    if (showCreateSessionDialog) {
        AlertDialog(
            onDismissRequest = { showCreateSessionDialog = false },
            title = { Text("New Cupping Session") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newSessionTitle,
                        onValueChange = { newSessionTitle = it },
                        label = { Text("Session Title") },
                        placeholder = { Text("e.g. Origin Evaluation Batch #4") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newSessionLocation,
                        onValueChange = { newSessionLocation = it },
                        label = { Text("Location / Lab") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Template Protocol:", style = MaterialTheme.typography.labelMedium)
                    CuppingTemplateType.values().forEach { type ->
                        Row(
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            FilterChip(
                                selected = newSessionTemplate == type,
                                onClick = { newSessionTemplate = type },
                                label = { Text(type.name.replace('_', ' ')) }
                            )
                        }
                    }
                    Text("Masking Mode:", style = MaterialTheme.typography.labelMedium)
                    MaskingMode.values().forEach { mode ->
                        FilterChip(
                            selected = newSessionMasking == mode,
                            onClick = { newSessionMasking = mode },
                            label = { Text(mode.name.replace('_', ' ')) }
                        )
                    }
                    OutlinedTextField(
                        value = newSessionEvaluators,
                        onValueChange = { newSessionEvaluators = it },
                        label = { Text("Evaluators (comma separated)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newSessionSamples,
                        onValueChange = { newSessionSamples = it },
                        label = { Text("Sample Bean Names (comma separated)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val evals = newSessionEvaluators.split(',').map { it.trim() }.filter { it.isNotBlank() }
                        val samples = newSessionSamples.split(',').map { it.trim() }.filter { it.isNotBlank() }
                        model.createCuppingSession(
                            title = newSessionTitle,
                            location = newSessionLocation,
                            templateType = newSessionTemplate,
                            maskingMode = newSessionMasking,
                            evaluatorNames = evals,
                            sampleBeanNames = samples
                        )
                        showCreateSessionDialog = false
                    },
                    enabled = newSessionTitle.isNotBlank() &&
                        newSessionEvaluators.split(',').any { it.isNotBlank() } &&
                        newSessionSamples.split(',').any { it.isNotBlank() }
                ) {
                    Text("Create Session")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateSessionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog: Score Sample
    if (scoringSample != null && activeSession != null) {
        val sample = activeSession.samples.find { it.sampleId == scoringSample }
        val template = activeSession.template
        var dynamicScores by remember(scoringSample, activeSession.id) {
            mutableStateOf(emptyMap<String, Float>())
        }
        var selectedDescriptorsList by remember(scoringSample) {
            mutableStateOf(scoreDescriptors.split(',').map { it.trim() }.filter { it.isNotBlank() })
        }

        if (showWheelPickerForSample) {
            FlavorWheelPickerDialog(
                initialSelected = selectedDescriptorsList,
                onDismissRequest = { showWheelPickerForSample = false },
                onConfirm = { picked ->
                    selectedDescriptorsList = picked
                    scoreDescriptors = picked.joinToString(", ")
                    showWheelPickerForSample = false
                }
            )
        }

        AlertDialog(
            onDismissRequest = { scoringSample = null },
            title = { Text("Score Sample: ${sample?.blindCode}") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Evaluator:", style = MaterialTheme.typography.labelMedium)
                    activeSession.evaluators.forEach { eval ->
                        FilterChip(
                            selected = evaluatorIdInput == eval.id,
                            onClick = { evaluatorIdInput = eval.id },
                            label = { Text("${eval.name} (${eval.role.name})") }
                        )
                    }

                    HorizontalDivider(Modifier.padding(vertical = 4.dp))
                    Text(
                        "Protocol: ${template.title}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    template.attributes.forEach { attr ->
                        val minVal = attr.minValue.toFloat()
                        val maxVal = attr.maxValue.toFloat()
                        val step = attr.stepSize.toFloat()
                        val steps = (((maxVal - minVal) / step).toInt() - 1).coerceAtLeast(0)
                        val currVal = dynamicScores[attr.key] ?: minVal
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(attr.label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Text(
                                dynamicScores[attr.key]?.let { String.format(java.util.Locale.US, "%.2f", it) } ?: "Not scored",
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        Slider(
                            value = currVal,
                            onValueChange = { dynamicScores = dynamicScores + (attr.key to it) },
                            valueRange = minVal..maxVal,
                            steps = steps
                        )
                    }

                    HorizontalDivider(Modifier.padding(vertical = 4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Flavor Wheel Notes (${selectedDescriptorsList.size})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        TextButton(onClick = { showWheelPickerForSample = true }) {
                            Icon(Icons.Default.Psychology, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Pick from Wheel")
                        }
                    }

                    if (selectedDescriptorsList.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            selectedDescriptorsList.forEach { descName ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.clickable {
                                        selectedDescriptorsList = selectedDescriptorsList - descName
                                        scoreDescriptors = selectedDescriptorsList.joinToString(", ")
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(descName, style = MaterialTheme.typography.labelSmall)
                                        Spacer(Modifier.width(4.dp))
                                        Icon(Icons.Default.Clear, "Remove", modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = scoreDescriptors,
                        onValueChange = {
                            scoreDescriptors = it
                            selectedDescriptorsList = it.split(',').map { s -> s.trim() }.filter { s -> s.isNotBlank() }
                        },
                        label = { Text("Descriptors (comma separated or wheel-picked)") },
                        placeholder = { Text("e.g. Jasmine, Bergamot, Peach") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = scoreTaints,
                            onValueChange = { scoreTaints = it },
                            label = { Text("Taints (0 if none)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = scoreFaults,
                            onValueChange = { scoreFaults = it },
                            label = { Text("Faults (0 if none)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = scoreNotes,
                        onValueChange = { scoreNotes = it },
                        label = { Text("Evaluator Notes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val scores = dynamicScores.mapValues {
                            BigDecimal(it.value.toDouble()).setScale(2, RoundingMode.HALF_UP)
                        }
                        val descList = selectedDescriptorsList.ifEmpty {
                            scoreDescriptors.split(',').map { it.trim() }.filter { it.isNotBlank() }
                        }
                        model.submitCuppingSheet(
                            sessionId = activeSession.id,
                            sampleId = scoringSample!!,
                            evaluatorId = evaluatorIdInput,
                            attributeScores = scores,
                            descriptors = descList,
                            taintCups = requireNotNull(scoreTaints.toIntOrNull()),
                            faultCups = requireNotNull(scoreFaults.toIntOrNull()),
                            notes = scoreNotes
                        )
                        scoringSample = null
                    },
                    enabled = dynamicScores.keys == template.attributes.map { it.key }.toSet() &&
                        activeSession.evaluators.any { it.id == evaluatorIdInput } &&
                        scoreTaints.toIntOrNull()?.let { it >= 0 } == true &&
                        scoreFaults.toIntOrNull()?.let { it >= 0 } == true
                ) {
                    Text("Submit Score")
                }
            },
            dismissButton = {
                TextButton(onClick = { scoringSample = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog: Reopen Session
    if (showReopenDialog && activeSession != null) {
        AlertDialog(
            onDismissRequest = { showReopenDialog = false },
            title = { Text("Reopen Cupping Session") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("An immutable entry will be recorded in the audit trail log.")
                    OutlinedTextField(
                        value = reopenReasonInput,
                        onValueChange = { reopenReasonInput = it },
                        label = { Text("Reason for reopening") },
                        placeholder = { Text("e.g. Correcting typo on Sample #B2 score") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    model.reopenCuppingSession(
                        activeSession.id,
                        activeSession.evaluators.firstOrNull()?.id ?: "lead",
                        reopenReasonInput
                    )
                    showReopenDialog = false
                }) {
                    Text("Reopen Session")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReopenDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Dialog: QC Report Receipt
    state.generatedQcReceipt?.let { receipt ->
        AlertDialog(
            onDismissRequest = { model.clearQcReceipt() },
            title = { Text("Offline QC Report Receipt") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Cryptographic Fingerprint (SHA-256):", style = MaterialTheme.typography.labelSmall)
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Text(
                            receipt.sha256Fingerprint,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Text("Report Preview:", style = MaterialTheme.typography.labelSmall)
                    Text(receipt.markdownReport, style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                Button(onClick = { model.clearQcReceipt() }) {
                    Text("Dismiss")
                }
            }
        )
    }

    // Dialog: Log Roast Rest Observation
    if (showLogObsDialog) {
        val days = obsDaysInput.toIntOrNull()?.takeIf { it in 0..3650 }
        val score = obsScoreInput.toBigDecimalOrNull()?.takeIf { it in BigDecimal.ZERO..BigDecimal("100") }
        val aroma = obsAromaInput.toBigDecimalOrNull()?.takeIf { it in BigDecimal.ZERO..BigDecimal("10") }
        val acidity = obsAcidityInput.toBigDecimalOrNull()?.takeIf { it in BigDecimal.ZERO..BigDecimal("10") }
        val sweetness = obsSweetnessInput.toBigDecimalOrNull()?.takeIf { it in BigDecimal.ZERO..BigDecimal("10") }
        val knownRoastLevels = setOf("LIGHT", "MEDIUM", "DARK")
        val knownProcesses = setOf("WASHED", "NATURAL", "ANAEROBIC")
        val validObservation = obsBagIdInput.isNotBlank() && days != null && score != null &&
            aroma != null && acidity != null && sweetness != null &&
            obsRoastLevelInput in knownRoastLevels && obsProcessInput in knownProcesses && obsPerceivedCarbonic != null
        AlertDialog(
            onDismissRequest = { showLogObsDialog = false },
            title = { Text("Log Roast-Rest Observation") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = obsBagIdInput,
                        onValueChange = { obsBagIdInput = it },
                        label = { Text("Bag / Lot ID") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Roast level used for the modeled rest estimate:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        knownRoastLevels.forEach { roastLevel ->
                            FilterChip(
                                selected = obsRoastLevelInput == roastLevel,
                                onClick = { obsRoastLevelInput = roastLevel },
                                label = { Text(roastLevel) }
                            )
                        }
                    }
                    Text("Process used for the modeled rest estimate:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        knownProcesses.forEach { process ->
                            FilterChip(
                                selected = obsProcessInput == process,
                                onClick = { obsProcessInput = process },
                                label = { Text(process) }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = obsDaysInput,
                        onValueChange = { obsDaysInput = it },
                        label = { Text("Days Post-Roast") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = obsScoreInput,
                        onValueChange = { obsScoreInput = it },
                        label = { Text("Cupping Score") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = obsAromaInput,
                            onValueChange = { obsAromaInput = it },
                            label = { Text("Aroma") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = obsAcidityInput,
                            onValueChange = { obsAcidityInput = it },
                            label = { Text("Acidity") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = obsSweetnessInput,
                            onValueChange = { obsSweetnessInput = it },
                            label = { Text("Sweetness") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text("Carbonic Acid / Sharpness:", modifier = Modifier.weight(1f))
                        FilterChip(
                            selected = obsPerceivedCarbonic == true,
                            onClick = { obsPerceivedCarbonic = true },
                            label = { Text("Detected") }
                        )
                        FilterChip(
                            selected = obsPerceivedCarbonic == false,
                            onClick = { obsPerceivedCarbonic = false },
                            label = { Text("Not detected") }
                        )
                    }
                    OutlinedTextField(
                        value = obsNotesInput,
                        onValueChange = { obsNotesInput = it },
                        label = { Text("Observation Notes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        "The roast-rest phase is a model classification based on entered roast age, roast level, and process.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val roastAgeDays = requireNotNull(days)
                    val observedScore = requireNotNull(score)
                    val observedAroma = requireNotNull(aroma)
                    val observedAcidity = requireNotNull(acidity)
                    val observedSweetness = requireNotNull(sweetness)
                    val pred = CuppingAnalyticsEngine.predictRoastRestCurve(obsBagIdInput, obsRoastLevelInput, obsProcessInput)
                    val phase = CuppingAnalyticsEngine.classifyObservationPhase(roastAgeDays, pred)
                    val observationTime = System.currentTimeMillis()
                    val obs = RoastRestObservation(
                        id = UUID.randomUUID().toString(),
                        bagId = obsBagIdInput,
                        roastDateEpoch = observationTime - 86400000L * roastAgeDays,
                        observationDateEpoch = observationTime,
                        daysPostRoast = roastAgeDays,
                        cuppingScore = observedScore.setScale(2, RoundingMode.HALF_UP),
                        observedAroma = observedAroma.setScale(1, RoundingMode.HALF_UP),
                        observedAcidity = observedAcidity.setScale(1, RoundingMode.HALF_UP),
                        observedSweetness = observedSweetness.setScale(1, RoundingMode.HALF_UP),
                        perceivedCarbonicAcid = requireNotNull(obsPerceivedCarbonic),
                        qualitativeNotes = obsNotesInput,
                        observedPhase = phase
                    )
                    model.recordRoastRestObservation(obs)
                    showLogObsDialog = false
                }, enabled = validObservation) {
                    Text("Save Observation")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogObsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun Knowledge(state: CoffeeUiState, model: CoffeeViewModel) {
    var query by rememberSaveable { mutableStateOf("") }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> uri?.let(model::importDocument) }
    Page("Knowledge") {
        item {
            Heading(
                "Your local sources",
                "Import UTF-8 TXT or PDF. Scanned pages use bundled English OCR. Source excerpts remain untrusted data."
            )
        }
        item {
            Button(onClick = {
                picker.launch(arrayOf("text/plain", "application/pdf"))
            }, enabled = !state.busy, modifier = Modifier.testTag("knowledge:import")) { Text("Import TXT / PDF") }
        }
        item { Field("Search imported evidence", query, { query = it }, "knowledge:query") }
        item {
            OutlinedButton(onClick = {
                model.searchDocuments(query)
            }, enabled = query.isNotBlank() && !state.busy, modifier = Modifier.testTag("knowledge:search")) { Text("Search locally") }
        }
        state.searchResult?.let { result ->
            item {
                InfoCard(
                    if (result.didAbstain) "No supporting evidence" else "Local source excerpts",
                    "${result.retrievalMode}\n${result.text}"
                ) {
                    result.matchedChunks.forEach { chunk ->
                        TextButton(onClick = { model.inspectChunk(chunk) }) { Text("Inspect ${chunk.citationText}") }
                    }
                }
            }
        }
        if (state.documents.isEmpty()) {
            item {
                InfoCard(
                    "No documents imported",
                    "Coffee Creator does not ship fictional manuals. Add documents you have permission to use."
                )
            }
        }
        items(state.documents, key = { it.id }) { document ->
            InfoCard(
                document.title,
                listOf(
                    "${document.fileType} · ${document.chunkCount} chunks",
                    if (document.fileType == "PDF") "${document.pageCount} real pages" else "Text has no page numbers",
                    "SHA-256 ${document.contentHash}"
                ).joinToString("\n")
            ) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { model.reindex(document) }, enabled = !state.busy) { Text("Reindex") }
                    TextButton(onClick = { model.requestDeletion("document", document.id) }) { Text("Remove source") }
                }
            }
        }
    }
}

@Composable
private fun Models(state: CoffeeUiState, model: CoffeeViewModel) {
    var importingId by rememberSaveable { mutableStateOf<String?>(null) }
    var showManifest by rememberSaveable { mutableStateOf(false) }
    var manifest by rememberSaveable { mutableStateOf("") }
    var expandedModel by rememberSaveable { mutableStateOf<String?>(null) }
    val directoryPicker =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri -> uri?.let(model::selectDirectory) }
    val modelPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        val asset = state.models.find { it.id == importingId }
        if (uri != null && asset != null) model.importModel(asset, uri)
    }
    Page("Models") {
        item {
            Heading(
                "Local AI models",
                "A model is installed only after exact size and SHA-256 validation. Downloads use network only when you request them."
            )
        }
        item {
            InfoCard(
                "User-owned model directory",
                state.directory ?: "Choose a SAF folder before downloading. Verified model files and manifests are published there."
            ) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        directoryPicker.launch(null)
                    }, enabled = !state.busy, modifier = Modifier.testTag("models:directory")) { Text("Choose directory") }
                    OutlinedButton(onClick = model::discoverAssets, enabled = state.directory != null && !state.busy) {
                        Text("Recover bundles")
                    }
                    TextButton(onClick = { showManifest = true }) { Text("Add manifest") }
                }
            }
        }
        items(state.models, key = { it.id }) { asset ->
            InfoCard(
                asset.name,
                listOf(
                    "${asset.format} · ${"%.2f".format(java.util.Locale.ROOT, asset.expectedSizeBytes / DECIMAL_GIGABYTE_BYTES)} GB",
                    BuiltInAssets.purpose(asset),
                    "Status: ${asset.status.name.lowercase()}",
                    "License: ${asset.license.substringBefore(';').take(LICENSE_SUMMARY_CHARS)}",
                    asset.error.orEmpty()
                ).joinToString("\n")
            ) {
                if (asset.status == ModelStatus.DOWNLOADING || asset.status == ModelStatus.QUEUED) {
                    LinearProgressIndicator(
                        progress = { asset.progressPercent / 100f },
                        modifier = Modifier.fillMaxWidth().semantics {
                            stateDescription =
                                "${asset.progressPercent} percent downloaded; verification pending"
                        }
                    )
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (asset.status !in
                        setOf(
                            ModelStatus.INSTALLED,
                            ModelStatus.DOWNLOADING,
                            ModelStatus.QUEUED,
                            ModelStatus.VERIFYING,
                            ModelStatus.PUBLISHING
                        )
                    ) {
                        Button(
                            onClick = { model.modelAction(asset, "download") },
                            enabled = state.directory != null && !state.busy && asset.downloadUrl.isNotBlank(),
                            modifier = Modifier.testTag("models:download:${asset.id}")
                        ) {
                            Text(
                                if (asset.downloadedBytes >
                                    0
                                ) {
                                    "Resume download"
                                } else {
                                    "Download"
                                }
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                importingId = asset.id
                                modelPicker.launch(arrayOf("*/*"))
                            },
                            enabled =
                            state.directory != null && !state.busy
                        ) { Text("Import matching GGUF") }
                    }
                    if (asset.status in
                        setOf(ModelStatus.DOWNLOADING, ModelStatus.QUEUED)
                    ) {
                        OutlinedButton(onClick = { model.modelAction(asset, "pause") }) { Text("Pause") }
                    }
                    if (asset.status in
                        setOf(ModelStatus.DOWNLOADING, ModelStatus.PAUSED, ModelStatus.FAILED)
                    ) {
                        TextButton(onClick = { model.modelAction(asset, "cancel") }) { Text("Cancel transfer") }
                    }
                    if (asset.status == ModelStatus.INSTALLED) {
                        Button(onClick = {
                            model.modelAction(asset, "load")
                        }, enabled = !state.busy && !state.generating, modifier = Modifier.testTag("models:load:${asset.id}")) {
                            Text("Load local model")
                        }
                        TextButton(onClick = {
                            model.modelAction(asset, "forget")
                        }, enabled = !state.busy) { Text("Forget registry entry") }
                    }
                }
                TextButton(
                    onClick = { expandedModel = if (expandedModel == asset.id) null else asset.id },
                    modifier = Modifier.heightIn(min = 48.dp).testTag("models:details:${asset.id}")
                ) { Text(if (expandedModel == asset.id) "Hide model details" else "Source, license & integrity") }
                if (expandedModel == asset.id) {
                    HorizontalDivider()
                    Text(BuiltInAssets.guidance(asset).orEmpty())
                    Text("Context limit in this app: 1024 tokens. Load succeeds only after runtime and RAM validation.")
                    Text("Expected ${asset.expectedSizeBytes} bytes · ${asset.downloadedBytes} received bytes")
                    Text("Revision ${asset.revision}")
                    Text(asset.license)
                    Text("SHA-256 ${asset.sha256}")
                    Text("Source ${asset.downloadUrl}")
                }
            }
        }
        if (state.loadedModel !=
            null
        ) {
            item {
                OutlinedButton(onClick = {
                    state.models.firstOrNull()?.let { model.modelAction(it, "unload") }
                }, enabled = !state.generating) { Text("Unload native model") }
            }
        }
        item {
            Text(
                "Reinstall revokes grants. Reselect this directory to rediscover verified bundles; restore " +
                    "a separate exported backup for recipes/chat. Forgetting an entry keeps user-owned files."
            )
        }
    }
    if (showManifest) {
        AlertDialog(onDismissRequest = { showManifest = false }, title = { Text("Add model manifest") }, text = {
            Column {
                Text("Provide id, name, revision, format, expectedSizeBytes, sha256, downloadUrl, license, runtime and tokenizerSupport.")
                OutlinedTextField(manifest, {
                    manifest =
                        it
                }, label = { Text("Manifest JSON") }, modifier = Modifier.heightIn(max = 320.dp).verticalScroll(rememberScrollState()))
            }
        }, confirmButton = {
            TextButton(onClick = {
                model.registerManifest(manifest)
                showManifest = false
            }, enabled = manifest.isNotBlank()) { Text("Validate manifest") }
        }, dismissButton = {
            TextButton(onClick = {
                showManifest =
                    false
            }) { Text("Cancel") }
        })
    }
}

@Composable
private fun Chat(state: CoffeeUiState, model: CoffeeViewModel) {
    var prompt by rememberSaveable { mutableStateOf("") }
    var showQuizDialog by rememberSaveable { mutableStateOf(false) }
    var quizRoast by rememberSaveable { mutableStateOf(RoastPreference.MEDIUM_LIGHT) }
    var quizAcidity by rememberSaveable { mutableStateOf(AcidityTolerance.HIGH_ENTHUSIAST) }
    var quizSweetness by rememberSaveable { mutableStateOf(SweetnessPriority.CORE_CRUCIAL) }
    var quizBitterness by rememberSaveable { mutableStateOf(BitternessSensitivity.AVERSE) }
    var quizBody by rememberSaveable { mutableStateOf(BodyPreference.SILKY_MEDIUM) }

    var customPersonaText by rememberSaveable { mutableStateOf("") }
    var showCustomPersonaDialog by rememberSaveable { mutableStateOf(false) }

    var selectedRecMethod by rememberSaveable { mutableStateOf("Pour Over (V60)") }

    var ovatDiagnosis by rememberSaveable { mutableStateOf<SensoryFeedbackDiagnosis?>(null) }
    var ovatActualSec by rememberSaveable { mutableStateOf("") }
    var ovatTargetSec by rememberSaveable { mutableStateOf("") }

    var elevationInput by rememberSaveable { mutableStateOf("") }
    var auditIngName by rememberSaveable { mutableStateOf("") }
    var auditIngPct by rememberSaveable { mutableStateOf("") }

    Page("AI Chat") {
        item {
            Heading(
                "Local coffee assistant & personalization",
                "${state.loadedModel ?: "No model loaded"}. Grounded on-device intelligence. " +
                    "Exact Kotlin domain cards and physics rules remain authoritative."
            )
        }

        // ====================================================================
        // 1. Barista Persona Presets & Custom Prompt (S2-072)
        // ====================================================================
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Barista Persona", style = MaterialTheme.typography.titleMedium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = state.selectedPersona?.id == BaristaPersonaPreset.SPECIALTY_Q_GRADER.id,
                            onClick = { model.selectBaristaPersonaPreset(BaristaPersonaPreset.SPECIALTY_Q_GRADER) },
                            label = { Text("Specialty Q-Grader") }
                        )
                        FilterChip(
                            selected = state.selectedPersona?.id == BaristaPersonaPreset.FRIENDLY_HOME_BARISTA.id,
                            onClick = { model.selectBaristaPersonaPreset(BaristaPersonaPreset.FRIENDLY_HOME_BARISTA) },
                            label = { Text("Friendly Barista") }
                        )
                        FilterChip(
                            selected = state.selectedPersona?.id == BaristaPersonaPreset.RD_COFFEE_CHEMIST.id,
                            onClick = { model.selectBaristaPersonaPreset(BaristaPersonaPreset.RD_COFFEE_CHEMIST) },
                            label = { Text("R&D Chemist") }
                        )
                        FilterChip(
                            selected = state.selectedPersona?.id == "custom_persona",
                            onClick = { showCustomPersonaDialog = true },
                            label = { Text("Custom Prompt") }
                        )
                    }
                    Text(
                        state.selectedPersona?.description ?: "Select a persona to guide response tone and extraction focus.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // ====================================================================
        // 2. Offline Personal Palate Quiz & Dynamic Profile (S2-063, S2-064)
        // ====================================================================
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Personal Palate Profile", style = MaterialTheme.typography.titleMedium)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                "Evidence: ${state.palateProfile.qualifiedSessionCount} sessions",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        "Roast: ${(state.palateProfile.vector.roast * 10).toInt()}/10 · " +
                            "Acidity: ${(state.palateProfile.vector.acidity * 10).toInt()}/10 · " +
                            "Sweetness: ${(state.palateProfile.vector.sweetness * 10).toInt()}/10 · " +
                            "Bitterness: ${(state.palateProfile.vector.bitterness * 10).toInt()}/10 · " +
                            "Body: ${(state.palateProfile.vector.body * 10).toInt()}/10",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { showQuizDialog = true }) {
                            Text(if (state.palateProfile.isQuizCompleted) "Retake Palate Quiz" else "Take Palate Quiz")
                        }
                        OutlinedButton(onClick = {
                            model.updatePalateSliders(
                                acidity = ((state.palateProfile.vector.acidity + 0.1).coerceAtMost(1.0))
                            )
                        }) {
                            Text("+ Acidity Bias")
                        }
                    }
                }
            }
        }

        // ====================================================================
        // 3. Explainable Inventory Recommendations (S2-065)
        // ====================================================================
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Inventory Recommendations", style = MaterialTheme.typography.titleMedium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Pour Over (V60)", "Espresso", "AeroPress", "French Press").forEach { method ->
                            FilterChip(
                                selected = selectedRecMethod == method,
                                onClick = {
                                    selectedRecMethod = method
                                    model.generateInventoryRecommendations(method)
                                },
                                label = { Text(method) }
                            )
                        }
                    }
                    Button(
                        onClick = { model.generateInventoryRecommendations(selectedRecMethod) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Check evidence for $selectedRecMethod matches")
                    }
                    if (state.inventoryRecommendations.isNotEmpty()) {
                        state.inventoryRecommendations.take(3).forEachIndexed { idx, item ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Column(Modifier.padding(10.dp)) {
                                    Text(
                                        "#${idx + 1} ${item.beanName} · ${(item.cosineSimilarity * 100).toInt()}% Match",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(item.explanationText, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    } else {
                        Text(
                            "Recommendations need recorded bag mass, roast date, human sensory notes linked to each bean, and compatible equipment. Unknown values are not estimated.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // ====================================================================
        // 4. OVAT Brewing Feedback Loop (S2-066)
        // ====================================================================
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Single-Variable Dial-In (OVAT)", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Proposes one controlled physical variable adjustment without confounding other variables.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        SensoryFeedbackDiagnosis.values().forEach { diag ->
                            FilterChip(
                                selected = ovatDiagnosis == diag,
                                onClick = { ovatDiagnosis = diag },
                                label = { Text(diag.name.replace("_", " ")) }
                            )
                        }
                    }
                    val actualSeconds = ovatActualSec.toIntOrNull()?.takeIf { it > 0 }
                    val targetSeconds = ovatTargetSec.toIntOrNull()?.takeIf { it > 0 }
                    val canPropose = ovatDiagnosis != null && actualSeconds != null && targetSeconds != null
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = ovatActualSec,
                            onValueChange = { ovatActualSec = it },
                            label = { Text("Actual (s)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = ovatTargetSec,
                            onValueChange = { ovatTargetSec = it },
                            label = { Text("Target (s)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Button(
                        onClick = {
                            model.proposeSingleVariableAdjustment(
                                requireNotNull(ovatDiagnosis),
                                requireNotNull(actualSeconds),
                                requireNotNull(targetSeconds)
                            )
                        },
                        enabled = canPropose,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                    ) {
                        Text("Diagnose & Propose Adjustment")
                    }
                    if (!canPropose) {
                        Text("Select a diagnosis and enter positive observed and target times.", style = MaterialTheme.typography.bodySmall)
                    }
                    state.ovatAdjustmentProposal?.let { prop ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        ) {
                            Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    "Proposed: ${prop.variableAdjusted} -> ${prop.proposedValue}",
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(prop.diagnosticReason, style = MaterialTheme.typography.bodySmall)
                                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = model::applyOvatAdjustmentToDraft) { Text("Apply to Recipe Draft") }
                                    OutlinedButton(onClick = model::dismissOvatAdjustment) { Text("Dismiss") }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ====================================================================
        // 6. Offline Weather & Altitude Boiling Point (S2-073)
        // ====================================================================
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Local Altitude & Thermodynamics", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Approximate offline boiling-point estimate from an elevation you enter. No GPS or weather service is used.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = elevationInput,
                            onValueChange = { elevationInput = it },
                            label = { Text("Elevation (m)") },
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                val elev = elevationInput.toDoubleOrNull() ?: return@Button
                                model.updateWeatherParameters(elev)
                            },
                            enabled = elevationInput.toDoubleOrNull()?.isFinite() == true,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("Compute")
                        }
                    }
                    Text(
                        state.weatherParams.calculatedBoilingPointCelsius?.let { boilingPoint ->
                            "Elevation: ${state.weatherParams.elevationMeters?.toInt()}m · Boiling Point: $boilingPoint°C · Safe Brew Max: ${boilingPoint - 0.5}°C"
                        } ?: "Elevation and boiling point are unknown until you enter an elevation.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // ====================================================================
        // 7. Ingredient & Allergen Safety Gate (S2-074)
        // ====================================================================
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ingredient & Allergen Safety Gate", style = MaterialTheme.typography.titleMedium)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = auditIngName,
                            onValueChange = { auditIngName = it },
                            label = { Text("Ingredient Name") },
                            modifier = Modifier.weight(1.5f)
                        )
                        OutlinedTextField(
                            value = auditIngPct,
                            onValueChange = { auditIngPct = it },
                            label = { Text("Dose %") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Text(
                        "Food-grade status, allergens, and supplier limits come from the local ingredient record. Missing evidence blocks approval.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    val ingredientDosePercent = auditIngPct.toDoubleOrNull()?.takeIf { it.isFinite() && it in 0.0..100.0 }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                model.auditIngredientSafety(auditIngName, requireNotNull(ingredientDosePercent))
                            },
                            enabled = auditIngName.isNotBlank() && ingredientDosePercent != null,
                            modifier = Modifier.heightIn(min = 48.dp)
                        ) {
                            Text("Audit Safety")
                        }
                    }
                    state.safetyAuditResult?.let { res ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when (res.level) {
                                IngredientSafetyLevel.SAFE_FOOD_GRADE -> MaterialTheme.colorScheme.primaryContainer
                                IngredientSafetyLevel.WARNING_ALLERGEN_PRESENT -> MaterialTheme.colorScheme.secondaryContainer
                                IngredientSafetyLevel.CRITICAL_NON_FOOD_PROHIBITED -> MaterialTheme.colorScheme.errorContainer
                            },
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        ) {
                            Column(Modifier.padding(10.dp)) {
                                Text("Safety Level: ${res.level.name}", style = MaterialTheme.typography.titleSmall)
                                res.flags.forEach { flag -> Text("• $flag", style = MaterialTheme.typography.bodySmall) }
                            }
                        }
                    }
                }
            }
        }

        // ====================================================================
        // 8. Grounded AI Barista Chat & Messages (S2-068, S2-071)
        // ====================================================================
        item {
            Text("Barista Chat Session", style = MaterialTheme.typography.titleMedium)
        }
        item {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    "Ask from my sources",
                    "Help plan a brew",
                    "Explain my observations",
                    "Calculate extraction yield"
                ).forEach { preset ->
                    OutlinedButton(onClick = { prompt = preset }, enabled = !state.generating) { Text(preset) }
                }
            }
        }
        items(state.messages, key = { it.id }) { message ->
            val title = when (message.role) {
                "user" -> "You"
                "tool" -> "KOTLIN TOOL · ${message.toolCallName.orEmpty()}"
                else -> "GENERATED · local model, unverified"
            }
            InfoCard(title, message.content) {
                state.chunks.filter { it.id in message.sourceChunkIds }.forEach { chunk ->
                    TextButton(onClick = { model.inspectChunk(chunk) }) {
                        Text("${if (chunk.citationText in message.citations) "Cited" else "Retrieved context"}: ${chunk.citationText}")
                    }
                }
            }
        }
        state.pendingToolJson?.let { json ->
            item {
                InfoCard(
                    "Review quantities before calculation",
                    "Check each value, unit and measured field. Edit by sending corrected JSON.\n$json"
                ) {
                    Button(onClick = model::confirmToolInputs, enabled = !state.generating && !state.busy) {
                        Text("Confirm inputs and calculate")
                    }
                    TextButton(onClick = model::dismissToolInputs) { Text("Discard") }
                }
            }
        }
        state.pendingRecipe?.let { recipe ->
            item {
                InfoCard(
                    "Confirm draft save",
                    "${recipe.name}: ${recipe.doseGrams} g dose / ${recipe.waterGrams} g water. No measured TDS or beverage mass supplied."
                ) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = model::confirmAiRecipe, enabled = !state.busy) { Text("Confirm save") }
                        OutlinedButton(onClick = model::dismissAiRecipe) { Text("Discard draft") }
                    }
                }
            }
        }
        item {
            OutlinedTextField(
                prompt,
                { prompt = it },
                label = { Text("Message or typed tool JSON") },
                modifier = Modifier.fillMaxWidth().testTag("chat:input"),
                minLines = 2,
                maxLines = 5
            )
        }
        item {
            Button(
                onClick = {
                    model.sendChat(prompt)
                    prompt = ""
                },
                enabled = prompt.isNotBlank() && !state.generating && !state.busy && state.ready,
                modifier = Modifier.fillMaxWidth().testTag("chat:send")
            ) {
                Text("Send locally")
            }
        }
        if (state.generating) {
            item {
                OutlinedButton(onClick = model::cancelGeneration, modifier = Modifier.testTag("chat:cancel")) { Text("Cancel generation") }
            }
        }
        item {
            InfoCard(
                "Manual typed tools work without an LLM",
                "Required values are validated; missing values are rejected. Example schema: tool = " +
                    "calculate_brew_ratio; arguments = doseGrams and waterGrams. No generated code runs."
            )
        }
    }

    // Dialog for Palate Quiz
    if (showQuizDialog) {
        AlertDialog(
            onDismissRequest = { showQuizDialog = false },
            title = { Text("Offline Palate Quiz") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("1. Roast Preference:", style = MaterialTheme.typography.bodyMedium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        RoastPreference.values().forEach { r ->
                            FilterChip(selected = quizRoast == r, onClick = { quizRoast = r }, label = { Text(r.name) })
                        }
                    }
                    Text("2. Acidity Tolerance:", style = MaterialTheme.typography.bodyMedium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        AcidityTolerance.values().forEach { a ->
                            FilterChip(selected = quizAcidity == a, onClick = { quizAcidity = a }, label = { Text(a.name) })
                        }
                    }
                    Text("3. Sweetness Priority:", style = MaterialTheme.typography.bodyMedium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        SweetnessPriority.values().forEach { s ->
                            FilterChip(selected = quizSweetness == s, onClick = { quizSweetness = s }, label = { Text(s.name) })
                        }
                    }
                    Text("4. Bitterness Sensitivity:", style = MaterialTheme.typography.bodyMedium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        BitternessSensitivity.values().forEach { b ->
                            FilterChip(selected = quizBitterness == b, onClick = { quizBitterness = b }, label = { Text(b.name) })
                        }
                    }
                    Text("5. Body Preference:", style = MaterialTheme.typography.bodyMedium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        BodyPreference.values().forEach { m ->
                            FilterChip(selected = quizBody == m, onClick = { quizBody = m }, label = { Text(m.name) })
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    model.submitPalateQuiz(
                        PalateQuizAnswers(
                            roast = quizRoast,
                            acidity = quizAcidity,
                            sweetness = quizSweetness,
                            bitterness = quizBitterness,
                            body = quizBody
                        )
                    )
                    showQuizDialog = false
                }) {
                    Text("Save & Calibrate")
                }
            },
            dismissButton = {
                TextButton(onClick = { showQuizDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Dialog for Custom Persona
    if (showCustomPersonaDialog) {
        AlertDialog(
            onDismissRequest = { showCustomPersonaDialog = false },
            title = { Text("Custom Persona System Prompt") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Max 300 characters. Automatically sanitized against prompt injection.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = customPersonaText,
                        onValueChange = { customPersonaText = it.take(300) },
                        label = { Text("System Prompt (${customPersonaText.length}/300)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        model.updateCustomPersonaPrompt(customPersonaText)
                        showCustomPersonaDialog = false
                    },
                    enabled = customPersonaText.isNotBlank()
                ) {
                    Text("Save & Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomPersonaDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun Settings(state: CoffeeUiState, model: CoffeeViewModel) {
    val context = LocalContext.current
    val isIndonesian = state.unitPreferences.language == AppLanguage.INDONESIAN
    var restoring by rememberSaveable { mutableStateOf<Uri?>(null) }
    var activeHelpDialog by rememberSaveable { mutableStateOf<String?>(null) }
    var personaText by rememberSaveable(state.selectedPersona?.id) { mutableStateOf(state.selectedPersona?.systemPrompt.orEmpty()) }
    var tokens by rememberSaveable(state.maxTokens) { mutableStateOf(state.maxTokens.toString()) }
    var temperature by rememberSaveable(state.temperature) { mutableStateOf(state.temperature.toString()) }
    val exporter =
        rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
            uri?.let { model.exportBackup(it, context.contentResolver) }
        }
    val restorer = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> restoring = uri }

    Page("Settings") {
        item {
            Heading(
                if (isIndonesian) "Keandalan & Pengaturan" else "Reliability & Settings",
                if (isIndonesian) {
                    "Data Anda tersimpan lokal di perangkat. Bebas SDK telemetri atau inferensi cloud."
                } else {
                    "Your records stay 100% on-device. Zero telemetry SDKs, cloud tracking, or remote inference."
                }
            )
        }

        // ====================================================================
        // S2-078: On-Device Language Toggle (EN / ID)
        // ====================================================================
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("settings:language_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        if (isIndonesian) "Bahasa Aplikasi (On-Device Language)" else "App Language (On-Device Language)",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        if (isIndonesian) {
                            "Peralihan bahasa instan tanpa koneksi internet atau restart aplikasi."
                        } else {
                            "Instant on-device language toggle without network connection or app restart."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = state.unitPreferences.language == AppLanguage.ENGLISH,
                            onClick = { model.setLanguage(AppLanguage.ENGLISH) },
                            label = { Text("English (EN)") },
                            modifier = Modifier.testTag("settings:lang_en")
                        )
                        FilterChip(
                            selected = state.unitPreferences.language == AppLanguage.INDONESIAN,
                            onClick = { model.setLanguage(AppLanguage.INDONESIAN) },
                            label = { Text("Bahasa Indonesia (ID)") },
                            modifier = Modifier.testTag("settings:lang_id")
                        )
                    }
                }
            }
        }

        // ====================================================================
        // S2-078: Localized Unit Preferences (Metric / Imperial)
        // ====================================================================
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("settings:units_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        if (isIndonesian) "Preferensi Satuan Fisika Seduh" else "Brewing Physics Unit Preferences",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        if (isIndonesian) {
                            "Konversi matematis presisi tinggi (BigDecimal) tanpa akumulasi drift pembulatan."
                        } else {
                            "Exact high-precision BigDecimal conversions with zero cumulative rounding drift."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Temperature unit
                    Text(if (isIndonesian) "Suhu Air:" else "Water Temperature:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = state.unitPreferences.temperatureUnit == TemperatureUnit.CELSIUS,
                            onClick = { model.setUnitPreferences(state.unitPreferences.copy(temperatureUnit = TemperatureUnit.CELSIUS)) },
                            label = { Text("Celsius (°C)") }
                        )
                        FilterChip(
                            selected = state.unitPreferences.temperatureUnit == TemperatureUnit.FAHRENHEIT,
                            onClick = {
                                model.setUnitPreferences(
                                    state.unitPreferences.copy(temperatureUnit = TemperatureUnit.FAHRENHEIT)
                                )
                            },
                            label = { Text("Fahrenheit (°F)") }
                        )
                    }

                    // Mass unit
                    Text(if (isIndonesian) "Massa Biji & Air:" else "Coffee & Water Mass:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = state.unitPreferences.massUnit == MassUnit.GRAMS,
                            onClick = { model.setUnitPreferences(state.unitPreferences.copy(massUnit = MassUnit.GRAMS)) },
                            label = { Text("Grams (g)") }
                        )
                        FilterChip(
                            selected = state.unitPreferences.massUnit == MassUnit.OUNCES,
                            onClick = { model.setUnitPreferences(state.unitPreferences.copy(massUnit = MassUnit.OUNCES)) },
                            label = { Text("Ounces (oz)") }
                        )
                    }

                    // Volume & Elevation
                    Text(if (isIndonesian) "Volume & Ketinggian:" else "Volume & Elevation:", style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = state.unitPreferences.volumeUnit == VolumeUnit.MILLILITERS,
                            onClick = { model.setUnitPreferences(state.unitPreferences.copy(volumeUnit = VolumeUnit.MILLILITERS)) },
                            label = { Text("mL") }
                        )
                        FilterChip(
                            selected = state.unitPreferences.volumeUnit == VolumeUnit.FLUID_OUNCES,
                            onClick = { model.setUnitPreferences(state.unitPreferences.copy(volumeUnit = VolumeUnit.FLUID_OUNCES)) },
                            label = { Text("fl oz") }
                        )
                        FilterChip(
                            selected = state.unitPreferences.elevationUnit == ElevationUnit.METERS,
                            onClick = { model.setUnitPreferences(state.unitPreferences.copy(elevationUnit = ElevationUnit.METERS)) },
                            label = { Text("masl") }
                        )
                        FilterChip(
                            selected = state.unitPreferences.elevationUnit == ElevationUnit.FEET,
                            onClick = { model.setUnitPreferences(state.unitPreferences.copy(elevationUnit = ElevationUnit.FEET)) },
                            label = { Text("ft") }
                        )
                    }

                    // Live sample conversion preview
                    val isFahr = state.unitPreferences.temperatureUnit == TemperatureUnit.FAHRENHEIT
                    val isOz = state.unitPreferences.massUnit == MassUnit.OUNCES
                    val sampleTemp = if (isFahr) "199.4 °F" else "93.0 °C"
                    val sampleDose = if (isOz) "0.635 oz" else "18.0 g"
                    val sampleWater = if (isOz) "8.818 oz" else "250.0 g"
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Live Preview: Dose $sampleDose • Water $sampleWater • Temp $sampleTemp",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // ====================================================================
        // S2-081: Graceful Degradation Status & Subsystem Health
        // ====================================================================
        item {
            val badge = state.degradationState.activeBadge
            val badgeText = if (isIndonesian) badge.labelId else badge.labelEn
            Card(
                modifier = Modifier.fillMaxWidth().testTag("settings:degradation_card"),
                colors = CardDefaults.cardColors(
                    containerColor = if (badge.isDegraded) {
                        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    }
                )
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (isIndonesian) "Status Subsistem & Degradasi" else "Subsystem Degradation Status",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (badge.isDegraded) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                        ) {
                            Text(
                                text = badgeText,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Text(
                        if (isIndonesian) {
                            "Aplikasi dirancang agar tetap 100% fungsional pada mode manual saat model AI, OCR, atau timbangan BLE tidak tersedia."
                        } else {
                            "Core coffee tools remain 100% operational in manual mode even when local LLM, OCR vision, or BLE scale are offline."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            "LLM: ${if (state.loadedModel != null) "✓ Active" else "— Manual"}",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            "OCR: ${if (state.degradationState.ocrStatus == ModuleStatus.AVAILABLE) "✓ Active" else "— Manual"}",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            "BLE: ${if (state.brewSession?.scaleDevice != null) "✓ Connected" else "— Offline"}",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }

        // ====================================================================
        // S2-080: On-Device Activity Diagnostics & Scrubbed Ring Buffer
        // ====================================================================
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("settings:diagnostics_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        if (isIndonesian) "Diagnostik Aktivitas On-Device" else "On-Device Activity Diagnostics",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        if (isIndonesian) {
                            "Buffer melingkar 500 entri dengan pembersihan otomatis PII (IP, token, path). Nol SDK telemetri eksternal."
                        } else {
                            "500-entry ring buffer with automatic PII scrubbing (IPs, paths, tokens). Zero external telemetry SDKs."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { model.refreshDiagnostics() }, modifier = Modifier.testTag("settings:refresh_diag")) {
                            Text(
                                if (isIndonesian) "Segarkan Log (${state.diagnosticLogSnapshot.size})" else "Refresh Logs (${state.diagnosticLogSnapshot.size})"
                            )
                        }
                        Button(onClick = { model.exportDiagnosticLogs(context) }, modifier = Modifier.testTag("settings:export_diag")) {
                            Text(if (isIndonesian) "Ekspor Log (Sharesheet)" else "Export Logs (Sharesheet)")
                        }
                        TextButton(onClick = { model.clearDiagnostics() }) {
                            Text(if (isIndonesian) "Hapus Log" else "Clear Logs")
                        }
                    }

                    if (state.diagnosticLogSnapshot.isNotEmpty()) {
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth().heightIn(max = 160.dp)
                        ) {
                            Column(Modifier.padding(8.dp).verticalScroll(rememberScrollState())) {
                                state.diagnosticLogSnapshot.takeLast(6).reversed().forEach { log ->
                                    Text(
                                        text = "[#${log.sequenceId}] [${log.level}] [${log.tag}] ${log.message}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (log.level == LogLevel.ERROR) {
                                            MaterialTheme.colorScheme.error
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ====================================================================
        // S2-078: Offline Help & Error Recovery Guides
        // ====================================================================
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("settings:help_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        if (isIndonesian) "Panduan Bantuan & Pemulihan Offline" else "Offline Help & Error Recovery",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        if (isIndonesian) {
                            "Panduan pemecahan masalah teknis mandiri saat di kedai kopi tanpa akses internet."
                        } else {
                            "Self-contained troubleshooting guides when brewing in airplane mode."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        OfflineHelpCatalog.topics.forEach { topic ->
                            OutlinedButton(
                                onClick = { activeHelpDialog = topic.id },
                                modifier = Modifier.testTag("help:${topic.id}")
                            ) {
                                Text(if (isIndonesian) topic.titleId else topic.titleEn)
                            }
                        }
                    }
                }
            }
        }

        // ====================================================================
        // S2-077: Cryptographic Full Backup & Restore
        // ====================================================================
        item {
            InfoCard(
                if (isIndonesian) "Cadangan & Pemulihan Penuh (SAF)" else "Complete Cryptographic Backup (SAF)",
                if (isIndonesian) {
                    "Ekspor resep, biji kopi, jurnal, cupping, profil rasa, dan preferensi dengan verifikasi SHA-256. " +
                        "File model GGUF dan izin SAF tersimpan terpisah."
                } else {
                    "Export recipes, beans, journal logs, cuppings, taste profiles, and preferences with SHA-256 integrity. " +
                        "Model weights and directory grants remain user-owned in SAF."
                }
            ) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        exporter.launch("coffee-creator-backup.json")
                    }, enabled = !state.busy, modifier = Modifier.testTag("settings:export")) {
                        Text(if (isIndonesian) "Ekspor cadangan" else "Export backup")
                    }
                    OutlinedButton(onClick = {
                        restorer.launch(arrayOf("application/json", "text/plain"))
                    }, enabled = !state.busy, modifier = Modifier.testTag("settings:restore")) {
                        Text(if (isIndonesian) "Pulihkan cadangan" else "Restore backup")
                    }
                }
            }
        }

        item {
            Select(
                if (isIndonesian) "Tema Tampilan" else "Theme",
                state.theme,
                listOf("system" to "System", "light" to "Light", "dark" to "Dark"),
                model::setTheme,
                "settings:theme"
            )
        }

        item { Field("Maximum output tokens (16–256)", tokens, { tokens = it }, "settings:tokens", true) }
        item { Field("Sampling temperature (0.1–1.2)", temperature, { temperature = it }, "settings:temperature", true) }
        item {
            Button(onClick = {
                model.setGeneration(tokens.toIntOrNull() ?: 0, temperature.replace(',', '.').toFloatOrNull() ?: Float.NaN)
            }, enabled = !state.busy) { Text(if (isIndonesian) "Simpan pengaturan inferensi" else "Save generation settings") }
        }

        item {
            InfoCard(
                if (isIndonesian) "Statistik Database Lokal" else "Local Database Diagnostics",
                listOf(
                    "App 1.8.0 · local records ${if (state.ready) "ready" else "unavailable"}",
                    "${state.recipes.size} recipes · ${state.journalEntries.size} journals · ${state.cuppingSessions.size} cuppings",
                    state.loadedModel ?: "No GGUF loaded",
                    "Delete specific records with confirmation in their respective workspace.",
                    "Exports remain in your directory until you delete them there."
                ).joinToString("\n")
            )
        }

        item {
            OutlinedButton(
                onClick = { model.requestDeletion("all application records", "all") },
                enabled = !state.busy && !state.generating
            ) { Text(if (isIndonesian) "Hapus semua data aplikasi…" else "Delete application records…") }
        }
    }

    // ========================================================================
    // Offline Help Topic Dialog
    // ========================================================================
    activeHelpDialog?.let { topicId ->
        val topic = OfflineHelpCatalog.topics.find { it.id == topicId }
        if (topic != null) {
            AlertDialog(
                onDismissRequest = { activeHelpDialog = null },
                title = { Text(if (isIndonesian) topic.titleId else topic.titleEn) },
                text = {
                    Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = if (isIndonesian) topic.summaryId else topic.summaryEn,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        HorizontalDivider()
                        val steps = if (isIndonesian) topic.recoveryStepsId else topic.recoveryStepsEn
                        steps.forEachIndexed { idx, step ->
                            Text("${idx + 1}. $step", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { activeHelpDialog = null }) {
                        Text(if (isIndonesian) "Tutup" else "Close")
                    }
                }
            )
        }
    }

    // ========================================================================
    // Restore Confirmation Dialog
    // ========================================================================
    restoring?.let { uri ->
        AlertDialog(
            onDismissRequest = { restoring = null },
            title = {
                Text(if (isIndonesian) "Pulihkan cadangan terverifikasi?" else "Restore validated backup?")
            },
            text = {
                Text(
                    if (isIndonesian) {
                        "Seluruh data resep, jurnal, cupping, dan preferensi akan dipulihkan secara atomik. " +
                            "File rusak atau tidak kompatibel akan ditolak tanpa mengubah database yang ada."
                    } else {
                        "Supported, integrity-checked records will replace the backed-up local record groups. " +
                            "Corrupt or unsupported files are rejected before mutation."
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    model.restoreBackup(uri, context.contentResolver)
                    restoring = null
                }) { Text(if (isIndonesian) "Validasi & Pulihkan" else "Validate and restore") }
            },
            dismissButton = {
                TextButton(onClick = { restoring = null }) {
                    Text(if (isIndonesian) "Batal" else "Cancel")
                }
            }
        )
    }
}

@Preview(name = "Manual empty state", showBackground = true)
@Composable
private fun EmptyStatePreview() {
    CoffeeCreatorTheme(dynamicColor = false) { InfoCard("No brews recorded", "Create a recipe; measured TDS stays empty until supplied.") }
}

@Preview(name = "Dark unavailable model", showBackground = true)
@Composable
private fun ModelUnavailablePreview() {
    CoffeeCreatorTheme(darkTheme = true, dynamicColor = false) {
        InfoCard("AI model unavailable", "Manual mode remains available. Import a verified GGUF in Models.")
    }
}
