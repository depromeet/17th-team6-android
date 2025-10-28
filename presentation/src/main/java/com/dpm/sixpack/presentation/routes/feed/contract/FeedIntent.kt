package com.dpm.sixpack.presentation.routes.feed.contract

import com.dpm.sixpack.presentation.common.base.UiIntent
import com.dpm.sixpack.presentation.common.model.Emoji
import java.time.LocalDate

sealed interface FeedIntent : UiIntent {
    data object OnTopBarGroupIconClick : FeedIntent
    data object OnTopBarAlarmIconClick : FeedIntent
    data class OnDateSelected(val date: LocalDate) : FeedIntent
    data class OnVisibleWeeksChanged(val startDate: LocalDate) : FeedIntent
    data object OnCertifiedUsersClick : FeedIntent
    data class OnPostUserProfileClick(val userId: Int, val isMe: Boolean) : FeedIntent
    data class OnPostMenuClick(val feedId: Int) : FeedIntent
    data class OnPostMapImageClick(val feedId: Int) : FeedIntent
    data class OnPostReactionClick(val feedId: Int, val emoji: Emoji) : FeedIntent
    data class OnPostReactionLongClick(val feedId: Int, val emojiType: String) : FeedIntent
    data class OnPostAddReactionClick(val feedId: Int) : FeedIntent
    data object OnBottomSheetDismiss : FeedIntent
    data class OnBottomSheetUserProfileClick(val userId: Int) : FeedIntent
    data class OnEmojiSelected(val emoji: Emoji) : FeedIntent
}
