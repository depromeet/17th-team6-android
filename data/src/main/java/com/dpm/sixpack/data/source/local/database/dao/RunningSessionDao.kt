package com.dpm.sixpack.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dpm.sixpack.data.source.local.database.entity.RunningTrackPointEntity

@Dao
interface RunningSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRunningTrackPoint(point: RunningTrackPointEntity)

    @Query("DELETE FROM running_track_point")
    suspend fun deleteAllRunningTrackPoints()

    /**
     * 5분마다 서버에 전송할 '동기화되지 않은' 모든 트랙 포인트
     */
    @Query("SELECT * FROM running_track_point WHERE isSynced = 0")
    suspend fun getUnsyncedTrackPoints(): List<RunningTrackPointEntity>

    /**
     * 서버 전송에 성공한 포인트들의 ID를 받아 '동기화됨'으로 표시
     * @param pointIds 동기화에 성공한 포인트들의 Primary Key (id) 리스트
     */
    @Query("UPDATE running_track_point SET isSynced = 1 WHERE id IN (:pointIds)")
    suspend fun markPointsAsSynced(pointIds: List<Long>)
}
