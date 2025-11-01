package com.dpm.sixpack.presentation.routes.feed.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedDateUiState
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun FeedFTAButton(
    enabled: Boolean,
    onFTAButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    ) {
    Box(
        modifier = modifier
            .padding(
                bottom = 16.dp,
                end = 20.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(
                    SixpackTheme.colors.blue600
                )
                .clickable(enabled = enabled) { onFTAButtonClick() }
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.ic_plus),
                contentDescription = stringResource(id = R.string.feed_floating_action_button_description),
                modifier = Modifier.size(24.dp),
            )
        }
    }
}
