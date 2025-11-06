package com.dpm.sixpack.presentation.destinations

import kotlinx.serialization.Serializable

// "친구 기능" 그래프 전체를 대표하는 '단일' 라우트
@Serializable
data object Friend : Route

// 그래프 내부에 속한 '개별' 화면들
@Serializable
sealed interface FriendRoute : Route {
    @Serializable
    data object FriendListRoute : FriendRoute

    @Serializable
    data object AddFriendRoute : FriendRoute
}
