package com.dpm.sixpack.presentation.routes.session.contract.uistate

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecordUiState(
    val currentDistance: String = "",
    // 00:32:10
    val currentDuration: String = "",
    // 5'30"
    val avgPace: String = "",
    // 180
    val cadence: String = "",
) : Parcelable

internal val INITIAL_RECORD_STATE =
    RecordUiState(
        currentDistance = "0.0km",
        currentDuration = "00:00:00",
        avgPace = "0'00\"",
        cadence = "0",
    )
