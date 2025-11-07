package com.dpm.sixpack.presentation.destinations

import com.dpm.sixpack.presentation.common.model.RunningSummary
import kotlinx.serialization.Serializable

@Serializable
data class PostUpload(
    val sessionId: Long,
    val mapImageUrl: String,
    val runningSummary: RunningSummary,
)
