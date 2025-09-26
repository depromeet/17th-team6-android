package com.dpm.sixpack.presentation.routes.session.component.goal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.util.formatDistanceToKm
import com.dpm.sixpack.presentation.common.util.formatPaceToString
import com.dpm.sixpack.presentation.routes.session.VerticalDivider
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningGoalUiState

@Composable
internal fun GoalMetrics(uiState: RunningGoalUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MetricItem(
            value = formatDistanceToKm(uiState.distanceMeter),
            label = "목표 거리",
            imageResId = R.drawable.ic_distance,
        )
        VerticalDivider()
        MetricItem(
            value = "${uiState.recommendedTimeMinutes}:00",
            label = "권장 러닝 시간",
            imageResId = R.drawable.ic_duration,
        )
        VerticalDivider()
        MetricItem(
            value = formatPaceToString(uiState.recommendedPaceSeconds),
            label = "권장 페이스",
            imageResId = R.drawable.ic_pace,
        )
    }
}

@Preview
@Composable
private fun GoalMetricsPreview() {
    GoalMetrics(
        uiState =
            RunningGoalUiState(
                distanceMeter = 10000,
                recommendedTimeMinutes = 60,
                recommendedPaceSeconds = 360,
                roundCount = 1,
                totalRoundCount = 20,
            ),
    )
}
