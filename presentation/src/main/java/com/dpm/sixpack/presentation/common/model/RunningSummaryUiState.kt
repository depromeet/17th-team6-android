package com.dpm.sixpack.presentation.common.model

import androidx.compose.runtime.Immutable

@Immutable
data class RunningSummaryUiState(
    val totalDistance: String,
    val totalTime: String,
    val averagePace: String,
    val cadence: String,
    val recordDateTime: String,
)
