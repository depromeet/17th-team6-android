package com.dpm.sixpack.data.local.running.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "running_sessions",
    foreignKeys = [
        // RunSession가 Course 테이블을 참조
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE // FIXME: 외래키 제약 뭘로할지 모르겠음
        )
    ]
)
data class RunSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val courseId: Long, // 세션에 어떤 코스를 달렸는지
    var isSynced: Boolean = false, // 서버 동기화 여부
    val startTime: Long,
    var endTime: Long? = null,
    var durationInMillis: Long = 0,
    var distanceInMeters: Double = 0.0,
)
