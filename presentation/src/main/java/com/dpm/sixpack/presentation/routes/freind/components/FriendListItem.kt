package com.dpm.sixpack.presentation.routes.freind.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.dpm.sixpack.presentation.common.model.FriendUiItem
import com.dpm.sixpack.presentation.common.util.calculateSecDiff
import com.dpm.sixpack.presentation.common.util.convertTimeDiffToString
import com.dpm.sixpack.presentation.common.util.formatDistanceToKm
import com.dpm.sixpack.presentation.routes.running.map.component.FriendAwakeButton
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun FriendListItem(
    friendItem: FriendUiItem,
    modifier: Modifier = Modifier,
    onAwakeClick: () -> Unit = {},
) {
    val distance = friendItem.distanceInMeter
    val lastestRunAt = friendItem.lastestRunAt
    val secDiff = if (lastestRunAt == null) null else calculateSecDiff(lastestRunAt)
    val isOutdated = secDiff == null || secDiff > 48 * 60 * 60
    val showInactive = isOutdated && !friendItem.isMe

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Profile Image
        Box(
            modifier = Modifier.size(48.dp),
        ) {
            AsyncImage(
                model =
                    ImageRequest
                        .Builder(LocalContext.current)
                        .data(friendItem.profileImgUrl)
                        .crossfade(true)
                        .build(),
                contentDescription = null,
                modifier =
                    Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .border(1.dp, SixpackTheme.colors.gray200, shape = CircleShape),
                placeholder = ColorPainter(SixpackTheme.colors.gray0),
                error = ColorPainter(SixpackTheme.colors.gray0),
                contentScale = ContentScale.Crop,
            )

            if (showInactive) {
                InactiveLabel(
                    modifier =
                        Modifier
                            .align(Alignment.Center)
                            .offset(x = (-16).dp, y = (-16).dp),
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // User Info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // nickname
                Text(
                    text = friendItem.nickName,
                    color = SixpackTheme.colors.gray900,
                    style = SixpackTheme.typography.t2Bold,
                    fontWeight = FontWeight.Bold,
                )
                // '나' 태그
                if (friendItem.isMe) {
                    Surface(
                        modifier = Modifier.padding(start = 6.dp),
                        shape = CircleShape,
                        color = SixpackTheme.colors.blue600,
                    ) {
                        Text(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            text = "나",
                            color = SixpackTheme.colors.gray0,
                            style = SixpackTheme.typography.c1Medium,
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 마지막 러닝 시간
                Text(
                    text = if (secDiff != null) convertTimeDiffToString(LocalContext.current, secDiff) else "",
                    style = SixpackTheme.typography.b2Regular,
                    color = SixpackTheme.colors.gray500,
                )
            }
            // 최근 러닝 거리 / 장소
            Text(
                text = if (!isOutdated && distance != null) formatDistanceToKm(distance) else "최근 러닝 기록이 없어요",
                style = SixpackTheme.typography.b2Medium,
                color = SixpackTheme.colors.gray700,
            )
        }

        // 응원하기
        if (showInactive) {
            FriendAwakeButton(
                onClick = onAwakeClick,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FriendListItemPreview() {
    Column {
        // 나
        FriendListItem(
            FriendUiItem(
                userId = 1234,
                nickName = "승규",
                isMe = true,
                profileImgUrl = "",
                lastestRunAt = "2025-10-19T19:57:13Z",
                distanceInMeter = 5000,
            ),
        )

        // 친구, 활성
        FriendListItem(
            FriendUiItem(
                userId = 24455,
                nickName = "소래",
                isMe = false,
                profileImgUrl = "",
                lastestRunAt = "2025-10-20T09:57:13Z",
                distanceInMeter = 900,
            ),
        )

        // 비활성 상태 (응원하기 버튼 표시)
        FriendListItem(
            FriendUiItem(
                userId = 9786,
                nickName = "승범",
                isMe = false,
                profileImgUrl = "",
                lastestRunAt = "2025-10-16T19:57:13Z",
                distanceInMeter = 3000,
            ),
        )
    }
}
