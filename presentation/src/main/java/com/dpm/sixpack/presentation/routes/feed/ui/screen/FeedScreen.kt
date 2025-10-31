package com.dpm.sixpack.presentation.routes.feed.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.dpm.sixpack.presentation.common.components.bottomsheet.EmojiSelectionBottomSheet
import com.dpm.sixpack.presentation.common.components.bottomsheet.ReactionUsersBottomSheet
import com.dpm.sixpack.presentation.common.components.post.FeedPostCard
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.model.Emoji
import com.dpm.sixpack.presentation.common.model.PostReaction
import com.dpm.sixpack.presentation.common.model.PostResource
import com.dpm.sixpack.presentation.common.model.PostingUserInfo
import com.dpm.sixpack.presentation.common.model.RunningSummary
import com.dpm.sixpack.presentation.common.model.UserInfo
import com.dpm.sixpack.presentation.routes.feed.component.CertificationCountView
import com.dpm.sixpack.presentation.routes.feed.component.FeedTopBar
import com.dpm.sixpack.presentation.routes.feed.component.calender.FeedWeeklyCalendar
import com.dpm.sixpack.presentation.routes.feed.contract.FeedUiState
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedCalenderUiState
import com.dpm.sixpack.presentation.theme.SixpackTheme
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate

@Composable
fun FeedScreen(
    state: FeedUiState,
    feedPagingItems: LazyPagingItems<PostResource>,
    modifier: Modifier = Modifier,
    onTopBarGroupIconClick: () -> Unit = {},
    onTopBarAlarmIconClick: () -> Unit = {},
    onDateSelected: (LocalDate) -> Unit = {},
    onVisibleWeeksChanged: (LocalDate) -> Unit = {},
    onCertifiedUsersClick: () -> Unit = {},
    onPostUserProfileClick: (Long, Boolean) -> Unit = { _, _ -> },
    onPostMenuClick: (Long) -> Unit = {},
    onPostMapImageClick: (Int) -> Unit = {},
    onPostReactionClick: (Long, Emoji) -> Unit = { _, _ -> },
    onPostReactionLongClick: (List<PostReaction>, Emoji) -> Unit = { _, _ -> },
    onPostAddReactionClick: (Long) -> Unit = {},
    onBottomSheetDismiss: () -> Unit = {},
    onBottomSheetUserProfileClick: (Long) -> Unit = {},
    onEmojiSelected: (Long, Emoji) -> Unit = { _, _ -> },
    onWeekDisplayed: (LocalDate) -> Unit = {},
) {
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        topBar = {
            FeedTopBar(
                onGroupIconClick = onTopBarGroupIconClick,
                onAlarmIconClick = onTopBarAlarmIconClick,
            )
        },
        containerColor = SixpackTheme.colors.gray0
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth(),
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                FeedWeeklyCalendar(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    feedCalenderUiState = state.calendarState,
                    onDateSelected = onDateSelected,
                    onWeekDisplayed = onWeekDisplayed
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = SixpackTheme.colors.gray50
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                CertificationCountView(
                    users = state.postingUserInfo,
                    onViewClick = onCertifiedUsersClick, modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }


            items(
                count = feedPagingItems.itemCount,
                key = { feedPagingItems.peek(it)?.feedId!!.toInt() }
            ) { index ->
                feedPagingItems[index]?.let { post ->
                    val isMenuExpanded = (state.selectedFeedId == post.feedId)
                    FeedPostCard(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        postDetail = post,
                        isMenuExpanded = isMenuExpanded,
                        onMenuClick = { onPostMenuClick(post.feedId) },
                        onReactionChipClick = onPostReactionClick,
                        onReactionChipLongClick = onPostReactionLongClick,
                        onAddReactionClick = { onPostAddReactionClick(post.feedId) }
                    )
                }
                Spacer( Modifier.height(40.dp))
            }

            feedPagingItems.loadState.apply {
                when {
                    refresh is LoadState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier.fillParentMaxSize(),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
                        }
                    }

                    append is LoadState.Loading -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
                        }
                    }

                    refresh is LoadState.Error -> {
                        item { Text(text = "Error: ${(refresh as LoadState.Error).error.message}") }
                    }

                    append is LoadState.Error -> {
                        item { Text(text = "Error: ${(append as LoadState.Error).error.message}") }
                    }
                }
            }
        }
    }


    ReactionUsersBottomSheet(
        isBottomSheetVisible = state.bottomSheetState.reactionUsers,
        onDismissRequest = onBottomSheetDismiss,
        reactionDetails = state.reactionDetailsUiState,
        onUserProfileClick = { userId -> onBottomSheetUserProfileClick(userId) },
        onTabSelected = {}
    )

    EmojiSelectionBottomSheet(
        selectedFeedId = state.selectedFeedId,
        isBottomSheetVisible = state.bottomSheetState.emojiSelection,
        onDismissRequest = onBottomSheetDismiss,
        onEmojiSelected = onEmojiSelected
    )
}

