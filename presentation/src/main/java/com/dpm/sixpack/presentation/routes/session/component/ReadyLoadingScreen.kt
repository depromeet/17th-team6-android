package com.dpm.sixpack.presentation.routes.session.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dpm.sixpack.presentation.routes.session.contract.RunningSessionState

@Composable
fun ReadyLoadingScreen(
    warmUpReadyState: RunningSessionState.WarmUpReady,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .fillMaxSize()
            .background(
                color = Color.Black.copy(alpha = 0.3f),
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.padding(top = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp),
        ) {
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

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    modifier =
                        Modifier
                            .size(350.dp),
                )
                Text(
                    text = warmUpReadyState.countdown.toString(),
                    fontSize = 16.sp,
                    color = Color.White,
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewReadyLoadingScreen() {
    ReadyLoadingScreen(
        warmUpReadyState = RunningSessionState.WarmUpReady(),
    )
}
