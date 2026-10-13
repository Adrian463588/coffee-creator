package com.coffeecreator.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = CoffeePrimary,
    onPrimary = CoffeeOnPrimary,
    primaryContainer = CoffeePrimaryContainer,
    onPrimaryContainer = CoffeeOnPrimaryContainer,
    secondary = CoffeeSecondary,
    onSecondary = CoffeeOnSecondary,
    secondaryContainer = CoffeeSecondaryContainer,
    onSecondaryContainer = CoffeeOnSecondaryContainer,
    tertiary = CoffeeAccent,
    onTertiary = CoffeeOnAccent,
    background = CoffeeBackground,
    onBackground = CoffeeOnBackground,
    surface = CoffeeSurface,
    onSurface = CoffeeOnSurface,
    surfaceVariant = CoffeeSurfaceVariant,
    onSurfaceVariant = CoffeeOnSurfaceVariant,
    error = CoffeeError,
    onError = CoffeeOnError
)

private val DarkColorScheme = darkColorScheme(
    primary = CoffeePrimaryContainer,
    onPrimary = CoffeeOnPrimaryContainer,
    secondary = CoffeeSecondary,
    onSecondary = CoffeeOnSecondary,
    tertiary = CoffeeAccent,
    onTertiary = CoffeeOnAccent,
    background = Color(0xFF1B1614),
    onBackground = Color(0xFFEDE0DC),
    surface = Color(0xFF221C1A),
    onSurface = Color(0xFFEDE0DC),
    surfaceVariant = Color(0xFF382F2C),
    onSurfaceVariant = Color(0xFFD4C4BF)
)

@Composable
fun CoffeeCreatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
