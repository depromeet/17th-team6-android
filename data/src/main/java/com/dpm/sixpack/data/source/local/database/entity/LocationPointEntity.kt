package com.dpm.sixpack.data.source.local.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * 특정 러닝 코스에 속한 위치 좌표들을 저장
 */
@Entity(
    tableName = "location_points",
)
data class LocationPointEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val courseId: Long, // 이 좌표가 어떤 코스에 속하는지
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
)
