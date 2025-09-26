package com.dpm.sixpack.presentation.routes.session.component.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningGoalUiState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun IntervalRoutineChart(uiState: RunningGoalUiState) {
    // TODO SK: 실제 계산 로직으로 고치기
    val minWeight = 0.15f
    val mainWeight = 0.7f

    val totalDuration =
        (uiState.warmUpMinutes + uiState.recommendedTimeMinutes + uiState.coolDownMinutes).toFloat()

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(60.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            // 웜업
            Box(
                modifier =
                    Modifier
                        .weight(minWeight)
                        .fillMaxHeight(0.5f)
                        .background(Color(0xFFB5B9FF)),
            )
            // 본러닝
            Box(
                modifier =
                    Modifier
                        .weight(mainWeight)
                        .fillMaxHeight(1f)
                        .background(Color(0xFF8B91FF)),
            )
            // 쿨다운
            Box(
                modifier =
                    Modifier
                        .weight(minWeight)
                        .fillMaxHeight(0.5f)
                        .background(Color(0xFFB5B9FF)),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Text Row
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.weight(minWeight),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "${uiState.warmUpMinutes}분",
                    textAlign = TextAlign.Center,
                    style = SixpackTheme.typography.b2Medium,
                    color = Color(0xFF4C46FC),
                )
                Text(
                    text = "웜업",
                    textAlign = TextAlign.Center,
                    style = SixpackTheme.typography.c1Regular,
                    color = SixpackTheme.colors.gray400,
                )
            }
            Column(
                modifier = Modifier.weight(mainWeight),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "${uiState.recommendedTimeMinutes}분",
                    textAlign = TextAlign.Center,
                    style = SixpackTheme.typography.b2Medium,
                    color = Color(0xFF4C46FC),
                )
                Text(
                    text = "러닝",
                    textAlign = TextAlign.Center,
                    style = SixpackTheme.typography.c1Regular,
                    color = SixpackTheme.colors.gray400,
                )
            }

            Column(
                modifier = Modifier.weight(minWeight),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "${uiState.coolDownMinutes}분",
                    textAlign = TextAlign.Center,
                    style = SixpackTheme.typography.b2Medium,
                    color = Color(0xFF4C46FC),
                )
                Text(
                    text = "쿨다운",
                    textAlign = TextAlign.Center,
                    style = SixpackTheme.typography.c1Regular,
                    color = SixpackTheme.colors.gray400,
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewIntervalRoutineChart() {
    IntervalRoutineChart(
        uiState =
            RunningGoalUiState(
                roundCount = 1,
                totalRoundCount = 1,
                warmUpMinutes = 5,
                recommendedTimeMinutes = 20,
                coolDownMinutes = 5,
            ),
    )
}
