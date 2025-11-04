package com.dpm.sixpack.presentation.routes.freind.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.dpm.sixpack.presentation.common.model.FriendUiItem

@Composable
fun FriendsLazyColumn(
    modifier: Modifier = Modifier,
    pagingItems: LazyPagingItems<FriendUiItem>,
    onAwakeClick: (Long) -> Unit = {},
    onItemClick: (Long) -> Unit = {},
) {
    when (pagingItems.loadState.refresh) {
        is LoadState.Loading -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        // 전체 새로고침 에러
        is LoadState.Error -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("에러가 발생했습니다. (재시도 버튼 구현)")
            }
        }
        // 로드 성공 (및 비어있는 상태)
        is LoadState.NotLoading -> {
            // 목록이 비어있는지 확인
            if (pagingItems.itemCount == 0) {
                Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("친구가 없습니다.")
                }
                return
            }

            // 실제 목록을 그리는 LazyColumn
            LazyColumn(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(
                    pagingItems.itemCount,
                    key = pagingItems.itemKey { it.userId },
                ) { index ->
                    val friendItem = pagingItems[index]
                    friendItem?.let {
                        FriendListItem(
                            friendItem = it,
                            onAwakeClick = {
                                onAwakeClick(it.userId)
                            },
                            // 7. ViewModel Intent와 연결 (FriendListItem이 modifier를 받는다고 가정)
                            modifier =
                                Modifier.clickable {
                                    onItemClick(it.userId)
                                },
                        )
                    }
                }

                if (pagingItems.loadState.append is LoadState.Loading) {
                    item {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}
