package com.dpm.sixpack.presentation.routes.home.component.session.next

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.routes.home.component.session.next.item.HomeNextSessionInformationItem
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            HomeNextSessionInformationItem(
                iconRes = R.drawable.ic_distance,
                value = state.distance,
                title = stringResource(R.string.home_goal_distance),
                contentDescription = "목표 거리 아이콘" // TODO: 접근성 resource
            )
            VerticalDivider(
                modifier = Modifier.height(40.dp),
                color = SixpackTheme.colors.gray100,
                thickness = 1.dp
            )
            HomeNextSessionInformationItem(
                iconRes = R.drawable.ic_duration,
                value = state.duration,
                title = stringResource(R.string.common_recommended_duration),
                contentDescription = "목표 시간 아이콘" // TODO: 접근성 resource
            )
            VerticalDivider(
                modifier = Modifier.height(40.dp),
                color = SixpackTheme.colors.gray100,
                thickness = 1.dp
            )
            HomeNextSessionInformationItem(
                iconRes = R.drawable.ic_pace,
                value = state.pace,
                title = stringResource(R.string.common_recommended_pace),
                contentDescription = "목표 페이스 아이콘" // TODO: 접근성 resource
            )
        }

        DoRunDefaultButton(
            text = stringResource(R.string.home_goal_run),
            onClick = { onClick() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
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
