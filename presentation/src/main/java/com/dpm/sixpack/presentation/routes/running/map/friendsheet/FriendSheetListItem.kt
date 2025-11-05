package com.dpm.sixpack.presentation.routes.running.map.friendsheet

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.core.util.TimeUtil.isoStringToEpochSeconds
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.DoRunDefaultAsyncImage
import com.dpm.sixpack.presentation.common.model.FriendUiItem
import com.dpm.sixpack.presentation.common.model.LastRunInfoUi
import com.dpm.sixpack.presentation.common.util.convertTimeDiffToString
import com.dpm.sixpack.presentation.common.util.formatDistanceToKm
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun FriendSheetListItem(
    friendItem: FriendUiItem,
    modifier: Modifier = Modifier,
    onAwakeClick: () -> Unit = {},
) {
    val lastRunInfo = friendItem.lastRunInfo
    val hasLastRun = lastRunInfo != null
    val lastCheeredAt = friendItem.latestCheeredAt // 최근 응원 시각 (String?)
    val currentTimeSec = System.currentTimeMillis() / 1000L

    // 깨우기 버튼 노출 여부 결정
    // 마지막 러닝으로부터 몇 초가 지났는지
    val secondsSinceLastRun =
        if (hasLastRun) {
            val lastRunSec = isoStringToEpochSeconds(lastRunInfo!!.lastestRunAt)
            if (lastRunSec != null) currentTimeSec - lastRunSec else null
        } else {
            null // 러닝 기록 없음
        }
    // "비활성" = 러닝 기록이 없거나, 48시간(172,800초)을 초과함
    val isInactive = !hasLastRun || secondsSinceLastRun == null || secondsSinceLastRun > 48 * 60 * 60
    // 비활성 상태이고, 내가 아닐 때만 버튼 노출
    val showAwakeButton = isInactive && !friendItem.isMe

    // 깨우기 버튼 활성화 여부
    // 마지막 응원으로부터 몇 초가 지났는지
    val secondsSinceLastCheer =
        if (lastCheeredAt != null) {
            val lastCheeredSec = isoStringToEpochSeconds(lastCheeredAt)
            if (lastCheeredSec != null) currentTimeSec - lastCheeredSec else null
        } else {
            null // 응원한 적 없음
        }

    // 1. 응원한 적이 없거나
    // 2. 응원한 지 24시간이 지났거나
    val isAwakeButtonEnabled =
        secondsSinceLastCheer == null || secondsSinceLastCheer >= 24 * 60 * 60

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
            DoRunDefaultAsyncImage(
                model = friendItem.profileImgUrl,
                contentDescription = null,
                modifier =
                    Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .border(1.dp, SixpackTheme.colors.blue600, shape = CircleShape),
                placeholder = ColorPainter(SixpackTheme.colors.gray50),
                error = painterResource(id = R.drawable.ill_profile_placeholder),
                contentScale = ContentScale.Crop,
            )

            if (isInactive) {
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
                Text(
                    text = friendItem.nickName,
                    color = SixpackTheme.colors.gray900,
                    style = SixpackTheme.typography.t2Bold,
                    fontWeight = FontWeight.Bold,
                )

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
                    text =
                        if (secondsSinceLastRun != null) {
                            convertTimeDiffToString(
                                LocalContext.current,
                                secondsSinceLastRun,
                            )
                        } else {
                            ""
                        },
                    style = SixpackTheme.typography.b2Regular,
                    color = SixpackTheme.colors.gray500,
                )
            }
            // 최근 러닝 거리 / 장소
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (!isInactive) {
                    // 거리
                    Text(
                        text = formatDistanceToKm(friendItem.lastRunInfo.distanceInMeter),
                        style = SixpackTheme.typography.b2Medium,
                        color = SixpackTheme.colors.gray700,
                    )
                    // 주소
                    friendItem.lastRunInfo.address.let {
                        Text(
                            text = it,
                            style = SixpackTheme.typography.b2Medium,
                            color = SixpackTheme.colors.gray700,
                        )
                    }
                } else {
                    Text(
                        text = stringResource(R.string.friend_no_recent_running),
                        style = SixpackTheme.typography.b2Medium,
                        color = SixpackTheme.colors.gray700,
                    )
                }
            }
        }

        // 응원하기
        if (showAwakeButton) {
            FriendAwakeButton(
                onClick = onAwakeClick,
                enabled = isAwakeButtonEnabled,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FriendListItemPreview() {
    Column {
        // 나
        FriendSheetListItem(
            FriendUiItem(
                userId = 1234,
                nickName = "승규",
                isMe = true,
                profileImgUrl = "",
                lastRunInfo =
                    LastRunInfoUi(
                        lastestRunAt = "2025-11-04T09:57:13Z",
                        distanceInMeter = 3000,
                        latitude = 37.566535,
                        longitude = 126.9779692,
                        address = "서울",
                    ),
            ),
        )

        // 친구, 활성
        FriendSheetListItem(
            FriendUiItem(
                userId = 2445895,
                nickName = "소래",
                isMe = false,
                profileImgUrl = "",
                lastRunInfo =
                    LastRunInfoUi(
                        lastestRunAt = "2025-11-04T09:57:13Z",
                        distanceInMeter = 5000,
                        latitude = 37.566535,
                        longitude = 126.9779692,
                        address = "서울",
                    ),
                latestCheeredAt = "2025-11-01T09:57:13Z",
            ),
        )

        // 친구, 비활성, 깨우기 활성
        FriendSheetListItem(
            FriendUiItem(
                userId = 24455,
                nickName = "소래",
                isMe = false,
                profileImgUrl = "",
                lastRunInfo =
                    LastRunInfoUi(
                        lastestRunAt = "2025-11-01T09:57:13Z",
                        distanceInMeter = 5000,
                        latitude = 37.566535,
                        longitude = 126.9779692,
                        address = "서울",
                    ),
                latestCheeredAt = "2025-11-01T09:57:13Z",
            ),
        )

        // // 친구, 비활성, 깨우기 비활성
        FriendSheetListItem(
            FriendUiItem(
                userId = 9767886,
                nickName = "승범",
                isMe = false,
                profileImgUrl = "",
                lastRunInfo =
                    LastRunInfoUi(
                        lastestRunAt = "2025-11-01T09:57:13Z",
                        distanceInMeter = 5000,
                        latitude = 37.566535,
                        longitude = 126.9779692,
                        address = "서울",
                    ),
                latestCheeredAt = "2025-11-04T09:57:13Z",
            ),
        )

        // // 친구, 비활성, 깨우기 비활성
        FriendSheetListItem(
            FriendUiItem(
                userId = 9213786,
                nickName = "승범",
                isMe = false,
                profileImgUrl = "",
                lastRunInfo =
                    LastRunInfoUi(
                        lastestRunAt = "2025-11-02T13:57:13Z",
                        distanceInMeter = 5000,
                        latitude = 37.566535,
                        longitude = 126.9779692,
                        address = "서울",
                    ),
                latestCheeredAt = "2025-11-02T09:57:13Z",
            ),
        )
    }
}
