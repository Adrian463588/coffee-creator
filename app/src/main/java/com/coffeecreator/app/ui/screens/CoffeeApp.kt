@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.coffeecreator.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
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
import com.coffeecreator.app.core.filesystem.BuiltInAssets
import com.coffeecreator.app.core.model.CoffeeBean
import com.coffeecreator.app.core.model.Ingredient
import com.coffeecreator.app.core.model.ModelStatus
import com.coffeecreator.app.core.data.CuratedCoffeeCatalog
import com.coffeecreator.app.core.model.Recipe
import com.coffeecreator.app.ui.theme.CoffeeCreatorTheme
import com.coffeecreator.app.ui.viewmodel.CoffeeUiState
import com.coffeecreator.app.ui.viewmodel.CoffeeViewModel

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
                    if (state.error != null || state.notice != null) {
                        Card(
                            Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (state.error !=
                                    null
                                ) {
                                    MaterialTheme.colorScheme.errorContainer
                                } else {
                                    MaterialTheme.colorScheme.secondaryContainer
                                }
                            )
                        ) {
                            Row(Modifier.padding(12.dp)) {
                                Text(
                                    state.error ?: state.notice.orEmpty(),
                                    Modifier.weight(1f).semantics {
                                        liveRegion =
                                            LiveRegionMode.Polite
                                    }
                                )
                                TextButton(onClick = model::clearNotice) { Text("Dismiss") }
                            }
                        }
                    }
                    if (route == "Formulate" && state.canUndoDraft) {
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
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, Modifier.semantics { heading() }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(detail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            content()
        }
    }
}

@Composable
private fun Field(label: String, value: String, onChange: (String) -> Unit, tag: String, numeric: Boolean = false) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().testTag(tag),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = if (numeric) KeyboardType.Decimal else KeyboardType.Text)
    )
}

