package com.dpm.sixpack.data.source.remote.util.base

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    @SerialName("status")
    val status: String,
    @SerialName("message")
    val message: String,
    @SerialName("timestamp")
    val timestamp: String,
    @SerialName("data")
    val data: T? = null,
)
