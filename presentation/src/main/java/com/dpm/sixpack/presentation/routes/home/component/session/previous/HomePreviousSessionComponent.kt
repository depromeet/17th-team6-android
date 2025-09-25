package com.dpm.sixpack.presentation.routes.home.component.session.previous

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun HomePreviousSessionComponent(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Row(
        modifier =
            modifier
                .widthIn(min = 335.dp)
                .background(
                    color = SixpackTheme.colors.gray0,
                    shape = SixpackTheme.shapes.round16,
                ).clip(shape = SixpackTheme.shapes.round16)
                .clickable(onClick = onClick)
                .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = stringResource(R.string.home_goal_run_again_title_condition),
                style = SixpackTheme.typography.b2Regular,
                color = SixpackTheme.colors.gray600,
            )
            Text(
                text = stringResource(R.string.home_goal_run_again_title),
                style = SixpackTheme.typography.t2Bold,
                color = SixpackTheme.colors.gray900,
            )
        }
        Image(
            painter = painterResource(R.drawable.ill_previous_session_running),
            contentDescription = "이전 세션 다시하기 일러스트", // TODO: 접근성 resource 추가
        )
    }
}

@Preview
@Composable
private fun HomePreviousSessionComponentPreview() {
    DoRunPreviewWrapper {
        HomePreviousSessionComponent(onClick = {})
    }
}