@Preview(showBackground = true)
@Composable
fun FeedScreenPreview() {
    val dummyPosts = listOf<PostResource>(
        PostResource(
            feedId = 1L,
            postImageUrl = "",
            user = PostingUserInfo(
                user = UserInfo(id = 1L, name = "김육팩", profileImageUrl = "", isMe = true),
                postingTime = "10분 전"
            ),
            runningInfo = RunningSummary(totalDistance = "5.2", totalTime = "1800"),
            reactions = listOf(PostReaction(Emoji.HEART, "10", true))
        ),
        PostResource(
            feedId = 2L,
            postImageUrl = "",
            user = PostingUserInfo(
                user = UserInfo(id = 2L, name = "박복근", profileImageUrl = "", isMe = false),
                postingTime = "30분 전"
            ),
            runningInfo = RunningSummary(totalDistance = "3.1", totalTime = "1200")
        )
    )


    // 1-2. 더미 리스트를 PagingData로 감싸고 Flow로 만듦
    val dummyPagingDataFlow = flowOf(PagingData.from(dummyPosts))

    // 1-3. Flow를 LazyPagingItems로 수집 (Composable 내부에서 수행)
    val dummyPagingItems = dummyPagingDataFlow.collectAsLazyPagingItems()

    // --- 2. 프리뷰용 UiState 생성 ---
    val dummyState = FeedUiState(
        // 캘린더는 기본값 사용
        calendarState = FeedCalenderUiState(
            postCounts = mapOf(LocalDate.now() to 2, LocalDate.now().minusDays(1) to 5)
        ),
        // 상단 인증 유저 목록
        postingUserInfo = listOf(
            PostingUserInfo(
                user = UserInfo(id = 1L, name = "김육팩", profileImageUrl = "", isMe = true),
                postingTime = ""
            ),
            PostingUserInfo(
                user = UserInfo(id = 2L, name = "박복근", profileImageUrl = "", isMe = false),
                postingTime = ""
            )
        ),
        // 1번 피드의 메뉴가 열려있도록 설정
    )

    // --- 3. 프리뷰 래퍼 및 화면 렌더링 ---
    DoRunPreviewWrapper {
        FeedScreen(
            state = dummyState,
            feedPagingItems = dummyPagingItems,

            // 모든 이벤트 핸들러는 비어있는 람다({})로 전달
            onTopBarGroupIconClick = {},
            onTopBarAlarmIconClick = {},
            onDateSelected = {},
            onVisibleWeeksChanged = {},
            onCertifiedUsersClick = {},
            onPostUserProfileClick = { _, _ -> },
            onPostMenuClick = {},
            onPostMapImageClick = {},
            onPostReactionClick = { _, _ -> },
            onPostReactionLongClick = { _, _ -> },
            onPostAddReactionClick = {},
            onBottomSheetDismiss = {},
            onBottomSheetUserProfileClick = {},
            onEmojiSelected = { _, _ -> },
            onWeekDisplayed = {}
        )
    }
}


