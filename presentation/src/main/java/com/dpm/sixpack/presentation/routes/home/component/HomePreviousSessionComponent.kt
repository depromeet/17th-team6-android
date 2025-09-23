package com.dpm.sixpack.presentation.routes.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun HomePreviousSessionComponent(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .widthIn(min = 335.dp)
            .background(
                color = SixpackTheme.colors.gray0,
                shape = SixpackTheme.shapes.round16
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.home_goal_run_again_title_condition),
                style = SixpackTheme.typography.b2Regular,
                color = SixpackTheme.colors.gray600
            )
            Text(
                text = stringResource(R.string.home_goal_run_again_title),
                style = SixpackTheme.typography.t2Bold,
                color = SixpackTheme.colors.gray900
            )
        }
    }
}
