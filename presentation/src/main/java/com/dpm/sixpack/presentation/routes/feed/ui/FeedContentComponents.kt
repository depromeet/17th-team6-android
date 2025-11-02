package com.dpm.sixpack.presentation.routes.feed.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.post.FeedPostCard
import com.dpm.sixpack.presentation.common.components.post.PostDropDownActionType
import com.dpm.sixpack.presentation.common.model.Emoji
import com.dpm.sixpack.presentation.common.model.PostReaction
import com.dpm.sixpack.presentation.common.model.PostResource
import com.dpm.sixpack.presentation.common.model.PostingUserInfo
import com.dpm.sixpack.presentation.routes.feed.component.CertificationCountView
import com.dpm.sixpack.presentation.routes.feed.component.FeedPostCardShimmer
import com.dpm.sixpack.presentation.routes.feed.component.calender.FeedWeeklyCalendar
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedCalenderUiState
import com.dpm.sixpack.presentation.theme.SixpackTheme
import java.time.LocalDate

/**
 * FeedScreen의 LazyColumn Content를 관리하는 확장 함수들
 * Recomposition 최소화를 위해 LazyListScope 확장으로 구현
 */

/**
 * Refresh Loading Indicator - 사용자가 새로고침할 때 표시
 */
internal fun LazyListScope.feedRefreshLoadingItem(isRefreshLoading: Boolean) {
    if (isRefreshLoading) {
        item(key = "refresh_loading") {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = SixpackTheme.colors.blue600,
                )
            }
        }
    }
}

/**
 * 주간 캘린더 아이템
 */
internal fun LazyListScope.feedCalendarItem(
    calendarState: FeedCalenderUiState,
    onDateSelected: (LocalDate) -> Unit,
    onWeekDisplayed: (LocalDate) -> Unit,
) {
    item(key = "calendar") {
        FeedWeeklyCalendar(
            modifier =
                Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp),
            feedCalenderUiState = calendarState,
            onDateSelected = onDateSelected,
            onWeekDisplayed = onWeekDisplayed,
        )
    }
}

/**
 * Divider 아이템
 */
internal fun LazyListScope.feedDividerItem() {
    item(key = "divider") {
        HorizontalDivider(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
            thickness = 1.dp,
            color = SixpackTheme.colors.gray50,
        )
    }
}

/**
 * Initial Loading Shimmer - 첫 로딩 시 Shimmer 표시
 */
internal fun LazyListScope.feedInitialLoadingItems() {
    items(
        count = 3,
        key = { index -> "shimmer_$index" },
    ) {
        FeedPostCardShimmer(
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(40.dp))
    }
}

/**
 * 인증 유저 카운트 뷰
 */
