package com.dpm.sixpack.domain.model

data class Segment(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val speed: Double,
    val pace: Int,
    val cadence: Int,
    val distance: Int,
    val time: String,
)
