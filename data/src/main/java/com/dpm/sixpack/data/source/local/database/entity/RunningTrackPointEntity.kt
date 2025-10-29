package com.dpm.sixpack.data.source.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dpm.sixpack.domain.model.RealtimeRunningData

@Entity(tableName = "running_track_point")
data class RunningTrackPointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long, // 이 트랙 포인트의 타임스탬프
    val latitude: Double, // 위도
    val longitude: Double, // 경도
    val altitude: Double, // 고도 (meters)
    val speed: Double,
    val avgPace: Int, // 평균 페이스 (s/km)
    val avgCadence: Int,
    val distanceInMeter: Int,
    val isSynced: Boolean = false, // 서버 세그먼트 전송 여부
)
