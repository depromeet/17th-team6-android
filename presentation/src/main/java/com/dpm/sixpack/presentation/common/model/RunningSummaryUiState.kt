package com.dpm.sixpack.presentation.common.model

import androidx.compose.runtime.Immutable

@Immutable
data class RunningSummaryUiState(
    val totalDistance: Double,
    val totalRunTime: Int,
    val averagePace: String,
    val cadence: Int
)
