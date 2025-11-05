package com.dpm.sixpack.presentation.routes.running.map.friendsheet

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun FriendSheetTitle(
    modifier: Modifier = Modifier,
    onIconClick: () -> Unit = {},
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(R.string.friend_status_title),
            color = SixpackTheme.colors.gray900,
            style = SixpackTheme.typography.t1Bold,
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onIconClick) {
            Icon(
                painter = painterResource(R.drawable.ic_friend),
                contentDescription = "Friend icon",
                tint = SixpackTheme.colors.gray800,
            )
        }
    }
}
