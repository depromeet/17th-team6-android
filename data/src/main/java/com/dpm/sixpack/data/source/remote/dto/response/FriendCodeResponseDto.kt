package com.dpm.sixpack.data.source.remote.dto.response

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class FriendCodeResponseDto(
    val code: String,
)
