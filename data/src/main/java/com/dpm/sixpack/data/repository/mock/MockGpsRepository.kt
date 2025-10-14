package com.dpm.sixpack.data.repository.mock

import android.location.Location
import android.os.SystemClock
import com.dpm.sixpack.domain.repository.GpsRepository
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MockGpsRepository @Inject constructor() : GpsRepository {
    // --- 시뮬레이션 설정값 ---
    companion object {
        private const val UPDATE_INTERVAL_MS = 1000L // 1초 간격으로 좌표 방출
    }

    override fun getLocationFlow(): Flow<DoRunResult<Location>> =
        flow {
            var lastLocation: Location? = null

            // latLngList의 각 좌표를 순회
            for (latLng in Sungsoo) {
                val currentSpeed: Float
                val currentLocation: Location

                if (lastLocation == null) {
                    // 첫 번째 좌표인 경우 속도는 0
                    currentSpeed = 0.0f
                    currentLocation = createMockLocation(latLng.first, latLng.second, currentSpeed)
                } else {
                    // 이전 좌표가 있으면, 거리를 계산하여 속도(m/s)를 구함
                    // (간격이 1초이므로 거리가 곧 속도가 됨)
                    val tempLocation =
                        Location("").apply {
                            latitude = latLng.first
                            longitude = latLng.second
                        }
                    val distance = lastLocation!!.distanceTo(tempLocation) // 미터 단위
                    currentSpeed = distance / (UPDATE_INTERVAL_MS / 1000.0f) // m/s
                    currentLocation = createMockLocation(latLng.first, latLng.second, currentSpeed)
                }

                emit(DoRunResult.Success(currentLocation))

                lastLocation = currentLocation

                delay(UPDATE_INTERVAL_MS)
            }
        }

    /**
     * Location 객체를 생성하는 헬퍼 함수
     */
    private fun createMockLocation(
        lat: Double,
        lon: Double,
        speed: Float,
    ): Location =
        Location("FakeGpsProvider").apply {
            latitude = lat
            longitude = lon
            altitude = 30.0
            this.speed = speed
            accuracy = 1.0f
            time = System.currentTimeMillis()
            elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
        }
}

// class MockGpsRepository @Inject constructor() : GpsRepository {
//    // --- 시뮬레이션 설정값 ---
//    companion object {
//        private const val START_LATITUDE = 37.566689
//        private const val START_LONGITUDE = 126.978181
//        private const val BEARING_DEGREES = 0f
//        private const val UPDATE_INTERVAL_MS = 1000L
//        private const val EARTH_RADIUS_METERS = 6371000.0
//
//        private const val START_PACE_MIN_PER_KM = 8.0 // 시작 페이스 (8:00)
//        private const val END_PACE_MIN_PER_KM = 4.0 // 목표 페이스 (4:00)
//        private const val ACCELERATION_DURATION_SECONDS = 60.0 // 가속에 걸리는 시간 (1분)
//
//        // 페이스를 m/s 속도로 변환
//        private val START_SPEED_MPS = 1000.0 / (START_PACE_MIN_PER_KM * 60.0) // 약 2.08 m/s
//        private val END_SPEED_MPS = 1000.0 / (END_PACE_MIN_PER_KM * 60.0) // 약 4.17 m/s
//    }
//
//    override fun getLocationFlow(): Flow<DoRunResult<Location>> =
//        flow {
//            var currentLatitude = START_LATITUDE
//            var currentLongitude = START_LONGITUDE
//            var elapsedSeconds = 0.0
//
//            while (true) {
//                // 순간 속도 계산
//                val currentSpeed = calculateCurrentSpeed(elapsedSeconds)
//
//                // 2. 현재 위치와 계산된 속도로 Location 객체 생성
//                val currentLocation = createMockLocation(currentLatitude, currentLongitude, currentSpeed.toFloat())
//                emit(DoRunResult.Success(currentLocation))
//
//                // 3. 현재 속도를 기반으로 다음 1초 동안 이동할 거리 계산
//                val distancePerTick = currentSpeed * (UPDATE_INTERVAL_MS / 1000.0)
//
//                // 4. 다음 위치 계산
//                val newCoords =
//                    calculateNewCoordinates(
//                        currentLatitude,
//                        currentLongitude,
//                        distancePerTick,
//                        BEARING_DEGREES,
//                    )
//                currentLatitude = newCoords.first
//                currentLongitude = newCoords.second
//
//                // 5. 시간 업데이트 및 딜레이
//                delay(UPDATE_INTERVAL_MS)
//                elapsedSeconds += (UPDATE_INTERVAL_MS / 1000.0)
//            }
//        }
//
//    /**
//     * 경과 시간에 따라 현재 속도를 선형 보간하여 계산합니다.
//     */
//    private fun calculateCurrentSpeed(elapsedSeconds: Double): Double {
//        if (elapsedSeconds >= ACCELERATION_DURATION_SECONDS) {
//            return END_SPEED_MPS // 목표 시간 도달 후 최고 속도 유지
//        }
//        // 진행률 (0.0 ~ 1.0)
//        val fraction = elapsedSeconds / ACCELERATION_DURATION_SECONDS
//        // 시작 속도와 끝 속도 사이를 보간
//        return START_SPEED_MPS * (1.0 - fraction) + END_SPEED_MPS * fraction
//    }
//
//    /**
//     * 시작 좌표, 거리, 방향을 기반으로 새로운 위도/경도를 계산합니다.
//     */
//    private fun calculateNewCoordinates(
//        startLat: Double,
//        startLon: Double,
//        distanceMeters: Double,
//        bearingDegrees: Float,
//    ): Pair<Double, Double> {
//        val latRad = Math.toRadians(startLat)
//        val lonRad = Math.toRadians(startLon)
//        val bearingRad = Math.toRadians(bearingDegrees.toDouble())
//        val angularDistance = distanceMeters / EARTH_RADIUS_METERS
//
//        val newLatRad =
//            asin(sin(latRad) * cos(angularDistance) + cos(latRad) * sin(angularDistance) * cos(bearingRad))
//        val newLat = Math.toDegrees(newLatRad)
//
//        val newLonRad =
//            lonRad +
//                atan2(
//                    sin(bearingRad) * sin(angularDistance) * cos(latRad),
//                    cos(angularDistance) - sin(latRad) * sin(newLatRad),
//                )
//        val newLon = Math.toDegrees(newLonRad)
//
//        return newLat to newLon
//    }
//
//    /**
//     * Location 객체를 생성하는 헬퍼 함수
//     */
//    private fun createMockLocation(
//        lat: Double,
//        lon: Double,
//        speed: Float,
//    ): Location =
//        Location("FakeGpsProvider").apply {
//            latitude = lat
//            longitude = lon
//            altitude = 30.0
//            this.speed = speed
//            accuracy = 1.0f
//            time = System.currentTimeMillis()
//            elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
//        }
// }
