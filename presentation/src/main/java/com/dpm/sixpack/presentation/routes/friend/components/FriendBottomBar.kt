package com.dpm.sixpack.presentation.routes.friend.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun FriendBottomBar(
    onAddFriendClick: () -> Unit,
    onCopyCodeClick: () -> Unit,
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            DoRunDefaultButton(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                text = stringResource(R.string.friend_code_enter_button),
                onClick = onAddFriendClick,
            )

            TextButton(
                onClick = onCopyCodeClick,
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Text(
                    text = stringResource(R.string.my_friend_code_copy),
                    color = SixpackTheme.colors.gray500,
                    style = SixpackTheme.typography.b2Medium,
                )
            }
        }
    }
}
