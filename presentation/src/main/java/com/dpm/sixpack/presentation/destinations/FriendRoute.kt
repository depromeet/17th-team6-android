package com.dpm.sixpack.presentation.destinations

import kotlinx.serialization.Serializable

/**
 * Friend 관련 Route 정의
 */
sealed interface FriendRoute : Route

/**
 * 친구 프로필 화면 Route
 * @param friendId 친구 ID
 */
@Serializable
data class FriendProfile(
    val friendId: Long,
) : FriendRoute

/**
 * 친구 추가 화면 Route
 */
@Serializable
data object FriendAdd : FriendRoute
