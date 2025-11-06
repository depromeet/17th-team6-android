package com.dpm.sixpack.presentation.routes.mypage.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.components.skeleton.ShimmerBox
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun RecordCardSkeleton(modifier: Modifier = Modifier) {
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
                ShimmerBox(
                    width = 60.dp,
                    height = 16.dp,
                )
            }

            // Distance
            ShimmerBox(
                width = 80.dp,
                height = 28.dp,
            )

            // Stats row
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                repeat(3) {
                    ShimmerBox(
                        width = 70.dp,
                        height = 16.dp,
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
