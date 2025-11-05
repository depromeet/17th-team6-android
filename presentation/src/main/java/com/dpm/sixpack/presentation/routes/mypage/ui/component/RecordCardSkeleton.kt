package com.dpm.sixpack.presentation.routes.mypage.ui.component

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun RecordCardSkeleton(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "shimmer",
    )

    val shimmerColor = SixpackTheme.colors.gray200.copy(alpha = shimmerAlpha)

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 1.dp,
                    color = SixpackTheme.colors.gray200,
                    shape = RoundedCornerShape(16.dp),
                )
                .background(SixpackTheme.colors.gray0)
                .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            // Time row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier =
                        Modifier
                            .width(60.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(shimmerColor),
                )
            }

            // Distance
            Box(
                modifier =
                    Modifier
                        .width(80.dp)
                        .height(28.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(shimmerColor),
            )

            // Stats row
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                repeat(3) {
                    Box(
                        modifier =
                            Modifier
                                .width(70.dp)
                                .height(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(shimmerColor),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun RecordCardSkeletonPreview() {
    DoRunPreviewWrapper {
        RecordCardSkeleton()
    }
}
