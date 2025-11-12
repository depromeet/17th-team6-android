package com.dpm.sixpack.presentation.routes.friendprofile.contract

import com.dpm.sixpack.presentation.common.base.UiIntent

/**
 * 친구 프로필 화면의 사용자 의도
 */
sealed interface FriendProfileIntent : UiIntent {
    /**
     * 뒤로가기 버튼 클릭 시
     */
    data object OnBackClick : FriendProfileIntent

    /**
     * 포스트 클릭 시
     */
    data class OnPostClick(
        val postId: Long,
    ) : FriendProfileIntent

    /**
     * 에러 재시도 버튼 클릭 시
     */
    data object OnRetryClick : FriendProfileIntent
}
