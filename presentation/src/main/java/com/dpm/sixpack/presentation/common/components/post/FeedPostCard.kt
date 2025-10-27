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
import com.dpm.sixpack.presentation.common.model.PostingUserUiState
import com.dpm.sixpack.presentation.common.model.RunningSummaryUiState
import com.dpm.sixpack.presentation.theme.SixpackTheme

/**
 * 피드에 표시되는 포스트 카드 컴포넌트
 *
 * LazyList에서 재사용 가능하도록 설계되었습니다.
 *
 * @param postDetail 포스트 상세 정보
 * @param currentUserName 현재 사용자 이름 (본인 포스트 표시용)
 * @param onReactionClick  반응 클릭 핸들러
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
            postingUser = postDetail.user,
            onMenuClick = onMenuClick,
        )

        Spacer(modifier = Modifier.height(12.dp))

        // TODO SB postTime util로 변환한 변수 넣기
        PostImageWithRecord(
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
    postingUser: PostingUserUiState,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PostUserInfo(
            userImageUrl = postingUser.userImageUrl,
            userName = postingUser.userName,
            postingTime = postingUser.postingTime,
            isMyPost = postingUser.isMe,
            // TODO SB typography 적용
            // textStyle = SixpackTheme.typography.body2,
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
                postImageUrl = "",
                user = PostingUserUiState(
                    userName = "비락식혜",
                    userImageUrl = "",
                    postingTime = "36분 전",
                    isMe = true
                ),
                runningInfo = RunningSummaryUiState(
                    totalDistance = "10.09",
                    totalTime = "4440", // 1시간 14분
                    averagePace = "7'30''",
                    cadence = "144",
                    recordDateTime = "2023-08-01T00:00:00"
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
