package com.dpm.sixpack.domain.running

import com.dpm.sixpack.domain.model.RunRecord
import com.dpm.sixpack.domain.model.SimpleLocation
import kotlinx.coroutines.flow.Flow

interface RunningRepository {
    fun startNewCourse(): Long

    fun addLocationToCourse(
        courseId: Long,
        simpleLocation: SimpleLocation,
        timestamp: Long,
    )

    fun updateCourseDistance(
        courseId: Long,
        distance: Double,
    )

    fun saveRunSession(
        courseId: Long,
        startTime: Long,
        endTime: Long,
    )

    fun getRunDetails(sessionId: Long): Flow<RunRecord>

    fun getAllRunSession(): Flow<List<RunRecord>>

    fun deleteRun(sessionId: Long)
}
