package com.dpm.sixpack.presentation.routes.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.routes.home.contract.HomeTotalGoalComponentState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun HomeTotalGoalComponent(
    modifier: Modifier = Modifier,
    state: HomeTotalGoalComponentState,
    onNavigateToGoalList: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 24.dp
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                state.imageRes?.let {
                    Image(
                        modifier = Modifier.padding(end = 8.dp),
                        painter = painterResource(it),
                        contentDescription = "운동 목표 일러스트", // TODO: 접근성 resource 추가
                    )
                }
                Text(
                    text = state.title,
                    style = SixpackTheme.typography.t1Bold,
                    color = SixpackTheme.colors.gray900
                )
            }

            TextButtonWithIcon(
                modifier = modifier,
                title = stringResource(R.string.home_goal_list),
                onClick = onNavigateToGoalList
            )
        }
    }
}

@Preview
@Composable
private fun HomeTotalGoalComponentPreview() {
    DoRunPreviewWrapper {
        HomeTotalGoalComponent(
            state = HomeTotalGoalComponentState(
                title = "이번 달 목표",
                imageRes = R.drawable.ill_marathon_10km,
                distance = "5.0km",
                duration = "01:00:00",
                pace = "6'00\""
            )
        )
    }
}
