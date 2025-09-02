package com.dpm.sixpack.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Immutable
data class SixpackShapes(
    val none: Shape,
    val extraSmall: Shape,
    val small: Shape,
    val medium: Shape,
    val large: Shape,
    val extraLarge: Shape,
    val full: Shape
)

private val SixpackShapesValue = SixpackShapes(
    none = RoundedCornerShape(0.dp),
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp),
    full = RoundedCornerShape(50)
)

val LocalSixpackColors = staticCompositionLocalOf {
    SixPackLightColors
}

val LocalSixpackTypography = staticCompositionLocalOf {
    SixPackTypographyValue
}

val LocalSixpackShapes = staticCompositionLocalOf {
    SixpackShapesValue
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
        LocalSixpackShapes provides SixpackShapesValue,
    ) {
        // BuildConfig.DEBUG
        if (true) DebugColorTheme(content) else content
    }
}

object SixpackTheme {
    val colors: SixPackColors
        @Composable get() = LocalSixpackColors.current

    val typography: SixpackTypography
        @Composable get() = LocalSixpackTypography.current

    val shapes: SixpackShapes
        @Composable get() = LocalSixpackShapes.current
}
