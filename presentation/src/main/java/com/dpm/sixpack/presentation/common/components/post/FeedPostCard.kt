package com.dpm.sixpack.presentation.common.components.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.model.Emoji
import com.dpm.sixpack.presentation.common.model.PostDetailUiState
import com.dpm.sixpack.presentation.common.model.PostReactionUiState
import com.dpm.sixpack.presentation.common.model.RunningSummaryUiState
import com.dpm.sixpack.presentation.theme.SixpackTheme

/**
 * 피드에 표시되는 포스트 카드 컴포넌트
 *
 * LazyList에서 재사용 가능하도록 설계되었습니다.
 *
 * @param postDetail 포스트 상세 정보
 * @param currentUserName 현재 사용자 이름 (본인 포스트 표시용)
 * @param onMoreClick 더보기 버튼 클릭 핸들러
 * @param onReactionClick 이모지 반응 클릭 핸들러
 * @param onAddReactionClick 이모지 추가 버튼 클릭 핸들러
 * @param modifier 컴포저블 수정자
 */
@Composable
fun FeedPostCard(
    postDetail: PostDetailUiState,
    currentUserName: String,
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit = {},
    onReactionClick: (String) -> Unit = {},
    onAddReactionClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SixpackTheme.colors.gray0)
    ) {
        // TODO SB postTime util로 변환한 변수 넣기
        PostUserInfoRow(
            userImageUrl = postDetail.userImageUrl,
            userName = postDetail.userName,
            postTime = postDetail.postTime,
            isMyPost = postDetail.userName == currentUserName,
            onMenuClick = onMenuClick,
        )

        Spacer(modifier = Modifier.height(12.dp))

        // TODO SB postTime util로 변환한 변수 넣기
        PostImageWithRecord(
            postTime = postDetail.postTime,
            postImageUrl = postDetail.postImageUrl,
            runningSummary = postDetail.runningInfo,
        )

        Spacer(modifier = Modifier.height(12.dp))

        PostReactionRow(
            reactions = postDetail.reactions,
            onReactionClick = onReactionClick,
            onAddReactionClick = onAddReactionClick,
        )
    }
}

/**
 * 포스트 상단의 사용자 정보를 표시하는 컴포넌트 (메뉴 버튼 포함)
 * FeedPostCard와 같이 메뉴 버튼이 필요한 곳에서 사용됩니다.
 */
@Composable
private fun PostUserInfoRow(
    userImageUrl: String,
    userName: String,
    postTime: String,
    isMyPost: Boolean,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PostUserInfo(
            userImageUrl = userImageUrl,
            userName = userName,
            postTime = postTime,
            isMyPost = isMyPost,
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = onMenuClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_meatball_menu),
                contentDescription = "Post options menu",
                tint = SixpackTheme.colors.gray600
            )
        }
    }
}

@Preview
@Composable
fun FeedPostCardPreview() {
    DoRunPreviewWrapper {
        FeedPostCard(
            postDetail = PostDetailUiState(
                feedId = 1,
                date = "2025-10-15",
                userName = "비락식혜",
                userImageUrl = "",
                postTime = "36분 전",
                postImageUrl = "https://example.com/map.jpg",
                runningInfo = RunningSummaryUiState(
                    totalDistance = 10.09,
                    totalRunTime = 4440, // 1시간 14분
                    averagePace = "7'30''",
                    cadence = 144
                ),
                reactions = listOf(
                    PostReactionUiState(Emoji.HEART, 10, true),
                    PostReactionUiState(Emoji.FIRE, 5, false),
                    PostReactionUiState(Emoji.SHOOT, 2, false)
                )
            ),
            currentUserName = "비락식혜"
        )
    }
}
