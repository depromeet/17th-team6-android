package com.dpm.sixpack.presentation.destinations

import com.dpm.sixpack.presentation.common.model.RunningSummary
import kotlinx.serialization.Serializable

sealed interface MainFeedRoute : Route

@Serializable
data class PostDetail(
    val feedId: Long,
) : MainFeedRoute

@Serializable
data class PostEdit(
    val feedId: Long,
) : MainFeedRoute

@Serializable
data class PostUpload(
    val sessionId: Long,
    val mapImageUrl: String,
    val runningSummary: RunningSummary,
) : MainFeedRoute

@Serializable
data class CertifiedUsers(
    val date: String,
) : MainFeedRoute
