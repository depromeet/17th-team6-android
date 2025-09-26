package com.dpm.sixpack.presentation.routes.session

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.routes.session.component.goal.GoalMetrics
import com.dpm.sixpack.presentation.routes.session.component.goal.IntervalRoutineChart
import com.dpm.sixpack.presentation.routes.session.contract.uistate.RunningGoalUiState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun RunningGoalScreen(
    goalUiState: RunningGoalUiState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .padding(top = 24.dp)
                .padding(horizontal = 28.dp),
    ) {
        GoalHeader(goalUiState)
        Spacer(modifier = Modifier.height(24.dp))
        GoalMetrics(goalUiState)
        Spacer(modifier = Modifier.height(32.dp))
        IntervalRoutineHeader()
        Spacer(modifier = Modifier.height(24.dp))
        IntervalRoutineChart(goalUiState)
    }
}

@Composable
private fun GoalHeader(uiState: RunningGoalUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(
                text = "${uiState.roundCount}회차 / 총 ${uiState.totalRoundCount}회",
                style = SixpackTheme.typography.b1Bold,
                color = SixpackTheme.colors.blue600,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "오늘의 러닝 목표",
                style = SixpackTheme.typography.t1Bold,
                color = SixpackTheme.colors.gray900,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "오늘도 두런두런과 함께 힘차게 달려볼까요?",
                style = SixpackTheme.typography.b2Medium,
                color = SixpackTheme.colors.gray600,
            )
        }
        Image(
            painter = painterResource(id = R.drawable.ill_goal_character_2),
            contentDescription = "캐릭터 아이콘",
            modifier = Modifier.size(58.dp),
        )
    }
}

@Composable
internal fun VerticalDivider() {
    Box(
        modifier =
            Modifier
                .height(40.dp)
                .width(1.dp)
                .background(SixpackTheme.colors.gray100),
    )
}

@Composable
private fun IntervalRoutineHeader() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "구간별 루틴",
            style = SixpackTheme.typography.t2Bold,
            color = SixpackTheme.colors.gray900,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "러닝 전후로 속도를 낮춰 달리면 부상 위험을 줄이고\n안전하게 달릴 수 있어요.",
            style = SixpackTheme.typography.b2Regular,
            color = SixpackTheme.colors.gray600,
        )
    }
}

@Preview
@Composable
private fun PreviewGoalScreen() {
    RunningGoalScreen(
        goalUiState =
            RunningGoalUiState(
                distanceMeter = 10000,
                recommendedTimeMinutes = 60,
                recommendedPaceSeconds = 360,
            ),
    )
}
