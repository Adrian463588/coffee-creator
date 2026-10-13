package com.coffeecreator.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val isPrimaryNav: Boolean = true
) {
    HOME("home", "Home", Icons.Default.Home, true),
    RECIPE_BUILDER("formulate", "Formulate", Icons.Default.Edit, true),
    COFFEE_LIBRARY("catalog", "Library", Icons.Default.LocalCafe, true),
    SENSORY("sensory", "Sensory", Icons.Default.Psychology, true),
    AI_CHAT("chat", "AI Chat", Icons.Default.Chat, true),
    KNOWLEDGE("knowledge", "Knowledge", Icons.Default.Book, false),
    MODELS("models", "Models", Icons.Default.Download, false),
    JOURNAL("journal", "Journal", Icons.Default.AutoAwesome, false),
    SETTINGS("settings", "Settings", Icons.Default.Settings, false)
}
