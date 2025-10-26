package com.dpm.sixpack.data.source.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.dpm.sixpack.data.source.local.database.entity.RunningTrackPointEntity

@Dao
interface RunningSessionDao {
    // 트랙 포인트 저장 (1초마다)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRunningTrackPoint(point: RunningTrackPointEntity)
}
