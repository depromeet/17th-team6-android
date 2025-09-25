package com.dpm.sixpack.presentation.routes.home.component.total.sub

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
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
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun HomeTotalGoalCompletedSubComponent(modifier: Modifier = Modifier) {
    Row(
        modifier =
            modifier
                .background(
                    color = SixpackTheme.colors.blue100,
                    shape = SixpackTheme.shapes.round16,
                ).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.ill_congratulation),
            contentDescription = "축하 일러스트", // TODO: 접근성 resource 추가
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = stringResource(R.string.home_goal_achieve),
            style = SixpackTheme.typography.b1Medium,
            color = SixpackTheme.colors.gray900,
        )
    }
}

@Preview
@Composable
private fun HomeTotalGoalCompletedSubComponentPreview() {
    DoRunPreviewWrapper {
        HomeTotalGoalCompletedSubComponent()
    }
}
