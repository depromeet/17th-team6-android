package com.dpm.sixpack.data.source.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 하나의 달리기 코스(경로) 정보를 저장
 */
@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String?, // 코스 이름
    var distanceInMeters: Double = 0.0,
//    val createdAt: Long = System.currentTimeMillis(),
)
