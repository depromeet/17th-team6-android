package com.dpm.sixpack.presentation.routes.feed.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.bottomsheet.EmojiSelectionBottomSheet
import com.dpm.sixpack.presentation.common.components.bottomsheet.ReactionUsersBottomSheet
import com.dpm.sixpack.presentation.common.components.post.FeedPostCard
import com.dpm.sixpack.presentation.common.components.post.PostDropDownActionType
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
import com.dpm.sixpack.presentation.routes.feed.component.dialog.PostDeleteDialog
import com.dpm.sixpack.presentation.routes.feed.component.dialog.PostReportDialog
import com.dpm.sixpack.presentation.routes.feed.contract.FeedUiState
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedCalenderUiState
import com.dpm.sixpack.presentation.routes.feed.contract.uistate.FeedDateUiState
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
    onCertifiedUsersClick: () -> Unit = {},
    onPostUserProfileClick: (Long) -> Unit = {},
    onPostMenuClick: (Long) -> Unit = {},
    onPostImageClick: (Long) -> Unit = {},
    onPostReactionClick: (Long, Emoji) -> Unit = { _, _ -> },
    onPostReactionLongClick: (List<PostReaction>, Emoji) -> Unit = { _, _ -> },
    onPostAddReactionClick: (Long) -> Unit = {},
    onBottomSheetDismiss: () -> Unit = {},
    onBottomSheetUserProfileClick: (Long) -> Unit = {},
    onEmojiSelected: (Long, Emoji) -> Unit = { _, _ -> },
    onVisibleWeeksChanged: (LocalDate) -> Unit = {},
    onDropDownMenuClick: (PostDropDownActionType) -> Unit = {},
    onDialogDismiss: () -> Unit = {},
    onDialogConfirmClick: () -> Unit = {},
    onFTAButtonClick: () -> Unit = {}
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
        containerColor = SixpackTheme.colors.gray0,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onFTAButtonClick,
                shape = CircleShape,
                containerColor = SixpackTheme.colors.blue600,
                contentColor = SixpackTheme.colors.gray0,
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_plus),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth(),
        ) {
            item {
                FeedWeeklyCalendar(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .padding(top = 16.dp),
                    feedCalenderUiState = state.calendarState,
                    onDateSelected = onDateSelected,
                    onWeekDisplayed = onVisibleWeeksChanged
                )
            }

            item {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    thickness = 1.dp,
                    color = SixpackTheme.colors.gray50
                )
            }

            when (state.feedDateState) {
                FeedDateUiState.NoPostsAndCertifiable -> {
                    item { EmptyStateCertifiable() }
                }

                FeedDateUiState.NoPostsAndExpired -> {
                    item { EmptyStateExpired() }
                }

                FeedDateUiState.PostsAvailable -> {
                    item {
                        CertificationCountView(
                            users = state.postingUserInfo,
                            onViewClick = onCertifiedUsersClick,
                            modifier = Modifier.padding(horizontal = 20.dp)
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
                                onDropDownMenuClick = onDropDownMenuClick,
                                onPostImageClick = onPostImageClick,
                                onPostUserProfileClick = onPostUserProfileClick,
                                onReactionChipClick = onPostReactionClick,
                                onReactionChipLongClick = onPostReactionLongClick,
                                onAddReactionClick = { onPostAddReactionClick(post.feedId) }
                            )
                        }
                        Spacer(Modifier.height(40.dp))
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

    if (state.dialogState.isDeleteVisible) {
        PostDeleteDialog(
            onDismissRequest = onDialogDismiss,
            onCancelClick = onDialogDismiss,
            onConfirmClick = onDialogConfirmClick
        )
    }

    if (state.dialogState.isReportVisible) {
        PostReportDialog(
            onDismissRequest = onDialogDismiss,
            onCancelClick = onDialogDismiss,
            onConfirmClick = onDialogConfirmClick
        )
    }
}

@Composable
fun EmptyStateExpired(
    modifier: Modifier = Modifier
) {
    Spacer(Modifier.height(80.dp))

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            imageVector = Icons.Filled.Image,
            contentDescription = "인증 없음",
            modifier = Modifier.size(120.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "이 날 인증한 친구가 없어요..",
            style = SixpackTheme.typography.t2Bold,
            color = SixpackTheme.colors.gray900,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "친구들과 함께 러닝 기록을 인증해보세요!",
            style = SixpackTheme.typography.b2Regular,
            color = SixpackTheme.colors.gray700,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EmptyStateCertifiable(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            imageVector = Icons.Filled.Image, // TODO: 여기에 실제 그래픽 리소스(Painter) 삽입
            contentDescription = "인증 대기",
            modifier = Modifier.size(120.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "정말 조용하네요..!",
            style = SixpackTheme.typography.t2Bold,
            color = SixpackTheme.colors.gray900,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "지금 첫 러닝을 인증해보세요!",
            style = SixpackTheme.typography.b2Regular,
            color = SixpackTheme.colors.gray700,
            textAlign = TextAlign.Center
        )
    }
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
        )
    }
}


@Preview
@Composable
private fun FeedScreenEmptyExpiredPreview() {
    val dummyPagingDataFlow = flowOf(PagingData.empty<PostResource>())

    val dummyPagingItems = dummyPagingDataFlow.collectAsLazyPagingItems()
    DoRunPreviewWrapper {
        FeedScreen(
            state = FeedUiState(feedDateState = FeedDateUiState.NoPostsAndExpired),
            feedPagingItems = dummyPagingItems,
        )
    }
}


@Preview
@Composable
private fun FeedScreenEmptyCertifiablePreview() {
    val dummyPagingDataFlow = flowOf(PagingData.empty<PostResource>())

    val dummyPagingItems = dummyPagingDataFlow.collectAsLazyPagingItems()
    DoRunPreviewWrapper {
        FeedScreen(
            state = FeedUiState(feedDateState = FeedDateUiState.NoPostsAndCertifiable),
            feedPagingItems = dummyPagingItems,
        )
    }
}

