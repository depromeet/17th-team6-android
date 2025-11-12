package com.dpm.sixpack.presentation.routes.friend.components

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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.model.FriendItem
import com.dpm.sixpack.presentation.theme.SixpackTheme
import kotlinx.coroutines.delay

@Composable
internal fun FriendListLazyColumn(
    modifier: Modifier = Modifier,
    showOptionForUserId: Long? = null,
    pagingItems: LazyPagingItems<FriendItem>,
    onRefresh: () -> Unit = {},
    onOptionClick: (Long) -> Unit = {}, // (FriendListItem에 맞게 수정)
    onOptionDismiss: () -> Unit = {},
    onDeleteClick: (Long) -> Unit = {},
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullToRefreshState()

    LaunchedEffect(pagingItems.loadState.refresh) {
        if (isRefreshing && pagingItems.loadState.refresh !is LoadState.Loading) {
            delay(300)
            isRefreshing = false
        }
    }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(5000)
            isRefreshing = false
        }
    }

    PullToRefreshBox(
        modifier = modifier.fillMaxSize(),
        state = pullRefreshState,
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            onRefresh()
        },
        indicator = {
            Indicator(
                state = pullRefreshState,
                isRefreshing = isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter),
                color = SixpackTheme.colors.blue600,
                containerColor = Color.White,
            )
        },
    ) {
        when (pagingItems.loadState.refresh) {
            is LoadState.Loading -> {
                if (pagingItems.itemCount == 0) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }

            is LoadState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(R.string.error_load_failed),
                        style = SixpackTheme.typography.b1Regular,
                        color = SixpackTheme.colors.gray700,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
                        onClick = {
                            pagingItems.retry()
                        },
                        colors =
                            ButtonDefaults.textButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = SixpackTheme.colors.gray700,
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
                                text = stringResource(R.string.request_retry),
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

            is LoadState.NotLoading -> {
                if (pagingItems.itemCount == 1 && pagingItems[0]?.isMe == true) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(R.string.friend_empty),
                            style = SixpackTheme.typography.t2Bold,
                            color = SixpackTheme.colors.gray900,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.friend_add_suggestion),
                            style = SixpackTheme.typography.b2Regular,
                            color = SixpackTheme.colors.gray700,
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(
                            pagingItems.itemCount,
                            key = pagingItems.itemKey { it.userId },
                        ) { index ->
                            if (index > 0) {
                                val friendItem = pagingItems[index]
                                friendItem?.let {
                                    FriendListItem(
                                        friend = it,
                                        isOptionMenuExpanded = (it.userId == showOptionForUserId),
                                        onOptionClick = { onOptionClick(it.userId) },
                                        onOptionDismiss = onOptionDismiss,
                                        onDeleteClick = { onDeleteClick(it.userId) },
                                    )
                                }
                            }
                        }

                        // (다음 페이지 로딩 스피너)
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
    }
}
