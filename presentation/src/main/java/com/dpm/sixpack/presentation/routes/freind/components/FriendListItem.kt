package com.dpm.sixpack.presentation.routes.freind.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.routes.freind.contract.FriendItem
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun FriendListItem(
    active: Boolean,
    friendItem: FriendItem,
    modifier: Modifier = Modifier,
    onCheerClick: () -> Unit = {},
) {
    val showInactive = remember { mutableStateOf(!(active || friendItem.isMe)) }

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
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

            if (showInactive.value) {
                InactiveLabel(
                    modifier =
                        Modifier
                            .align(Alignment.Center)
                            .offset(x = (-16).dp, y = (-16).dp),
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = friendItem.nickName.toString(),
                    style = MaterialTheme.typography.titleMedium,
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
                            text = "나",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                            color = SixpackTheme.colors.gray0,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 마지막 러닝 시간
                Text(
                    text = friendItem.latestRunAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = friendItem.distanceInMeter.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // 응원하기
        if (showInactive.value) {
            DoRunDefaultButton(
                text = "응원하기",
                onClick = onCheerClick,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}

@Preview(showBackground = true, name = "전체 상태 미리보기")
@Composable
private fun FriendListItemPreview() {
    Column {
        // 1. 러닝 중인 상태 (나)
        FriendListItem(
            true,
            friendItem =
                FriendItem(
                    userId = 1231242,
                    nickName = "승규",
                    isMe = true,
                    profileImgUrl = "",
                    latestRunAt = "2일 전",
                    distanceInMeter = 5000,
                    latitude = 37.5301,
                    longitude = 127.12345,
                ),
        )

//        // 2. 최근 러닝 완료 상태
//        FriendListItem(
//            friendItem =
//                FriendItem(
//                    userId = 1231242,
//                    isMe = false,
//                    profileImgUrl = "",
//                    latestRunAt = "25분 전",
//                    distanceInMeter = 5000,
//                    latitude = 37.5301,
//                    longitude = 127.12345,
//                ),
//        )
//
        // 3. 비활성 상태 (응원하기 버튼 표시)
        FriendListItem(
            active = false,
            friendItem =
                FriendItem(
                    userId = 1231242,
                    nickName = "승범",
                    isMe = false,
                    profileImgUrl = "",
                    latestRunAt = "5시간 전",
                    distanceInMeter = 5000,
                    latitude = 37.5301,
                    longitude = 127.12345,
                ),
        )
    }
}
