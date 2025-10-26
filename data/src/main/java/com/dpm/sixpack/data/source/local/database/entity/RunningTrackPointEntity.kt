package com.dpm.sixpack.data.source.local.database.entity

import androidx.room.PrimaryKey

data class RunningTrackPointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long, // 이 트랙 포인트가 속한 세션 ID
    val timestamp: Long, // 이 트랙 포인트의 타임스탬프
    val latitude: Double, // 위도
    val longitude: Double, // 경도
    val altitude: Double, // 고도 (meters)
    val speed: Double,
    val avgPace: Int, // 평균 페이스 (s/km)
    val avgCadence: Int,
    val distanceInMeter: Int,
)
