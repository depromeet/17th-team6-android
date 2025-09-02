package com.dpm.sixpack.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 디버깅용 (실험)
private val DebugColor = Color.Magenta

private val DebugColorScheme = lightColorScheme(
    primary = DebugColor,
    onPrimary = DebugColor,
    primaryContainer = DebugColor,
    onPrimaryContainer = DebugColor,
    inversePrimary = DebugColor,
    secondary = DebugColor,
    onSecondary = DebugColor,
    secondaryContainer = DebugColor,
    onSecondaryContainer = DebugColor,
    tertiary = DebugColor,
    onTertiary = DebugColor,
    tertiaryContainer = DebugColor,
    onTertiaryContainer = DebugColor,
    background = DebugColor,
    onBackground = DebugColor,
    surface = DebugColor,
    onSurface = DebugColor,
    surfaceVariant = DebugColor,
    onSurfaceVariant = DebugColor,
    surfaceTint = DebugColor,
    inverseSurface = DebugColor,
    inverseOnSurface = DebugColor,
    error = DebugColor,
    onError = DebugColor,
    errorContainer = DebugColor,
    onErrorContainer = DebugColor,
    outline = DebugColor,
    outlineVariant = DebugColor,
    scrim = DebugColor,
    surfaceBright = DebugColor,
    surfaceContainer = DebugColor,
    surfaceContainerHigh = DebugColor,
    surfaceContainerHighest = DebugColor,
    surfaceContainerLow = DebugColor,
    surfaceContainerLowest = DebugColor,
    surfaceDim = DebugColor
)

@Composable
fun DebugColorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DebugColorScheme,
        content = content
    )
}
