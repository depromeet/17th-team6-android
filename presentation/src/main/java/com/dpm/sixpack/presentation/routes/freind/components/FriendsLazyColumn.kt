package com.dpm.sixpack.presentation.routes.freind.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.model.FriendUiItem

@Composable
fun FriendsLazyColumn(
    modifier: Modifier = Modifier,
    friendList: List<FriendUiItem> = emptyList(),
    onAwakeClick: (Long) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        // 친구 목록 아이템들을 표시
        items(
            items = friendList,
            key = { friend -> friend.userId }, // 각 친구의 고유 유저 ID를 key로 지정
        ) { friend ->
            FriendListItem(
                friendItem = friend,
                onAwakeClick = {
                    onAwakeClick(friend.userId)
                },
            )
        }
    }
}

@Preview
@Composable
private fun FriendsLazyColumnPreview() {
    DoRunPreviewWrapper {
        FriendsLazyColumn(
            friendList =
                listOf(
                    FriendUiItem(
                        userId = 12345,
                        nickName = "승규",
                        isMe = true,
                        profileImgUrl = "",
                        lastestRunAt = "2025-10-19T19:57:13Z",
                        distanceInMeter = 5000,
                        latitude = 37.5301,
                        longitude = 127.12345,
                    ),
                    FriendUiItem(
                        userId = 12315,
                        nickName = "소래",
                        isMe = false,
                        profileImgUrl = "",
                        lastestRunAt = "2025-10-20T09:57:13Z",
                        distanceInMeter = 900,
                        latitude = 37.5301,
                        longitude = 127.12345,
                    ),
                    FriendUiItem(
                        userId = 112415,
                        nickName = "소래",
                        isMe = false,
                        profileImgUrl = "",
                        lastestRunAt = "2025-10-16T19:57:13Z",
                        distanceInMeter = 3000,
                        latitude = 37.5301,
                        longitude = 127.12345,
                    ),
                ),
        )
    }
}
