package com.dpm.sixpack.presentation.routes.feed.contract

import com.dpm.sixpack.presentation.common.base.SideEffect
import com.dpm.sixpack.presentation.common.model.PostResource
import com.dpm.sixpack.presentation.common.model.PostingUserInfo

sealed interface FeedSideEffect : SideEffect {
    data class ShowToast(val message: String) : FeedSideEffect

    data object NavigateToFriend : FeedSideEffect

    data object NavigateToMyPage : FeedSideEffect

    data object NavigateToAlarm : FeedSideEffect

    data object NavigateToPostUpload : FeedSideEffect

    data class NavigateToCertificationFriend(val postingUsers: List<PostingUserInfo>) : FeedSideEffect

    data class NavigateToUserProfile(val userId: Long) : FeedSideEffect

    data class NavigateToPostDetail(val post: PostResource) : FeedSideEffect

}
