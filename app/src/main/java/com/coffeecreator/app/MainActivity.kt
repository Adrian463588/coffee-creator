package com.coffeecreator.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.coffeecreator.app.ui.navigation.Screen
import com.coffeecreator.app.ui.screens.*
import com.coffeecreator.app.ui.theme.CoffeeCreatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

            CoffeeCreatorTheme(darkTheme = isDarkTheme) {
                var currentScreen by remember { mutableStateOf(Screen.HOME) }
                var showMenu by remember { mutableStateOf(false) }

                Scaffold(
                    topBar = {
                        @OptIn(ExperimentalMaterial3Api::class)
                        TopAppBar(
                            title = {
                                Column {
                                    Text(
                                        text = currentScreen.title,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Text(
                                        text = "100% Offline • Local Workstation",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = { showMenu = true }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "More destinations")
                                }
                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Knowledge Library") },
                                        leadingIcon = { Icon(Icons.Default.Book, contentDescription = null) },
                                        onClick = {
                                            currentScreen = Screen.KNOWLEDGE
                                            showMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Model Manager (SAF)") },
                                        leadingIcon = { Icon(Icons.Default.Download, contentDescription = null) },
                                        onClick = {
                                            currentScreen = Screen.MODELS
                                            showMenu = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Brew Journal") },
                                        leadingIcon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) },
                                        onClick = {
                                            currentScreen = Screen.JOURNAL
                                            showMenu = false
                                        }
                                    )
                                    Divider()
                                    DropdownMenuItem(
                                        text = { Text("Settings & Privacy") },
                                        leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                        onClick = {
                                            currentScreen = Screen.SETTINGS
                                            showMenu = false
                                        }
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            val primaryScreens = listOf(
                                Screen.HOME,
                                Screen.RECIPE_BUILDER,
                                Screen.COFFEE_LIBRARY,
                                Screen.SENSORY,
                                Screen.AI_CHAT
                            )
                            primaryScreens.forEach { screen ->
                                NavigationBarItem(
                                    icon = { Icon(screen.icon, contentDescription = screen.title) },
                                    label = { Text(screen.title) },
                                    selected = currentScreen == screen,
                                    onClick = { currentScreen = screen }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            Screen.HOME -> HomeScreen(onNavigate = { currentScreen = it })
                            Screen.RECIPE_BUILDER -> RecipeBuilderScreen(onNavigateToSensory = { currentScreen = Screen.SENSORY })
                            Screen.COFFEE_LIBRARY -> CoffeeLibraryScreen()
                            Screen.SENSORY -> SensoryScreen()
                            Screen.AI_CHAT -> AiChatScreen()
                            Screen.KNOWLEDGE -> KnowledgeLibraryScreen()
                            Screen.MODELS -> ModelManagerScreen()
                            Screen.JOURNAL -> BrewJournalScreen(onSelectRecipeForDial = { currentScreen = Screen.RECIPE_BUILDER })
                            Screen.SETTINGS -> SettingsScreen(
                                isDarkTheme = isDarkTheme,
                                onToggleDarkTheme = { isDarkTheme = it }
                            )
                        }
                    }
                }
            }
        }
    }
}
