package com.dpm.sixpack.presentation.routes.running.session.contract.state

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecordState(
    val currentDistance: Int = 0,
    // 00:32:10
    val currentDuration: Int = 0,
    // 5'30"
    val avgPace: Int = 0,
    // 180
    val cadence: Int = 0,
) : Parcelable
