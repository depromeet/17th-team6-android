package com.dpm.sixpack.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Segment(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val speed: Double,
    val pace: Int,
    val cadence: Int,
    val distance: Int,
    val time: String,
) : Parcelable
