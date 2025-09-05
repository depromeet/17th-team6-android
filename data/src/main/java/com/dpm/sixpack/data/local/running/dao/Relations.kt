package com.dpm.sixpack.data.local.running.dao

import androidx.room.Embedded
import androidx.room.Relation
import com.dpm.sixpack.data.local.running.entity.CourseEntity
import com.dpm.sixpack.data.local.running.entity.LocationPointEntity
import com.dpm.sixpack.data.local.running.entity.RunSessionEntity

data class CourseWithLocations(
    @Embedded
    val course: CourseEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "courseId"
    )
    val locations: List<LocationPointEntity>
)

data class RunSessionWithCourseAndLocations(
    @Embedded
    val session: RunSessionEntity,

    @Relation(
        entity = CourseEntity::class,
        parentColumn = "courseId",
        entityColumn = "id"
    )
    val courseWithLocations: CourseWithLocations
)
