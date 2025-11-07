package com.dpm.sixpack.presentation.destinations

import kotlinx.serialization.Serializable

// "친구 기능" 그래프 전체를 대표하는 '단일' 라우트
sealed interface FriendRoute : Route

@Serializable
data object Friend : FriendRoute

// 그래프 내부에 속한 '개별' 화면들\
@Serializable
data object FriendListRoute : FriendRoute

@Serializable
data object AddFriendRoute : FriendRoute

/**
 * 친구 프로필 화면 Route
 * @param friendId 친구 ID
 */
@Serializable
data class FriendProfile(
    val friendId: Long,
) : FriendRoute
