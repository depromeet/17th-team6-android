package com.dpm.sixpack.presentation.routes.postdetail.contract

import com.dpm.sixpack.presentation.common.base.UiIntent
import com.dpm.sixpack.presentation.common.components.post.PostDropDownActionType
import com.dpm.sixpack.presentation.common.model.Emoji
import com.dpm.sixpack.presentation.common.model.PostReaction
import com.dpm.sixpack.presentation.common.model.PostResource

sealed interface PostDetailIntent : UiIntent {
    // TopBar
    data object OnBackClick : PostDetailIntent

    data class OnMenuClick(val isExpanded : Boolean) : PostDetailIntent
    // Post Actions
    data class OnUserProfileClick(
        val userId: Long,
        val isMe: Boolean,
    ) : PostDetailIntent

    data class OnPostReactionClick(
        val post: PostResource,
        val emoji: Emoji,
        val isReacted: Boolean,
    ) : PostDetailIntent

    data class OnPostReactionLongClick(
        val feedId: Long,
        val reactions: List<PostReaction>,
        val selectedEmoji: Emoji,
    ) : PostDetailIntent

    data class OnAddReactionClick(
        val post: PostResource,
    ) : PostDetailIntent

    data class OnDropDownMenuClick(
        val post: PostResource,
        val action: PostDropDownActionType,
    ) : PostDetailIntent

    // Bottom Sheet
    data object OnBottomSheetDismiss : PostDetailIntent

    data class OnUserReactionSheetTabClick(
        val selectedEmoji: Emoji,
    ) : PostDetailIntent

    data class OnEmojiSheetEmojiSelected(
        val emoji: Emoji,
    ) : PostDetailIntent

    // Dialog
    data object OnDialogDismiss : PostDetailIntent

    data object OnDialogConfirmClick : PostDetailIntent
    // Retry
    data object OnRetryClick : PostDetailIntent
}
