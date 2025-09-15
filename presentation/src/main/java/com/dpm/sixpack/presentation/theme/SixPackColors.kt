package com.dpm.sixpack.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class SixPackColors(
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val onSecondary: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val error: Color,
    val onError: Color,
    val success: Color,
    val onSuccess: Color,
    val warning: Color,
    val onWarning: Color,
    val outline: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
)

val SixPackLightColors =
    SixPackColors(
        primary = Color(0xFF3E4FFF),
        onPrimary = Color.White,
        secondary = Color(0xFF625B71),
        onSecondary = Color.White,
        background = Color(0xFFFFFBFE),
        onBackground = Color(0xFF1C1B1F),
        surface = Color(0xFFFFFBFE),
        onSurface = Color(0xFF1C1B1F),
        error = Color(0xFFBA1A1A),
        onError = Color.White,
        success = Color(0xFF388E3C),
        onSuccess = Color.White,
        warning = Color(0xFFFF9800),
        onWarning = Color.Black,
        outline = Color(0xFF79747E),
        surfaceVariant = Color(0xFFE7E0EC),
        onSurfaceVariant = Color(0xFF49454F),
    )

val SixPackDarkColors =
    SixPackColors(
        primary = Color(0xFF3E4FFF),
        onPrimary = Color(0xFF381E72),
        secondary = Color(0xFFCCC2DC),
        onSecondary = Color(0xFF332D41),
        background = Color(0xFF1C1B1F),
        onBackground = Color(0xFFE6E1E5),
        surface = Color(0xFF1C1B1F),
        onSurface = Color(0xFFE6E1E5),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        success = Color(0xFF81C784),
        onSuccess = Color(0xFF003300),
        warning = Color(0xFFFFCC02),
        onWarning = Color(0xFF332D00),
        outline = Color(0xFF938F99),
        surfaceVariant = Color(0xFF49454F),
        onSurfaceVariant = Color(0xFFCAC4D0),
    )
