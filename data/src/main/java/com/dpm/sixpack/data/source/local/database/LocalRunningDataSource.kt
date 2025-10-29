package com.dpm.sixpack.data.source.local.database

import com.dpm.sixpack.data.source.local.database.dao.RunningSessionDao
import com.dpm.sixpack.data.source.local.database.entity.RunningTrackPointEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalRunningDataSource @Inject constructor(
    private val runningSessionDao: RunningSessionDao,
) {
    suspend fun saveRunningTrackPoint(point: RunningTrackPointEntity) {
        runningSessionDao.insertRunningTrackPoint(point)
    }

    suspend fun getUnsyncedTrackPoints(): List<RunningTrackPointEntity> = runningSessionDao.getUnsyncedTrackPoints()

    suspend fun markPointsAsSynced(pointIds: List<Long>) = runningSessionDao.markPointsAsSynced(pointIds)

    suspend fun deleteAllRunningTrackPoints() {
        runningSessionDao.deleteAllRunningTrackPoints()
    }
}
