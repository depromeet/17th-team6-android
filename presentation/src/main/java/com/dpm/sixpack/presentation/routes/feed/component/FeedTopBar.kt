package com.dpm.sixpack.presentation.routes.feed.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.components.topbar.DoRunTopBarSlot
import com.dpm.sixpack.presentation.common.util.modifier.noRippleClickable
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun FeedTopBar(
    onGroupIconClick: () -> Unit,
    onAlarmIconClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DoRunTopBarSlot(
        modifier = modifier.padding(start = 20.dp, end = 10.dp),
        leadingContent = {
            Text(
                text = "인증 피드",
                style = SixpackTheme.typography.t1Bold,
                color = SixpackTheme.colors.gray900
            )
        },
        trailingContent = {
            Row(
                modifier = Modifier.padding(10.dp),

                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .noRippleClickable(onClick = onGroupIconClick),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_group),
                        contentDescription = "검색",
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier.noRippleClickable(onClick = onAlarmIconClick),
                    contentAlignment = Alignment.Center

                ) {
                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_alarm),
                        contentDescription = "알림",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    )
}

@Preview
@Composable
fun FeedTopBarPreview() {
    DoRunPreviewWrapper {
        FeedTopBar(
            onGroupIconClick = { },
            onAlarmIconClick = { }
        )
    }
}
