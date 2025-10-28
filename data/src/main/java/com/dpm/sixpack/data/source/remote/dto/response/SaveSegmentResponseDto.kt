package com.dpm.sixpack.data.source.remote.dto.response

import com.dpm.sixpack.domain.usecase.SaveRealtimeRunningDataResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SaveSegmentResponseDto(
    @SerialName("segmentId")
    val segmentId: Long,
    @SerialName("savedCount")
    val savedCount: Int,
) {
    fun toSyncResult() =
        SaveRealtimeRunningDataResult.SyncResult(
            segmentId = segmentId,
            savedCount = savedCount,
        )
}
