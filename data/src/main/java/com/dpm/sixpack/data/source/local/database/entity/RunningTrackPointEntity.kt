package com.dpm.sixpack.data.source.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "running_track_point")
data class RunningTrackPointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long, // 이 트랙 포인트의 타임스탬프
    val latitude: Double, // 위도
    val longitude: Double, // 경도
    val altitude: Double, // 고도 (meters)
    val speed: Double,
    val avgPace: Int, // 평균 페이스 (s/km)
    val maxPace: Int, // 최대 페이스 (s/km)
    val maxPaceLatitude: Double, // 최대 페이스 위치 위도
    val maxPaceLongitude: Double, // 최대 페이스 위치 경도
    val avgCadence: Int,
    val maxCadence: Int,
    val distanceInMeter: Int,
    val durationInSec: Int,
    val isSynced: Boolean = false, // 서버 세그먼트 전송 여부
)
