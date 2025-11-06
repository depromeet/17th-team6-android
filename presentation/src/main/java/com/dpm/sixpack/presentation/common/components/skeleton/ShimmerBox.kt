package com.dpm.sixpack.presentation.common.components.skeleton

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixpackTheme

/**
 * Reusable shimmer loading box component with animated placeholder effect.
 *
 * @param modifier Modifier to be applied to the box
 * @param width Width of the shimmer box
 * @param height Height of the shimmer box
 * @param shape Shape of the shimmer box (default: RoundedCornerShape(4.dp))
 * @param durationMillis Animation duration in milliseconds (default: 1000)
 * @param initialAlpha Initial alpha value for shimmer animation (default: 0.3f)
 * @param targetAlpha Target alpha value for shimmer animation (default: 0.6f)
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    width: Dp = 100.dp,
    height: Dp = 16.dp,
    shape: Shape = RoundedCornerShape(4.dp),
    durationMillis: Int = 1000,
    initialAlpha: Float = 0.3f,
    targetAlpha: Float = 0.6f,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = initialAlpha,
        targetValue = targetAlpha,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "shimmer",
    )

    val shimmerColor = SixpackTheme.colors.gray200.copy(alpha = shimmerAlpha)

    Box(
        modifier =
            modifier
                .width(width)
                .height(height)
                .clip(shape)
                .background(shimmerColor),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ShimmerBoxPreview() {
    DoRunPreviewWrapper {
        ShimmerBox()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ShimmerBoxCustomPreview() {
    DoRunPreviewWrapper {
        ShimmerBox(
            width = 200.dp,
            height = 40.dp,
            shape = RoundedCornerShape(8.dp),
        )
    }
}