package com.dpm.sixpack.presentation.routes.running.map.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun FriendAwakeButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit = { },
) {
    Button(
        modifier = modifier,
        onClick = {
            if (enabled) onClick() else null
        },
        shape = SixpackTheme.shapes.round12,
        colors =
            if (enabled) {
                ButtonDefaults.buttonColors(
                    containerColor = SixpackTheme.colors.blue200,
                )
            } else {
                ButtonDefaults.buttonColors(
                    containerColor = SixpackTheme.colors.gray100,
                )
            },
    ) {
        Row(
            modifier = Modifier.padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.friend_awake_btn),
                style = SixpackTheme.typography.b1Bold,
                color = if (enabled) SixpackTheme.colors.blue600 else SixpackTheme.colors.gray400,
            )
            Icon(
                modifier = Modifier.padding(start = 2.dp),
                painter = painterResource(id = R.drawable.ic_friend_awake),
                contentDescription = null,
                tint = if (enabled) SixpackTheme.colors.blue600 else SixpackTheme.colors.gray400,
            )
        }
    }
}
