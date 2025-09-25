package com.dpm.sixpack.data.source.remote.dto.response

import android.annotation.SuppressLint
import com.dpm.sixpack.domain.model.total.RunningTotalGoal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class TodayGoalResponseDto(
    @SerialName("id")
    val id: Long,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("updatedAt")
    val updatedAt: String,
    @SerialName("pausedAt")
    val pausedAt: String? = null,
    @SerialName("clearedAt")
    val clearedAt: String? = null,
    @SerialName("title")
    val title: String,
    @SerialName("subTitle")
    val subTitle: String? = null,
    @SerialName("type")
    val type: String,
    @SerialName("pace")
    val pace: Int,
    @SerialName("distance")
    val distance: Int,
    @SerialName("duration")
    val duration: Int,
    @SerialName("totalRoundCount")
    val totalRoundCount: Int,
    @SerialName("clearedRoundCount")
    val clearedRoundCount: Int,
) {

    fun toRunningTotalGoal(): RunningTotalGoal =
        RunningTotalGoal(
            id = id,
            createdAt = createdAt,
            updatedAt = updatedAt,
            pausedAt = pausedAt,
            clearedAt = clearedAt,
            title = title,
            subTitle = subTitle.orEmpty(),
            type = type,
            pace = pace,
            distance = distance,
            duration = duration,
            totalRoundCount = totalRoundCount,
            clearedRoundCount = clearedRoundCount
        )
}
