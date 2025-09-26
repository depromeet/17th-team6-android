package com.dpm.sixpack.presentation.routes.session.contract.uistate

import android.os.Parcelable
import com.dpm.sixpack.domain.model.session.RunningSessionGoal
import kotlinx.parcelize.Parcelize

@Parcelize
data class RunningGoalUiState(
    val id: Long? = null,
    val distanceMeter: Int = 0, // 목표 거리 m
    val recommendedTimeMinutes: Int = 0, // 목표 시간
    val recommendedPaceSeconds: Int = 0, // 목표 페이스
    val roundCount: Int = 1,
    val totalRoundCount: Int = 20,
    val warmUpMinutes: Int = 5,
    val coolDownMinutes: Int = 5,
) : Parcelable

fun RunningSessionGoal.toUiState() =
    RunningGoalUiState(
        id = id,
        distanceMeter = distance,
        recommendedTimeMinutes = duration / 60,
        recommendedPaceSeconds = pace,
        roundCount = roundCount,
        totalRoundCount = totalRoundCount ?: 20,
    )
