package com.dpm.sixpack.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.dpm.sixpack.core.BuildConfig


val LocalSixpackColors = staticCompositionLocalOf {
    SixPackLightColors
}

val LocalSixpackTypography = staticCompositionLocalOf {
    SixPackTypographyValue
}

val LocalSixpackShapes = staticCompositionLocalOf {
    SixPackShapesValue
}


@Composable
fun SixpackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) SixPackDarkColors else SixPackLightColors

    CompositionLocalProvider(
        LocalSixpackColors provides colors,
        LocalSixpackTypography provides SixPackTypographyValue,
        LocalSixpackShapes provides SixPackShapesValue,
    ) {
        // BuildConfig.DEBUG
        if (BuildConfig.DEBUG) DebugColorTheme(content) else content
    }
}

object SixpackTheme {
    val colors: SixPackColors
        @Composable get() = LocalSixpackColors.current

    val typography: SixpackTypography
        @Composable get() = LocalSixpackTypography.current

    val shapes: SixPackShapes
        @Composable get() = LocalSixpackShapes.current
}
