package com.dpm.sixpack.presentation.common.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class RunningSummaryUiState(
    val totalDistance: String,
    val totalTime: String,
    val averagePace: String,
    val cadence: String,
    val recordDateTime: String,
) : Parcelable
