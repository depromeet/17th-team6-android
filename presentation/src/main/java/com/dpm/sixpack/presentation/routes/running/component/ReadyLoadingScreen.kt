package com.dpm.sixpack.presentation.routes.running.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dpm.sixpack.presentation.routes.running.contract.RunningSessionState

@Composable
fun ReadyLoadingScreen(
    readyState: RunningSessionState.MainReady,
    modifier: Modifier = Modifier,
) {
    var progress by remember { mutableFloatStateOf(0f) }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "ProgressAnimation",
    )

    LaunchedEffect(Unit) {
        progress = 1f
    }

    Column(
        modifier
            .fillMaxSize()
            .background(
                color = Color.Black.copy(alpha = 0.3f),
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.padding(top = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp),
        ) {
            // FIXME SK: Adjust Typo
            Text(
                text = "잠시 후 러닝 시작",
                color = Color.White,
            )

            Text(
                text =
                    "웜업 운동을 종료하고\n" +
                        "본 러닝이 시작됩니다.",
                color = Color.White,
            )

            if (readyState.countdown <= 3 && readyState.countdown > 0) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        progress = {
                            animatedProgress
                        },
                        modifier =
                            Modifier
                                .size(350.dp),
                        color = Color.White,
                        trackColor = Color.DarkGray,
                        strokeWidth = ProgressIndicatorDefaults.CircularStrokeWidth,
                        strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                    )

                    Text(
                        text = readyState.countdown.toString(),
                        fontSize = 16.sp,
                        color = Color.White,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewReadyLoadingScreen() {
    ReadyLoadingScreen(
        readyState = RunningSessionState.MainReady(countdown = 1),
    )
}
