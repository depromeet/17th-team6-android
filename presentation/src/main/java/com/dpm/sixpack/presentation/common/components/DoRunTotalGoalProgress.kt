package com.dpm.sixpack.presentation.common.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun DoRunTotalGoalProgress(
    modifier: Modifier = Modifier,
    current: Int,
    total: Int,
    progress: Float
) {


    val progressColor = SixpackTheme.colors.blue600
    val trackColor = SixpackTheme.colors.gray100
    val textDimColor = SixpackTheme.colors.gray400

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "goalProgressAnim"
    )

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.common_session_count).format(current),
                style = SixpackTheme.typography.b1Bold,
                color = progressColor
            )
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = stringResource(R.string.common_session_total_count).format(total),
                style = SixpackTheme.typography.c1Regular,
                color = textDimColor
            )
        }
        Canvas(
            modifier = Modifier
                .padding(top = 6.dp)
                .fillMaxWidth()
                .height(10.dp)
                .semantics {
                    contentDescription = "진행도 ${current}회차, 총 ${total}회"
                }
        ) {
            val radius = size.height / 2f

            // Track
            drawRoundRect(
                color = trackColor,
                size = Size(size.width, size.height),
                cornerRadius = CornerRadius(radius, radius)
            )

            // Progress
            drawRoundRect(
                color = progressColor,
                size = Size(size.width * animatedProgress, size.height),
                cornerRadius = CornerRadius(radius, radius)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun DoRunTotalGoalProgressPreview() {
    val current = 4
    val total = 20
    DoRunPreviewWrapper {
        DoRunTotalGoalProgress(
            modifier = Modifier
                .fillMaxWidth(),
            current = current,
            total = total,
            progress = current / total.toFloat()
        )
    }
}
