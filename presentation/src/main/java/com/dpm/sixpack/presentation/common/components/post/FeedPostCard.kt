package com.dpm.sixpack.presentation.common.components.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dpm.sixpack.presentation.common.components.preview.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.common.model.Emoji
import com.dpm.sixpack.presentation.common.model.PostReaction
import com.dpm.sixpack.presentation.common.model.PostResource
import com.dpm.sixpack.presentation.common.model.PostingUserInfo
import com.dpm.sixpack.presentation.common.model.RunningSummary
import com.dpm.sixpack.presentation.common.model.UserInfo
import com.dpm.sixpack.presentation.common.util.capture.CaptureController
import com.dpm.sixpack.presentation.common.util.capture.rememberCaptureController
import com.dpm.sixpack.presentation.common.util.toTimeAgoString
import com.dpm.sixpack.presentation.theme.SixpackTheme

/**
 * 피드에 표시되는 포스트 카드 컴포넌트
 *
 * LazyList에서 재사용 가능하도록 설계되었습니다.
 *
 * @param postDetail 포스트 상세 정보
 * @param captureController 이미지 캡처 컨트롤러 (이미지 저장용)
 * @param onReactionChipClick  반응 클릭 핸들러
 * @param onAddReactionClick 이모지 추가 버튼 클릭 핸들러
 * @param modifier 컴포저블 수정자
 */
@Composable
fun FeedPostCard(
    postDetail: PostResource,
    isMenuExpanded: Boolean,
    captureController: CaptureController,
    modifier: Modifier = Modifier,
    onPostUserProfileClick: (Long, Boolean) -> Unit = { _, _ -> },
    onPostImageClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onDropDownMenuClick: (PostDropDownActionType) -> Unit = {},
    onReactionChipClick: (Emoji, Boolean) -> Unit = { _, _ -> },
    onReactionChipLongClick: (Emoji, List<PostReaction>) -> Unit = { _, _ -> },
    onAddReactionClick: () -> Unit = {},
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(SixpackTheme.colors.gray0),
    ) {
        PostUserInfoRow(
            postingUser = postDetail.user,
            isMenuExpanded = isMenuExpanded,
            onMenuClick = onMenuClick,
            onDropDownMenuClick = onDropDownMenuClick,
            onPostUserProfileClick = { onPostUserProfileClick(postDetail.user.user.id, postDetail.user.user.isMe) },
        )

        Spacer(modifier = Modifier.height(12.dp))

        PostImageWithRecord(
            postImageUrl = postDetail.postImageUrl,
            runningSummary = postDetail.runningInfo,
            onPostImageClick = onPostImageClick,
            captureController = captureController,
        )

        Spacer(modifier = Modifier.height(12.dp))

        PostReactionRow(
            feedId = postDetail.feedId,
            reactions = postDetail.reactions,
            onReactionChipClick = onReactionChipClick,
            onReactionChipLongClick = onReactionChipLongClick,
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
    postingUser: PostingUserInfo,
    isMenuExpanded: Boolean,
    onMenuClick: () -> Unit,
    onPostUserProfileClick: () -> Unit,
    onDropDownMenuClick: (PostDropDownActionType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PostUserInfo(
            userName = postingUser.user.name,
            userImageUrl = postingUser.user.profileImageUrl,
            postingTime = postingUser.postingTime.toTimeAgoString(context),
            isMyPost = postingUser.user.isMe,
            onPostUserProfileClick = onPostUserProfileClick,
        )

        Spacer(modifier = Modifier.weight(1f))

        PostDropDownMenuIcon(
            isMyPost = postingUser.user.isMe,
            isMenuExpanded = isMenuExpanded,
            onMenuClick = { isMenuExpanded -> onMenuClick() },
            onDropDownMenuClick = onDropDownMenuClick,
        )
    }
}

@Preview
@Composable
fun FeedPostCardPreview() {
    DoRunPreviewWrapper {
        var isMenuExpanded by remember { (mutableStateOf(true)) }
        val captureController = rememberCaptureController()

        FeedPostCard(
            postDetail =
                PostResource(
                    feedId = 1,
                    postImageUrl = "",
                    user =
                        PostingUserInfo(
                            user =
                                UserInfo(
                                    name = "비락식혜",
                                    profileImageUrl = "",
                                    isMe = true,
                                ),
                            postingTime = "36분 전",
                        ),
                    runningInfo =
                        RunningSummary(
                            totalDistance = "10.09",
                            totalTime = "4440", // 1시간 14분
                            averagePace = "7'30''",
                            cadence = "144",
                            recordDateTime = "2023-08-01T00:00:00",
                        ),
                    reactions =
                        listOf(
                            PostReaction(Emoji.HEART, "10", true),
                            PostReaction(Emoji.FIRE, "5", false),
                            PostReaction(Emoji.HEART, "2", false),
                        ),
                ),
            isMenuExpanded,
            captureController,
            onMenuClick = { isMenuExpanded = !isMenuExpanded },
        )
    }
}

@Preview
@Composable
fun FeedFreindPostCardPreview() {
    DoRunPreviewWrapper {
        var isMenuExpanded by remember { (mutableStateOf(true)) }
        val captureController = rememberCaptureController()

        FeedPostCard(
            postDetail =
                PostResource(
                    feedId = 1,
                    postImageUrl = "",
                    user =
                        PostingUserInfo(
                            user =
                                UserInfo(
                                    name = "비락식혜",
                                    profileImageUrl = "",
                                    isMe = false,
                                ),
                            postingTime = "36분 전",
                        ),
                    runningInfo =
                        RunningSummary(
                            totalDistance = "10.09",
                            totalTime = "4440", // 1시간 14분
                            averagePace = "7'30''",
                            cadence = "144",
                            recordDateTime = "2023-08-01T00:00:00",
                        ),
                    reactions =
                        listOf(
                            PostReaction(Emoji.HEART, "10", true),
                            PostReaction(Emoji.FIRE, "5", false),
                            PostReaction(Emoji.HEART, "2", false),
                        ),
                ),
            isMenuExpanded,
            captureController,
            onMenuClick = { isMenuExpanded = !isMenuExpanded },
        )
    }
}
