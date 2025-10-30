package com.dpm.sixpack.data.source.remote.dto.response

import android.annotation.SuppressLint
import com.dpm.sixpack.domain.usecase.SaveRealtimeRunningDataResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class SaveSegmentResponseDto(
    @SerialName("segmentId")
    val segmentId: Long,
    @SerialName("savedCount")
    val savedCount: Int,
)
