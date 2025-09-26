package com.dpm.sixpack.presentation.routes.session.contract.uistate

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecordUiState(
    val currentDistance: Int = 0,
    // 00:32:10
    val currentDuration: Int = 0,
    // 5'30"
    val avgPace: Int = 0,
    // 180
    val cadence: Int = 0,
) : Parcelable

internal val INITIAL_RECORD_STATE =
    RecordUiState(
        currentDistance = 0,
        currentDuration = 0,
        avgPace = 0,
        cadence = 0,
    )
