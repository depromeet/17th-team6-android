package com.dpm.sixpack.presentation.routes.session.component.panel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun MainSessionInfo(
    primaryInfo: String,
    secondaryInfo: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Companion.CenterVertically,
    ) {
        Text(
            text = primaryInfo,
            style = SixpackTheme.typography.h2Bold,
            color = SixpackTheme.colors.gray900,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Companion.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.panel_target_distance),
                style = SixpackTheme.typography.b2Medium,
                color = SixpackTheme.colors.gray500,
            )
            Text(
                text = secondaryInfo,
                style = SixpackTheme.typography.t1Bold,
                color = SixpackTheme.colors.gray900,
            )
        }
    }
}

@Composable
internal fun PrePostSessionInfo(
    primaryInfo: String,
    showSkip: Boolean,
    onSkipClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Companion.CenterVertically,
    ) {
        Text(
            text = primaryInfo,
            style = SixpackTheme.typography.h2Bold,
            color = SixpackTheme.colors.gray900,
        )
        if (showSkip) {
            Text(
                modifier = Modifier.clickable(onClick = onSkipClick),
                text = stringResource(R.string.panel_skip),
                style = SixpackTheme.typography.t2Bold,
                color = SixpackTheme.colors.blue500,
            )
        }
    }
}
