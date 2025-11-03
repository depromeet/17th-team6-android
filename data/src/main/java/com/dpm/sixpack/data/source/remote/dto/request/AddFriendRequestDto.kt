package com.dpm.sixpack.data.source.remote.dto.request

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class AddFriendRequestDto(
    val code: String,
)