internal fun LazyListScope.feedCertificationCountItem(
    postingUserInfo: List<PostingUserInfo>,
    onCertifiedUsersClick: () -> Unit,
) {
    item(key = "certification_count") {
        CertificationCountView(
            users = postingUserInfo,
            onViewClick = onCertifiedUsersClick,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
    }
}

/**
 * 피드 포스트 아이템들
 */
internal fun LazyListScope.feedPostItems(
    feedPagingItems: LazyPagingItems<PostResource>,
    selectedPostMenuId: Long?,
    onPostMenuClick: (Long) -> Unit,
    onDropDownMenuClick: (PostResource, PostDropDownActionType) -> Unit,
    onPostImageClick: (PostResource) -> Unit,
    onPostUserProfileClick: (Long, Boolean) -> Unit,
    onReactionClick: (PostResource, Emoji, Boolean) -> Unit,
    onReactionLongClick: (Long, List<PostReaction>, Emoji) -> Unit,
    onAddReactionClick: (PostResource) -> Unit,
) {
    items(
        count = feedPagingItems.itemCount,
        key = { index ->
            feedPagingItems.peek(index)?.feedId ?: index.toLong()
        },
    ) { index ->
        feedPagingItems[index]?.let { post ->
            val isMenuExpanded =
                remember(selectedPostMenuId, post.feedId) {
                    selectedPostMenuId != null && selectedPostMenuId == post.feedId
                }

            FeedPostCard(
                modifier = Modifier.padding(horizontal = 20.dp),
                postDetail = post,
                isMenuExpanded = isMenuExpanded,
                onMenuClick = { onPostMenuClick(post.feedId) },
                onDropDownMenuClick = { action -> onDropDownMenuClick(post, action) },
                onPostImageClick = { onPostImageClick(post) },
                onPostUserProfileClick = { userId, isMe ->
                    onPostUserProfileClick(userId, isMe)
                },
                onReactionChipClick = { emoji, isReacted ->
                    onReactionClick(post, emoji, isReacted)
                },
                onReactionChipLongClick = { emoji, reactions ->
                    onReactionLongClick(post.feedId, reactions, emoji)
                },
                onAddReactionClick = { onAddReactionClick(post) },
            )
        }
        Spacer(Modifier.height(40.dp))
    }
}

/**
 * LoadState 처리 - Append Loading, Error
 */
internal fun LazyListScope.feedLoadStateItems(feedPagingItems: LazyPagingItems<PostResource>) {
    when {
        // Append Loading - 하단 로딩
        feedPagingItems.loadState.append is LoadState.Loading -> {
            item(key = "append_loading") {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = SixpackTheme.colors.blue600,
                    )
                }
            }
        }

        // Append Error
        feedPagingItems.loadState.append is LoadState.Error -> {
            item(key = "append_error") {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(id = R.string.feed_load_state_retry_button),
                        style = SixpackTheme.typography.b2Regular,
                        color = SixpackTheme.colors.blue600,
                        modifier = Modifier.clickable { feedPagingItems.retry() },
                    )
                }
            }
        }

        // Refresh Error (첫 로딩 실패)
        feedPagingItems.loadState.refresh is LoadState.Error && feedPagingItems.itemCount == 0 -> {
            item(key = "refresh_error") {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 80.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text =
                            stringResource(
                                id = R.string.feed_load_state_error_message,
                                (feedPagingItems.loadState.refresh as LoadState.Error).error.message
                                    ?: "",
                            ),
                        style = SixpackTheme.typography.b1Regular,
                        color = SixpackTheme.colors.gray700,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = { feedPagingItems.retry() }) {
                        Text(stringResource(id = R.string.feed_load_state_retry_button))
                    }
                }
            }
        }
    }
}

/**
 * FeedContent 전체 구성을 담당하는 확장 함수
 * PostsAvailable 상태일 때의 모든 content를 처리
 */
internal fun LazyListScope.feedContentItems(
    isInitialLoad: Boolean,
    postingUserInfo: List<PostingUserInfo>,
    feedPagingItems: LazyPagingItems<PostResource>,
    selectedPostMenuId: Long?,
    onCertifiedUsersClick: () -> Unit,
    onPostMenuClick: (Long) -> Unit,
    onDropDownMenuClick: (PostResource, PostDropDownActionType) -> Unit,
    onPostImageClick: (PostResource) -> Unit,
    onPostUserProfileClick: (Long, Boolean) -> Unit,
    onReactionClick: (PostResource, Emoji, Boolean) -> Unit,
    onReactionLongClick: (Long, List<PostReaction>, Emoji) -> Unit,
    onAddReactionClick: (PostResource) -> Unit,
) {
    if (isInitialLoad) {
        feedInitialLoadingItems()
    } else {
        // 실제 컨텐츠
        feedCertificationCountItem(postingUserInfo, onCertifiedUsersClick)

        item(key = "spacer_after_count") {
            Spacer(modifier = Modifier.height(32.dp))
        }

        feedPostItems(
            feedPagingItems = feedPagingItems,
            selectedPostMenuId = selectedPostMenuId,
            onPostMenuClick = onPostMenuClick,
            onDropDownMenuClick = onDropDownMenuClick,
            onPostImageClick = onPostImageClick,
            onPostUserProfileClick = onPostUserProfileClick,
            onReactionClick = onReactionClick,
            onReactionLongClick = onReactionLongClick,
            onAddReactionClick = onAddReactionClick,
        )

        // LoadState 처리
        feedLoadStateItems(feedPagingItems)
    }
}
