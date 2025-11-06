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
import androidx.compose.ui.unit.dp
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
                text = "친구 코드 입력하기",
                onClick = onAddFriendClick,
            )

            TextButton(
                onClick = onCopyCodeClick,
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Text(
                    text = "내 코드 복사하기",
                    color = SixpackTheme.colors.gray500,
                    style = SixpackTheme.typography.b2Medium,
                )
            }
        }
    }
}
