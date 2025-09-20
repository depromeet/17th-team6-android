package com.dpm.sixpack.presentation.routes.session.contract.uistate

import android.os.Parcelable
import com.dpm.sixpack.domain.model.RunningSessionGoal
import com.dpm.sixpack.presentation.common.util.formatDistanceToKm
import com.dpm.sixpack.presentation.common.util.formatSecondsToPace
import com.dpm.sixpack.presentation.common.util.formatSecondsToTime
import kotlinx.parcelize.Parcelize

@Parcelize
data class RunningGoalUiState(
    val sessionNumber: Int? = null,
    val mainRunningDistance: String = "", // 목표 거리
    val mainRunningDuration: String = "", // 목표 시간
    val mainRunningPace: String = "", // 목표 페이스
    val warmUpDuration: String = "", // 준비운동 시간
    val coolDownDuration: String = "", // 정리운동 시간
) : Parcelable

fun RunningSessionGoal.toUiState() = RunningGoalUiState(
    sessionNumber = sessionNumber,
    mainRunningDistance = formatDistanceToKm(mainRunningDistance),
    mainRunningDuration = formatSecondsToTime(mainRunningDuration),
    mainRunningPace = formatSecondsToPace(mainRunningPace),
    warmUpDuration = formatSecondsToTime(mainRunningDuration),
    coolDownDuration = formatSecondsToTime(coolDownDuration),
)

