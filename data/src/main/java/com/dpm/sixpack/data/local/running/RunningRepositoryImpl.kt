package com.dpm.sixpack.data.local.running

import com.dpm.sixpack.domain.model.RunRecord
import com.dpm.sixpack.domain.model.SimpleLocation
import com.dpm.sixpack.domain.running.RunningRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RunningRepositoryImpl @Inject constructor(
    private val dataSource: LocalRunningDataSource,
) : RunningRepository {

    override fun startNewCourse(): Long {
        TODO("Not yet implemented")
    }

    override fun addLocationToCourse(
        courseId: Long,
        simpleLocation: SimpleLocation,
        timestamp: Long
    ) {
        TODO("Not yet implemented")
    }

    override fun updateCourseDistance(courseId: Long, distance: Double) {
        TODO("Not yet implemented")
    }

    override fun saveRunSession(courseId: Long, startTime: Long, endTime: Long) {
        TODO("Not yet implemented")
    }

    override fun getRunDetails(sessionId: Long): Flow<RunRecord> {
        TODO("Not yet implemented")
    }

    override fun getAllRunSession(): Flow<List<RunRecord>> {
        TODO("Not yet implemented")
    }

    override fun deleteRun(sessionId: Long) {
        TODO("Not yet implemented")
    }
}
