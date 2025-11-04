package com.dpm.sixpack.presentation.routes.friend

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dpm.sixpack.core.util.TimeUtil.isoStringToEpochSeconds
import com.dpm.sixpack.presentation.common.components.DoRunDefaultAsyncImage
import com.dpm.sixpack.presentation.common.components.DoRunDefaultButton
import com.dpm.sixpack.presentation.common.components.topbar.DoRunNavigationTopBar
import com.dpm.sixpack.presentation.common.model.FriendUiItem
import com.dpm.sixpack.presentation.common.model.LastRunInfoUi
import com.dpm.sixpack.presentation.common.util.convertTimeDiffToString
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
fun FriendRoute(viewModel: FriendViewModel = hiltViewModel()) {
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendScreen(
    userList: List<FriendUiItem>,
    onNavigateBack: () -> Unit = {},
    onEnterCodeClick: () -> Unit = {},
    onCopyCodeClick: () -> Unit = {},
    onFriendOptionsClick: (Int) -> Unit = {},
) {
    val friendList = userList.filter { !it.isMe }

    Scaffold(
        topBar = {
            DoRunNavigationTopBar(
                navigateToBack = onNavigateBack,
                titleContent = {
                    Text(
                        text = "친구",
                        style = SixpackTheme.typography.t1Bold,
                        color = SixpackTheme.colors.gray900,
                    )
                },
            )
        },
        bottomBar = {
            FriendBottomBar(
                onEnterCodeClick = onEnterCodeClick,
                onCopyCodeClick = onCopyCodeClick,
            )
        },
        containerColor = SixpackTheme.colors.gray0,
    ) { paddingValues ->
        // 메인 컨텐츠 영역
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "친구목록",
                        style = SixpackTheme.typography.t1Bold,
                        color = SixpackTheme.colors.gray900,
                    )
                    Text(
                        text = "${friendList.size}",
                        style = SixpackTheme.typography.t1Bold,
                        color = SixpackTheme.colors.gray500,
                    )
                }
            }

            items(
                count = friendList.size,
                key = { friendList[it].userId },
            ) { index ->
                FriendListItem(
                    friend = friendList[index],
                    onOptionsClick = { },
                )
            }
        }
    }
}

@Composable
private fun FriendListItem(
    friend: FriendUiItem,
    onOptionsClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DoRunDefaultAsyncImage(
            model = friend.profileImgUrl,
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

        Spacer(Modifier.width(12.dp))

        // 닉네임, 마지막 활동 시간
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            val secondsSinceLastRun =
                if (friend.lastRunInfo != null) {
                    val lastRunSec = isoStringToEpochSeconds(friend.lastRunInfo.lastestRunAt)
                    if (lastRunSec != null) (System.currentTimeMillis() / 1000L) - lastRunSec else null
                } else {
                    null // 러닝 기록 없음
                }
            Text(
                text = friend.nickName,
                style = SixpackTheme.typography.t2Bold,
                color = SixpackTheme.colors.gray900,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text =
                    if (secondsSinceLastRun != null) {
                        convertTimeDiffToString(
                            context = LocalContext.current,
                            secondsSinceLastRun,
                        )
                    } else {
                        ""
                    },
                style = SixpackTheme.typography.b2Regular,
                color = SixpackTheme.colors.gray500,
            )
        }

        // 더보기 버튼
        IconButton(onClick = onOptionsClick) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "친구 옵션",
                tint = Color.Gray,
            )
        }
    }
}

@Composable
private fun FriendBottomBar(
    onEnterCodeClick: () -> Unit,
    onCopyCodeClick: () -> Unit,
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp, // 그림자
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 16.dp),
            // 하단 여백 추가
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // "친구 코드 입력하기" 버튼
            DoRunDefaultButton(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                text = "친구 코드 입력하기",
                onClick = { },
            )

            // "내 코드 복사하기" 텍스트 버튼
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

// --- Preview ---
@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun FriendScreenPreview() {
    // MaterialTheme으로 감싸야 함 (실제 앱에서는 이미 적용되어 있을 것)
    MaterialTheme {
        FriendScreen(
            listOf(
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
            ),
        )
    }
}