@Composable
private fun Select(label: String, selected: String, choices: List<Pair<String, String>>, onSelect: (String) -> Unit, tag: String) {
    var open by rememberSaveable { mutableStateOf(false) }
    Column {
        OutlinedButton(onClick = { open = true }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag(tag)) {
            Text("$label: ${choices.find { it.first == selected }?.second ?: selected.ifBlank { "Choose" }}")
        }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            choices.forEach { (id, text) ->
                DropdownMenuItem(text = { Text(text) }, onClick = {
                    onSelect(id)
                    open = false
                })
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
                                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                                            model.saveBean(id, mapOf("name" to name, "variety" to "Custom", "provenance" to "Custom user lot"))
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
                                            listOf("V60", "AeroPress", "Espresso", "French Press", "Chemex", "Immersion", "Kalita Wave", "Moka Pot", "Cold Brew", "Syphon", "Clever Dripper").map { it to it },
                                            { model.updateField("method", it) },
                                            "form:method"
                                        )
                                        Field(
                                            "Brew method (or custom apparatus)",
                                            fields.getValue("method"),
                                            { model.updateField("method", it) },
                                            "form:custom_method"
                                        )
                                        Field("Dose (g)", fields.getValue("dose"), { model.updateField("dose", it) }, "form:dose", true)
                                        Field("Brew water (g)", fields.getValue("water"), {
                                            model.updateField("water", it)
                                        }, "form:water", true)
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
                        }
                    }
                }
            }
            Surface(tonalElevation = 3.dp, shadowElevation = 2.dp) {
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
                        Text(ratioLabel, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                    Button(
                        onClick = model::saveRecipe,
                        enabled = state.ready && !state.busy,
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("form:save")
                    ) {
                        Text(if (state.draft.id == null) "Save recipe" else "Save new revision", maxLines = 2)
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
    var query by rememberSaveable { mutableStateOf("") }
    var tab by rememberSaveable { mutableStateOf("Coffee") }
    var formKind by rememberSaveable { mutableStateOf<String?>(null) }
    var editingId by rememberSaveable { mutableStateOf<String?>(null) }
    var form by rememberSaveable { mutableStateOf(emptyMap<String, String>()) }
    var process by rememberSaveable { mutableStateOf("All") }
    Page("Library") {
        item {
            Heading(
                "Coffee & ingredients",
                "Reference varieties describe coffee genetics. Add your supplier lots separately; " +
                    "origin, processing, roast and measurements stay Unknown until supplied."
            )
        }
        item { Field("Search origin, name or process", query, { query = it }, "library:query") }
        item {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Coffee", "Ingredients").forEach { label ->
                    FilterChip(
                        tab == label,
                        { tab = label },
                        label = { Text(label) },
                        modifier = Modifier.heightIn(min = 48.dp)
                    )
                }
            }
        }
        item {
            Button(onClick = {
                formKind = tab
                editingId = null
                form = emptyMap()
            }, modifier = Modifier.testTag("library:add")) { Text("Add ${if (tab == "Coffee") "coffee" else "ingredient"}") }
        }
        if (tab == "Coffee") {
            item {
                Select("Process filter", process, (listOf("All") + state.beans.map { it.process }.distinct()).map { it to it }, {
                    process =
                        it
                }, "library:process")
            }
            val hasCuratedBeans = state.beans.any { b -> CuratedCoffeeCatalog.beans.any { it.id == b.id } }
            if (!hasCuratedBeans) {
                item {
                    InfoCard(
                        "Specialty Single-Origin Beans",
                        "Load 8 authentic specialty beans with verified agricultural data (Yirgacheffe, Huila Pink Bourbon, Panama Geisha, etc.)."
                    ) {
                        Button(
                            onClick = model::importCuratedCatalog,
                            enabled = !state.busy,
                            modifier = Modifier.testTag("library:import_catalog")
                        ) {
                            Text("Load Curated Specialty Beans")
                        }
                    }
                }
            }
            items(
                state.beans.filter { bean ->
                    (process == "All" || bean.process == process) &&
                        listOf(bean.name, bean.variety, bean.country, bean.region, bean.process).any { it.contains(query, true) }
                },
                key = { it.id }
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
        } else {
            if (state.ingredients.isEmpty()) {
                item {
                    InfoCard(
                        "No ingredient records",
                        "Add food-grade status, allergens, density and supplier dosage limits from a real supplier."
                    )
                }
            }
            items(state.ingredients.filter { it.name.contains(query, true) }, key = { it.id }) { ingredient ->
                InfoCard(
                    ingredient.name,
                    listOf(
                        "Food grade: ${ingredient.foodGrade?.toString() ?: "Unknown"}",
                        "Density: ${ingredient.densityGramsPerMl ?: "Unknown"} g/mL",
                        "Supplier maximum: ${ingredient.supplierMaxPercent ?: "Unknown"}% w/w",
                        "Allergens: ${ingredient.allergens.joinToString().ifBlank { "not declared; verify supplier" }}",
                        "Source: ${ingredient.provenance}"
                    ).joinToString("\n")
                ) {
                    TextButton(onClick = {
                        formKind = "Ingredients"
                        editingId = ingredient.id
                        form = ingredientForm(ingredient)
                    }) { Text("Edit ingredient") }
                }
            }
        }
    }
    formKind?.let { kind ->
        val keys = if (kind ==
            "Coffee"
        ) {
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
        AlertDialog(onDismissRequest = {
            formKind = null
        }, title = { Text("${if (editingId == null) "Add" else "Edit"} ${if (kind == "Coffee") "coffee" else "ingredient"}") }, text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                keys.forEach { (key, label) -> Field(label, form[key].orEmpty(), { form = form + (key to it) }, "catalog:$key") }
                if (kind !=
                    "Coffee"
                ) {
                    Select(
                        "Food-grade evidence",
                        form["foodGrade"].orEmpty(),
                        listOf("Unknown", "Confirmed food grade", "Not food grade").map {
                            it to
                                it
                        },
                        { form = form + ("foodGrade" to it) },
                        "catalog:foodGrade"
                    )
                }
                Text("Leave unknown facts blank. Unknown food-grade ingredients cannot be used in a drink.")
            }
        }, confirmButton = {
            TextButton(onClick = {
                val values = form + ("name" to form["name"].orEmpty())
                if (kind == "Coffee") model.saveBean(editingId, values) else model.saveIngredient(editingId, values)
                formKind = null
            }, enabled = form["name"].orEmpty().isNotBlank() && !state.busy, modifier = Modifier.testTag("catalog:save")) {
                Text("Save entry")
            }
        }, dismissButton = { TextButton(onClick = { formKind = null }) { Text("Cancel") } })
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

@Composable
private fun Journal(state: CoffeeUiState, model: CoffeeViewModel, edit: (Recipe) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var comparing by rememberSaveable { mutableStateOf<String?>(null) }
    Page("Journal") {
        item { Heading("Brew journal", "Saved revisions are immutable. Repeat a brew, make a copy, or compare observations.") }
        item { Field("Search recipes", query, { query = it }, "journal:query") }
        val hasCuratedRecipes = state.recipes.any { r -> CuratedCoffeeCatalog.recipes.any { it.id == r.id } }
        if (!hasCuratedRecipes) {
            item {
                InfoCard(
                    "Specialty Preset Library",
                    "Load 10 world-standard recipes (Hoffmann V60, Kasuya 4:6, AeroPress Champion, Chemex, etc.) and real sensory logs."
                ) {
                    Button(
                        onClick = model::importCuratedCatalog,
                        enabled = !state.busy,
                        modifier = Modifier.testTag("journal:import_catalog")
                    ) {
                        Text("Load 10 Curated Recipes & Journal")
                    }
                }
            }
        }
        if (state.recipes.isEmpty()) item { InfoCard("No saved brews", "Create and save your first manual recipe in Brew.") }
        items(state.recipes.filter { it.name.contains(query, true) }, key = { it.id }) { recipe ->
            InfoCard(
                "${recipe.name} · v${recipe.version}",
                listOf(
                    "${recipe.brewMethod} · ${recipe.doseGrams} g coffee / ${recipe.waterGrams} g water",
                    "Measured TDS: ${recipe.measuredTdsPercent?.let { "$it%" } ?: "not measured"}",
                    "Measured beverage: ${recipe.beverageMassGrams?.let { "$it g" } ?: "not measured"}",
                    "${state.observations.count { it.recipeId == recipe.id }} human observations"
                ).joinToString("\n")
            ) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        edit(recipe)
                    }, modifier = Modifier.testTag("journal:edit:${recipe.id}")) { Text("Repeat / edit") }
                    TextButton(onClick = { model.copyRecipe(recipe) }) { Text("Copy") }
                    TextButton(onClick = { comparing = recipe.revisionGroupId }) { Text("Compare history") }
                    TextButton(onClick = { model.requestDeletion("recipe", recipe.id) }) { Text("Delete") }
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
}

@Composable
private fun Sensory(state: CoffeeUiState, model: CoffeeViewModel) {
    var scores by rememberSaveable { mutableStateOf(listOf(5, 5, 5, 5, 5, 5, 5, 5)) }
    var descriptors by rememberSaveable { mutableStateOf("") }
    var assessor by rememberSaveable { mutableStateOf("") }
    var panel by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }
    Page("Sensory") {
        item {
            Heading(
                "Record what you taste",
                "Human observations and uncalibrated estimates have separate labels. Scores below are " +
                    "editable inputs, saved only when you record."
            )
        }
        item {
            Select("Recipe revision", state.selectedRecipeId.orEmpty(), state.recipes.map { it.id to "${it.name} v${it.version}" }, { id ->
                state.recipes.find {
                    it.id ==
                        id
                }?.let(model::selectSensoryRecipe)
            }, "sensory:recipe")
        }
        if (state.recipes.isEmpty()) item { InfoCard("Save a brew first", "Observations are linked to a specific saved recipe revision.") }
        listOf("Aroma", "Acidity", "Sweetness", "Bitterness", "Body", "Clarity", "Finish", "Balance").forEachIndexed { index, label ->
            item {
                Column {
                    Text("Observed $label: ${scores[index]} / 10")
                    Slider(
                        scores[index].toFloat(),
                        { value -> scores = scores.toMutableList().also { it[index] = value.toInt() } },
                        valueRange = 1f..10f,
                        steps = 8,
                        modifier = Modifier.semantics {
                            stateDescription =
                                "$label intensity ${scores[index]} of 10"
                        }
                    )
                }
            }
        }
        item { Field("Descriptors, optional intensity: citrus:7, floral:5", descriptors, { descriptors = it }, "sensory:descriptors") }
        item {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("citrus", "floral", "berry", "chocolate", "nutty", "roasted").forEach { descriptor ->
                    OutlinedButton(onClick = {
                        val current = descriptors.split(',').map(String::trim).filter(String::isNotBlank)
                        descriptors = (current + descriptor).distinct().joinToString(", ")
                    }) { Text(descriptor) }
                }
            }
        }
        item { Field("Assessor", assessor, { assessor = it }, "sensory:assessor") }
        item { Field("Panel / session, optional", panel, { panel = it }, "sensory:panel") }
        item { Field("Observation notes", notes, { notes = it }, "sensory:notes") }
        item {
            Button(
                onClick = { model.recordSensory(scores, descriptors, notes, assessor, panel) },
                enabled =
                state.selectedRecipeId != null && !state.busy,
                modifier = Modifier.fillMaxWidth().testTag("sensory:save")
            ) {
                Text("Record observed profile")
            }
        }
        if (state.selectedRecipeId != null) {
            item {
                SensoryRadar(
                    state.observations.lastOrNull { it.recipeId == state.selectedRecipeId },
                    state.estimates.firstOrNull { it.recipeId == state.selectedRecipeId }?.profile
                )
            }
        }
        state.estimates.firstOrNull { it.recipeId == state.selectedRecipeId }?.profile?.let { estimate ->
            item {
                InfoCard(
                    "ESTIMATED direction · ${estimate.method}",
                    listOf(
                        estimate.basis,
                        estimate.suggestedDescriptors.joinToString(),
                        "Confidence: uncalibrated / unknown",
                        estimate.uncertaintyDisclaimer
                    ).joinToString("\n")
                )
            }
        }
        items(state.observations.filter { it.recipeId == state.selectedRecipeId }, key = { it.id }) { observation ->
            InfoCard(
                "OBSERVED · ${observation.assessor.ifBlank {
                    "Unspecified assessor"
                }}",
                listOf(
                    observation.tastingDescriptors.joinToString(),
                    observation.notes,
                    "Aroma ${observation.aroma}, acidity ${observation.acidity}, sweetness ${observation.sweetness}; " +
                        "body ${observation.body}"
                ).joinToString("\n")
            )
        }
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
    Page("AI Chat") {
        item {
            Heading(
                "Local coffee assistant",
                "${state.loadedModel ?: "No model loaded"}. Generated text may be wrong. " +
                    "Measurements and exact Kotlin cards remain authoritative."
            )
        }
        item {
            Select("Persona", state.selectedPersona?.id.orEmpty(), state.personas.map { it.id to it.title }, { id ->
                state.personas.find {
                    it.id ==
                        id
                }?.let(model::selectPersona)
            }, "chat:persona")
        }
        item {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Ask from my sources", "Help plan a brew", "Explain my observations").forEach { preset ->
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
            OutlinedTextField(prompt, {
                prompt = it
            }, label = {
                Text("Message or typed tool JSON")
            }, modifier = Modifier.fillMaxWidth().testTag("chat:input"), minLines = 2, maxLines = 5)
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
}

@Composable
private fun Settings(state: CoffeeUiState, model: CoffeeViewModel) {
    val context = LocalContext.current
    var restoring by rememberSaveable { mutableStateOf<Uri?>(null) }
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
                "Privacy & recovery",
                "Your records stay local. SAF exports contain personal data; keep them private. No trackers or cloud inference."
            )
        }
        item {
            Select(
                "Theme",
                state.theme,
                listOf("system" to "System", "light" to "Light", "dark" to "Dark"),
                model::setTheme,
                "settings:theme"
            )
        }
        item {
            InfoCard(
                "Complete backup",
                "Export recipes, catalog, observations, chat and source metadata with schema and SHA-256 " +
                    "integrity. Model weights and grants are separate; uninstall revokes permissions."
            ) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        exporter.launch("coffee-creator-backup.json")
                    }, enabled = !state.busy, modifier = Modifier.testTag("settings:export")) { Text("Export backup") }
                    OutlinedButton(onClick = {
                        restorer.launch(arrayOf("application/json", "text/plain"))
                    }, enabled = !state.busy, modifier = Modifier.testTag("settings:restore")) { Text("Restore backup") }
                }
            }
        }
        item { Field("Maximum output tokens (16–256)", tokens, { tokens = it }, "settings:tokens", true) }
        item { Field("Sampling temperature (0.1–1.2)", temperature, { temperature = it }, "settings:temperature", true) }
        item {
            Button(onClick = {
                model.setGeneration(tokens.toIntOrNull() ?: 0, temperature.replace(',', '.').toFloatOrNull() ?: Float.NaN)
            }, enabled = !state.busy) { Text("Save generation settings") }
        }
        item {
            Select("Editable persona", state.selectedPersona?.id.orEmpty(), state.personas.map { it.id to it.title }, { id ->
                state.personas.find {
                    it.id ==
                        id
                }?.let(model::selectPersona)
            }, "settings:persona")
        }
        item {
            OutlinedTextField(personaText, {
                personaText = it
            }, label = { Text("Persona preference") }, modifier = Modifier.fillMaxWidth(), minLines = 3, maxLines = 8)
        }
        item {
            Button(onClick = {
                state.selectedPersona?.let { model.updatePersona(it, personaText) }
            }, enabled = !state.busy) { Text("Save persona") }
        }
        item {
            InfoCard(
                "Local diagnostics",
                listOf(
                    "App 1.1.0 · local records ${if (state.ready) "ready" else "unavailable"}",
                    "${state.recipes.size} recipes · ${state.observations.size} observations · ${state.documents.size} sources",
                    state.loadedModel ?: "No GGUF loaded",
                    "Delete specific records with confirmation in their library/journal.",
                    "Exports remain in your directory until you delete them there."
                ).joinToString("\n")
            )
        }
        item {
            OutlinedButton(
                onClick = { model.requestDeletion("all application records", "all") },
                enabled =
                !state.busy && !state.generating
            ) { Text("Delete application records…") }
        }
    }
    restoring?.let { uri ->
        AlertDialog(
            onDismissRequest = {
                restoring = null
            },
            title = {
                Text("Restore validated backup?")
            },
            text = {
                Text(
                    "Supported, integrity-checked records will replace the backed-up local record groups. " +
                        "Corrupt or unsupported files are rejected before mutation. Export current records first if " +
                        "needed."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    model.restoreBackup(uri, context.contentResolver)
                    restoring = null
                }) { Text("Validate and restore") }
            },
            dismissButton = {
                TextButton(onClick = {
                    restoring =
                        null
                }) { Text("Cancel") }
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
