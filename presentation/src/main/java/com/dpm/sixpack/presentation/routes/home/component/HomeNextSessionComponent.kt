package com.dpm.sixpack.presentation.routes.home.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.routes.home.contract.HomeSessionComponentState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun HomeNextSessionComponent(
    modifier: Modifier = Modifier,
    state: HomeSessionComponentState,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .widthIn(min = 335.dp)
            .background(
                color = SixpackTheme.colors.gray0,
                shape = SixpackTheme.shapes.round16
            )
            .padding(20.dp)
    ) {
        Text(
            text = stringResource(R.string.home_goal_session_count)
                .format(state.sessionCount),
            style = SixpackTheme.typography.t2Bold,
            color = SixpackTheme.colors.gray900
        )
        state.cheerUpStringRes?.let {
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = stringResource(it),
                style = SixpackTheme.typography.b2Regular,
                color = SixpackTheme.colors.gray900
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top=24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeNextSessionGoalComponent(
                modifier = Modifier
                    .weight(1f),
                iconRes = R.drawable.ic_distance,
                title = state.distance,
                description = stringResource(R.string.home_goal_distance),
                contentDescription = "목표 거리 아이콘" // TODO: 접근성 resource
            )
            HomeNextSessionGoalComponent(
                modifier = Modifier
                    .weight(1f),
                iconRes = R.drawable.ic_duration,
                title = state.duration,
                description = stringResource(R.string.common_recommended_duration),
                contentDescription = "목표 시간 아이콘" // TODO: 접근성 resource
            )
            HomeNextSessionGoalComponent(
                modifier = Modifier
                    .weight(1f),
                iconRes = R.drawable.ic_pace,
                title = state.pace,
                description = stringResource(R.string.common_recommended_pace),
                contentDescription = "목표 페이스 아이콘" // TODO: 접근성 resource
            )
        }

        DoRunDefaultButton(
            text = stringResource(R.string.home_goal_run),
            onClick = { onClick() },
            modifier = Modifier.fillMaxWidth().padding(top= 24.dp)
        )
    }
}

@Composable
fun HomeNextSessionGoalComponent(
    @DrawableRes iconRes: Int,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = "",
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription, // TODO: 접근성 resource
            tint = SixpackTheme.colors.gray300
        )
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = title,
            style = SixpackTheme.typography.t1Bold,
            color = SixpackTheme.colors.gray900
        )
        Text(
            modifier = Modifier.padding(top = 2.dp),
            text = description,
            style = SixpackTheme.typography.c1Regular,
            color = SixpackTheme.colors.gray600
        )
    }
}

@Preview
@Composable
private fun HomeNextSessionComponentPreview() {
    DoRunPreviewWrapper {
        HomeNextSessionComponent(
            state = HomeSessionComponentState(
                sessionCount = 3,
                cheerUpStringRes = R.string.home_goal_cheer_up_1_25,
                distance = "5.0km",
                duration = "01:00:00",
                pace = "6'00\"",
            )
        )
    }
}

@Preview
@Composable
private fun HomeNextSessionGoalComponentPreview() {
    DoRunPreviewWrapper {
        HomeNextSessionGoalComponent(
            iconRes = R.drawable.ic_distance,
            title = "5.0km",
            description = "목표 거리"
        )
    }
}
