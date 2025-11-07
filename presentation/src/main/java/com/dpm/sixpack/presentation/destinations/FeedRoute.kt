package com.dpm.sixpack.presentation.destinations

import kotlinx.serialization.Serializable

sealed interface MainFeedRoute : Route

@Serializable
data class PostDetail(
    val feedId: Long,
) : MainFeedRoute

/**
 * 피드 수정/작성 화면 Route
 * @param feedId 피드 ID (0인 경우 새 피드 작성)
 */
@Serializable
data class PostEdit(
    val feedId: Long = 0L,
) : MainFeedRoute

@Serializable
data class CertifiedUsers(
    val date: String,
) : MainFeedRoute
