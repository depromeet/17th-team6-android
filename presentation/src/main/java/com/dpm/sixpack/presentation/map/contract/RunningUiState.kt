package com.dpm.sixpack.presentation.map.contract

import android.os.Parcelable
import com.dpm.sixpack.domain.model.RunningState
import kotlinx.parcelize.Parcelize

@Parcelize
data class RunningUiState(
    val duration: Long = 0L,
    val distance: Double = 0.0,
    val paceInMoment: Double = 0.0,
    val paceAverage: Double = 0.0,
    val cadence: Int = 0
) : Parcelable

fun RunningUiState.toRunningState(): RunningState = RunningState(
    duration = duration,
    distance = distance,
    paceInMoment = paceInMoment,
    paceAverage = paceAverage,
    cadence = cadence
)

fun RunningState.toRunningUiState(): RunningUiState = RunningUiState(
    duration = duration,
    distance = distance,
    paceInMoment = paceInMoment,
    paceAverage = paceAverage,
    cadence = cadence
)
