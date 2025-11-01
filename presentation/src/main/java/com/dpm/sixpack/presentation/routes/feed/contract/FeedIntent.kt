package com.dpm.sixpack.presentation.routes.feed.contract

import com.dpm.sixpack.presentation.common.base.UiIntent
import com.dpm.sixpack.presentation.common.components.post.PostDropDownActionType
import com.dpm.sixpack.presentation.common.model.Emoji
import com.dpm.sixpack.presentation.common.model.PostReaction
import java.time.LocalDate

sealed interface FeedIntent : UiIntent {
    // TopBar
    data object OnTopBarGroupIconClick : FeedIntent
    data object OnTopBarAlarmIconClick : FeedIntent

    // Calendar
    data class OnDateSelected(val date: LocalDate) : FeedIntent
    data class OnVisibleWeeksChanged(val startDate: LocalDate) : FeedIntent

    // Certified Users
    data object OnCertifiedUsersClick : FeedIntent

    // Post Card
    data class OnPostUserProfileClick(val userId: Long) : FeedIntent
    data class OnPostMenuClick(val feedId: Long) : FeedIntent
    data class OnPostImageClick(val feedId: Long) : FeedIntent
    data class OnPostReactionClick(val feedId: Long, val emoji: Emoji) : FeedIntent
    data class OnPostReactionLongClick(val feedId: Long, val emoji: Emoji, val reactions: List<PostReaction>) : FeedIntent
    data class OnPostAddReactionClick(val feedId: Long) : FeedIntent
    data class OnDropDownMenuClick(val feedId: Long, val action: PostDropDownActionType) : FeedIntent

    // BottomSheet
    data object OnBottomSheetDismiss : FeedIntent
    data class OnBottomSheetUserProfileClick(val userId: Long) : FeedIntent
    data class OnEmojiSelected(val feedId: Long, val emoji: Emoji) : FeedIntent

    // Dialog
    data object OnDialogDismiss : FeedIntent
    data object OnDialogConfirmClick : FeedIntent

    // FloatingActionButton
    data object OnFloatingActionButtonClick : FeedIntent
}
