package com.dpm.sixpack.presentation.routes.feed.contract

import com.dpm.sixpack.presentation.common.base.UiIntent
import com.dpm.sixpack.presentation.common.components.post.PostDropDownActionType
import com.dpm.sixpack.presentation.common.model.Emoji
import com.dpm.sixpack.presentation.common.model.PostReaction
import com.dpm.sixpack.presentation.common.model.PostResource
import java.time.LocalDate

sealed interface FeedIntent : UiIntent {
    // TopBar
    data object OnTopBarGroupIconClick : FeedIntent

    data object OnTopBarAlarmIconClick : FeedIntent

    // Calendar
    data class OnDateSelected(
        val date: LocalDate,
    ) : FeedIntent

    // Certified Users
    data object OnCertifiedUsersClick : FeedIntent

    // User 프로필 클릭
    data class OnUserProfileClick(
        val userId: Long,
        val isMe: Boolean,
    ) : FeedIntent

    // Post Card
    data class OnPostMenuClick(
        val feedId: Long,
    ) : FeedIntent

    data class OnPostImageClick(
        val post: PostResource,
    ) : FeedIntent

    data class OnPostReactionClick(
        val post: PostResource,
        val emoji: Emoji,
        val isReacted: Boolean,
    ) : FeedIntent

    data class OnPostReactionLongClick(
        val feedId: Long,
        val reactions: List<PostReaction>,
        val selectedEmoji: Emoji,
    ) : FeedIntent

    data class OnPostAddReactionClick(
        val post: PostResource,
    ) : FeedIntent

    data class OnDropDownMenuClick(
        val post: PostResource,
        val action: PostDropDownActionType,
    ) : FeedIntent

    // BottomSheet
    data object OnBottomSheetDismiss : FeedIntent

    data class OnUserReactionSheetTabClick(
        val selectedEmoji: Emoji,
    ) : FeedIntent

    data class OnEmojiSheetEmojiSelected(
        val emoji: Emoji,
    ) : FeedIntent

    // Dialog
    data object OnDialogDismiss : FeedIntent

    data object OnDialogConfirmClick : FeedIntent

    // FloatingActionButton
    data object OnFloatingActionButtonClick : FeedIntent

    sealed interface Observed : FeedIntent {
        data class VisibleWeeksChanged(
            val startDate: LocalDate,
        ) : Observed

        data object PagingDataEmpty : Observed
    }
}
