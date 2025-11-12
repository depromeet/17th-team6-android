package com.dpm.sixpack.presentation.routes.friendprofile.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

/**
 * 친구 프로필 화면의 사이드 이펙트
 */
sealed interface FriendProfileSideEffect : SideEffect {
    /**
     * 뒤로가기
     */
    data object NavigateBack : FriendProfileSideEffect

    /**
     * 포스트 상세 화면으로 이동
     */
    data class NavigateToPostDetail(
        val postId: Long,
    ) : FriendProfileSideEffect
}
