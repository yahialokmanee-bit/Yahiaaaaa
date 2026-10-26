package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = MathAmber,
    onPrimary = Slate950,
    primaryContainer = Slate800,
    onPrimaryContainer = MathAmber,
    secondary = ParametricPink,
    onSecondary = Slate950,
    secondaryContainer = ParametricPinkDark,
    onSecondaryContainer = ParametricPink,
    tertiary = ReferenceBlue,
    background = Slate950,
    onBackground = Slate100,
    surface = Slate900,
    onSurface = Slate100,
    surfaceVariant = Slate800,
    onSurfaceVariant = Slate200,
    outline = Slate700
)

private val LightColorScheme = darkColorScheme( // Graph workstation defaults to sleek dark
    primary = MathAmber,
    onPrimary = Slate950,
    secondary = ParametricPink,
    tertiary = ReferenceBlue,
    background = Slate950,
    surface = Slate900,
    surfaceVariant = Slate800,
    outline = Slate700
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
