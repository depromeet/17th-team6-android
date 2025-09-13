package com.dpm.sixpack.data.source.remote.dto.response

import android.annotation.SuppressLint
import com.dpm.sixpack.domain.model.RunningGoal
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
    @SerialName("title")
    val title: String,
    @SerialName("startedAt")
    val startedAt: String,
    @SerialName("endedAt")
    val endedAt: String,
    @SerialName("clearedAt")
    val clearedAt: String? = null,
    @SerialName("pace")
    val pace: Int,
    @SerialName("distance")
    val distance: Int,
    @SerialName("duration")
    val duration: Int,
    @SerialName("repeatType")
    val repeatType: String,
    @SerialName("repeatFrequency")
    val repeatFrequency: Int
){
    fun toRunningGoal() : RunningGoal = RunningGoal(
        id =  id,
        title = title,
        distance = distance,
        duration = duration,
        pace = pace,

    )
}

