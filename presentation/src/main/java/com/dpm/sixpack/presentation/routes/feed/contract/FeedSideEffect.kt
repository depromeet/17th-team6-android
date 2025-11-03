package com.dpm.sixpack.presentation.routes.feed.contract

import com.dpm.sixpack.presentation.common.base.SideEffect
import com.dpm.sixpack.presentation.common.model.PostResource
import java.time.LocalDate

sealed interface FeedSideEffect : SideEffect {
    data object NavigateToFriend : FeedSideEffect

    data object NavigateToAlarm : FeedSideEffect

    data class NavigateToCertificationFriend(
        val date: String,
    ) : FeedSideEffect

    data object NavigateToMyPage : FeedSideEffect

    data class NavigateToUserPage(
        val userId: Long,
    ) : FeedSideEffect

    data class NavigateToPostDetail(
        val post: PostResource,
    ) : FeedSideEffect

    data class NavigateToPostUpload(
        val date: LocalDate,
    ) : FeedSideEffect

    data class NavigateToPostEdit(
        val post: PostResource,
    ) : FeedSideEffect

    data class ShowToast(
        val message: String,
    ) : FeedSideEffect

    data object RefreshPagingList : FeedSideEffect
}
