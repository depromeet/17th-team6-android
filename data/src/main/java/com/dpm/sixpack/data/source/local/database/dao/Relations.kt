package com.dpm.sixpack.data.source.local.database.dao

import androidx.room.Embedded
import androidx.room.Relation
import com.dpm.sixpack.data.source.local.database.entity.CourseEntity
import com.dpm.sixpack.data.source.local.database.entity.LocationPointEntity
import com.dpm.sixpack.data.source.local.database.entity.RunSessionEntity

data class CourseWithLocations(
    @Embedded
    val course: CourseEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "courseId",
    )
    val locations: List<LocationPointEntity>,
)

data class RunSessionWithCourseAndLocations(
    @Embedded
    val session: RunSessionEntity,
    @Relation(
        entity = CourseEntity::class,
        parentColumn = "courseId",
        entityColumn = "id",
    )
    val courseWithLocations: CourseWithLocations,
)
