package com.dpm.sixpack.data.local.running.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.dpm.sixpack.data.local.running.entity.CourseEntity
import com.dpm.sixpack.data.local.running.entity.LocationPointEntity
import com.dpm.sixpack.data.local.running.entity.RunSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RunningDao {
    // --- 세션 ---
    @Insert
    suspend fun insertRunSession(session: RunSessionEntity): Long

    @Query("SELECT * FROM running_sessions ORDER BY startTime DESC")
    fun getAllRunSessions(): Flow<List<RunSessionEntity>>

    @Query("SELECT * FROM running_sessions WHERE id = :sessionId")
    suspend fun getRunSessionById(sessionId: Long): RunSessionEntity?

    @Query("DELETE FROM running_sessions WHERE id = :sessionId")
    suspend fun deleteRunSessionById(sessionId: Long)

    // --- 코스 ---
    @Insert
    suspend fun insertCourse(course: CourseEntity): Long

    @Insert
    suspend fun insertLocationPoint(locationPoint: LocationPointEntity)
    
    @Update
    suspend fun updateCourse(courseId: Long, distance: Double)

    @Query("SELECT * FROM courses WHERE id = :courseId")
    suspend fun getCourseById(courseId: Long): CourseEntity?

    @Transaction
    @Query("SELECT * FROM courses WHERE id = :courseId")
    fun getLocationWithCourse(courseId: Long): Flow<CourseWithLocations>

    @Transaction
    @Query("SELECT * FROM running_sessions WHERE id = :sessionId")
    fun getRunSessionWithCourseAndLocations(sessionId: Long): Flow<RunSessionWithCourseAndLocations>
}
