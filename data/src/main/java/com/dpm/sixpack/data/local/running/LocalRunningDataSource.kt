package com.dpm.sixpack.data.local.running

import com.dpm.sixpack.data.local.running.dao.CourseWithLocations
import com.dpm.sixpack.data.local.running.dao.RunSessionWithCourseAndLocations
import com.dpm.sixpack.data.local.running.dao.RunningDao
import com.dpm.sixpack.data.local.running.entity.CourseEntity
import com.dpm.sixpack.data.local.running.entity.LocationPointEntity
import com.dpm.sixpack.data.local.running.entity.RunSessionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalRunningDataSource @Inject constructor(
    private val runningDao: RunningDao
) {

    /**
     * 새로운 달리기 기록을 시작하고, 생성된 코스 ID를 반환
     */
    suspend fun startNewCourse(): Long {
        val newCourse = CourseEntity(name = null)
        return runningDao.insertCourse(newCourse)
    }

    /**
     * 코스에 좌표를 추가
     */
    suspend fun addLocationToCourse(
        courseId: Long,
        latitude: Double,
        longitude: Double,
        timeStamp: Long,
    ) {
        val locationPoint =
            LocationPointEntity(
                courseId = courseId,
                latitude = latitude,
                longitude = longitude,
                timestamp = timeStamp
            )
        runningDao.insertLocationPoint(locationPoint)
    }

    /*
     * 코스 거리 업데이트
     */
    suspend fun updateCourseDistance(courseId: Long, distance: Double) {
        runningDao.updateCourse(courseId, distance)
    }

    /**
     * 달리기 종료 -> 세선 생성
     */
    suspend fun saveRunSession(courseId: Long, startTime: Long, endTime: Long?, duration: Long): Long {
        val distance = runningDao.getCourseById(courseId)?.distanceInMeters ?: 0.0

        val session = RunSessionEntity(
            courseId = courseId,
            startTime = startTime,
            endTime = endTime ?: System.currentTimeMillis(),
            durationInMillis = duration,
            distanceInMeters = distance
        )

        return runningDao.insertRunSession(session)
    }

    /**
     * 특정 ID 달리기 세션을 삭제
     */
    suspend fun deleteRun(sessionId: Long) {
        runningDao.deleteRunSessionById(sessionId)
    }

    /**
     * 모든 달리기 기록
     */
    fun getAllRunSession(): Flow<List<RunSessionEntity>> = runningDao.getAllRunSessions()

    /*
     * 경로의 좌표들
     */
    fun getLocationsWithCourse(courseId: Long): Flow<CourseWithLocations> =
        runningDao.getLocationWithCourse(courseId)

    /**
     * 특정 달리기 기록의 상세 정보(경로 포함)를 가져옴
     */
    fun getRunDetails(sessionId: Long): Flow<RunSessionWithCourseAndLocations> =
        runningDao.getRunSessionWithCourseAndLocations(sessionId)
}
