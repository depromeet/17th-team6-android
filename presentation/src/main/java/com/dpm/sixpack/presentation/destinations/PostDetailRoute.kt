package com.dpm.sixpack.presentation.destinations

import kotlinx.serialization.Serializable

@Serializable
data class PostDetail(
    val feedId: Long,
) : MainFeedRoute
