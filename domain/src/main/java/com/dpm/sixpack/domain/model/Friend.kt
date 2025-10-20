package com.dpm.sixpack.domain.model

data class Friend(
    val userInfo: User,
    val lastestRunAt: String, // "2025-09-13T19:57:13Z",
    val distanceInMeter: Int, // 5000
    val latitude: Double, // 37.5301,
    val longitude: Double, // 127.12345
)
