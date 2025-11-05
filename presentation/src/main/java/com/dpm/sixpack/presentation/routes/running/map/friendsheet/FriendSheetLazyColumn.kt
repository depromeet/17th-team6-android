package com.dpm.sixpack.presentation.routes.running.map.friendsheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.dpm.sixpack.presentation.common.model.FriendUiItem
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun FriendSheetLazyColumn(
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

        is LoadState.Error -> {
            Column(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "데이터를 불러오는 데 실패했습니다.",
                    style = SixpackTheme.typography.b1Regular,
                    color = SixpackTheme.colors.gray700,
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    onClick = {
                        pagingItems.retry()
                    },
                    colors =
                        ButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            disabledContentColor = Color.Transparent,
                        ),
                ) {
                    Row(
                        modifier = Modifier.padding(all = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "재시도",
                            style = SixpackTheme.typography.b1Regular,
                            color = SixpackTheme.colors.gray700,
                        )
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = SixpackTheme.colors.gray700,
                        )
                    }
                }
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
                        FriendSheetListItem(
                            friendItem = it,
                            onAwakeClick = {
                                onAwakeClick(it.userId)
                            },
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
