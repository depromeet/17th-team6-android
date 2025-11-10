package com.dpm.sixpack.presentation.routes.postdetail.ui

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.dpm.sixpack.presentation.common.components.bottomsheet.EmojiSelectionBottomSheet
import com.dpm.sixpack.presentation.common.components.bottomsheet.ReactionUsersBottomSheet
import com.dpm.sixpack.presentation.common.components.post.PostDropDownActionType
import com.dpm.sixpack.presentation.common.components.post.PostDropDownMenuIcon
import com.dpm.sixpack.presentation.common.components.post.PostImageWithRecord
import com.dpm.sixpack.presentation.common.components.post.PostReactionRow
import com.dpm.sixpack.presentation.common.components.post.PostUserInfo
import com.dpm.sixpack.presentation.common.components.topbar.DoRunNavigationTopBar
import com.dpm.sixpack.presentation.common.util.capture.CaptureController
import com.dpm.sixpack.presentation.common.util.capture.rememberCaptureController
import com.dpm.sixpack.presentation.common.util.toTimeAgoString
import com.dpm.sixpack.presentation.routes.feed.component.dialog.PostDeleteDialog
import com.dpm.sixpack.presentation.routes.feed.component.dialog.PostReportDialog
import com.dpm.sixpack.presentation.routes.postdetail.contract.PostDetailIntent
import com.dpm.sixpack.presentation.routes.postdetail.contract.PostDetailUiState
import com.dpm.sixpack.presentation.theme.SixpackTheme
import kotlinx.coroutines.launch

@Composable
fun PostDetailScreen(
    uiState: PostDetailUiState,
    onIntent: (PostDetailIntent) -> Unit,
    modifier: Modifier = Modifier,
    onSavePostImage: (Bitmap) -> Unit = {},
) {
    val captureController = rememberCaptureController()
    val postImageUrl = uiState.post?.postImageUrl ?: ""

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AsyncImage(
            model = postImageUrl,
            contentDescription =null,
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 20.dp, edgeTreatment = BlurredEdgeTreatment.Rectangle),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SixpackTheme.colors.gray900.copy(alpha = 0.6f))
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            DoRunNavigationTopBar(
                navigateToBack = { onIntent(PostDetailIntent.OnBackClick) },
                isDarkTheme = true,
            )
        },
        containerColor = Transparent,
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        if (uiState.isMenuExpanded) {
                            onIntent(PostDetailIntent.OnMenuClick(false))
                        }
                    },
        ) {
            when {
                uiState.isLoading -> {
                    LoadingContent()
                }

                uiState.error != null -> {
                    ErrorContent(
                        error = uiState.error,
                        onRetry = { onIntent(PostDetailIntent.OnRetryClick) },
                    )
                }

                uiState.post != null -> {
                    PostDetailContent(
                        uiState = uiState,
                        onIntent = onIntent,
                        onSavePostImage = onSavePostImage,
                        captureController = captureController,
                    )
                }
            }
        }
    }

    // BottomSheets
    ReactionUsersBottomSheet(
        isBottomSheetVisible = uiState.bottomSheetState.reactionUsers,
        onDismissRequest = { onIntent(PostDetailIntent.OnBottomSheetDismiss) },
        reactionDetails = uiState.reactionDetailsUiState,
        onUserProfileClick = { userId, isMe -> onIntent(PostDetailIntent.OnUserProfileClick(userId, isMe)) },
        onTabSelected = { emoji -> onIntent(PostDetailIntent.OnUserReactionSheetTabClick(emoji)) },
    )

    EmojiSelectionBottomSheet(
        isBottomSheetVisible = uiState.bottomSheetState.emojiSelection,
        onDismissRequest = { onIntent(PostDetailIntent.OnBottomSheetDismiss) },
        onEmojiSelected = { emoji -> onIntent(PostDetailIntent.OnEmojiSheetEmojiSelected(emoji)) },
    )

    // Dialogs
    if (uiState.dialogState.deleteFeedId != null) {
        PostDeleteDialog(
            onDismissRequest = { onIntent(PostDetailIntent.OnDialogDismiss) },
            onCancelClick = { onIntent(PostDetailIntent.OnDialogDismiss) },
            onConfirmClick = { onIntent(PostDetailIntent.OnDialogConfirmClick) },
        )
    }

    if (uiState.dialogState.reportFeedId != null) {
        PostReportDialog(
            onDismissRequest = { onIntent(PostDetailIntent.OnDialogDismiss) },
            onCancelClick = { onIntent(PostDetailIntent.OnDialogDismiss) },
            onConfirmClick = { onIntent(PostDetailIntent.OnDialogConfirmClick) },
        )
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            color = SixpackTheme.colors.blue500,
        )
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = error,
                style = SixpackTheme.typography.b1Medium,
                color = SixpackTheme.colors.gray600,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "다시 시도",
                style = SixpackTheme.typography.b2Medium,
                color = SixpackTheme.colors.blue500,
                modifier =
                    Modifier
                        .clickable { onRetry() }
                        .padding(8.dp),
            )
        }
    }
}

@Composable
private fun PostDetailContent(
    uiState: PostDetailUiState,
    onIntent: (PostDetailIntent) -> Unit,
    modifier: Modifier = Modifier,
    onSavePostImage: (Bitmap) -> Unit = {},
    captureController: CaptureController,
) {
    val post = uiState.post ?: return
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        Column {
            Row(
                modifier =
                    modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                PostUserInfo(
                    userImageUrl = post.user.user.profileImageUrl,
                    userName = post.user.user.name,
                    postingTime = post.user.postingTime.toTimeAgoString(context),
                    isMyPost = post.user.user.isMe,
                    onPostUserProfileClick = {
                        onIntent(PostDetailIntent.OnUserProfileClick(post.user.user.id, post.user.user.isMe))
                    },
                    isDarkTheme = true,
                )

                Spacer(modifier = Modifier.weight(1f))

                PostDropDownMenuIcon(
                    isMyPost = uiState.post.user.user.isMe,
                    isMenuExpanded = uiState.isMenuExpanded,
                    onMenuClick = { onIntent(PostDetailIntent.OnMenuClick(!uiState.isMenuExpanded)) },
                    onMenuDismiss = { onIntent(PostDetailIntent.OnMenuClick(false)) },
                    onDropDownMenuClick = { action ->
                        if (action == PostDropDownActionType.SAVE_IMAGE) {
                            coroutineScope.launch {
                                captureController.captureHighQuality()?.let { bitmap ->
                                    onSavePostImage(bitmap)
                                }
                            }
                        }
                        onIntent(PostDetailIntent.OnDropDownMenuClick(uiState.post, action))
                    },
                    isDarkTheme = true,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PostImageWithRecord(
                postImageUrl = post.postImageUrl,
                runningSummary = post.runningInfo,
                onPostImageClick = {},
                captureController = captureController,
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Spacer(modifier = Modifier.height(12.dp))

            PostReactionRow(
                modifier = Modifier.padding(horizontal = 20.dp),
                feedId = post.feedId,
                reactions = post.reactions,
                onReactionChipClick = { emoji, isReacted ->
                    onIntent(PostDetailIntent.OnPostReactionClick(post, emoji, isReacted))
                },
                onReactionChipLongClick = { emoji, reactions ->
                    onIntent(PostDetailIntent.OnPostReactionLongClick(reactions, emoji))
                },
                onAddReactionClick = {
                    onIntent(PostDetailIntent.OnAddReactionClick(post))
                },
            )
        }
    }
}
