package com.dpm.sixpack.data.repository

import android.location.Location
import android.os.SystemClock
import com.dpm.sixpack.domain.repository.GpsRepository
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class MockGpsRepository
    @Inject
    constructor() : GpsRepository {
        // --- 시뮬레이션 설정값 ---
        companion object {
            private const val START_LATITUDE = 37.566689
            private const val START_LONGITUDE = 126.978181
            private const val SPEED_METERS_PER_SECOND = 2.8f
            private const val BEARING_DEGREES = 0f
            private const val UPDATE_INTERVAL_MS = 1000L
            private const val EARTH_RADIUS_METERS = 6371000.0
        }

        override fun getLocationFlow(): Flow<DoRunResult<Location>> =
            flow {
                var currentLocation = createMockLocation(START_LATITUDE, START_LONGITUDE, SPEED_METERS_PER_SECOND)

                val distancePerTick = SPEED_METERS_PER_SECOND * (UPDATE_INTERVAL_MS / 1000.0)

                while (true) {
                    emit(DoRunResult.Success(currentLocation))

                    delay(UPDATE_INTERVAL_MS)

                    // 다음 위치를 Location 객체로 직접 계산
                    currentLocation =
                        calculateNewLocation(
                            startPoint = currentLocation,
                            distanceMeters = distancePerTick,
                            bearingDegrees = BEARING_DEGREES,
                        )
                }
            }

        /**
         * 시작 Location, 거리, 방향을 기반으로 새로운 Location 객체를 계산합니다.
         */
        private fun calculateNewLocation(
            startPoint: Location,
            distanceMeters: Double,
            bearingDegrees: Float,
        ): Location {
            val latRad = Math.toRadians(startPoint.latitude)
            val lonRad = Math.toRadians(startPoint.longitude)
            val bearingRad = Math.toRadians(bearingDegrees.toDouble())
            val angularDistance = distanceMeters / EARTH_RADIUS_METERS

            val newLatRad =
                asin(
                    sin(latRad) * cos(angularDistance) +
                        cos(latRad) * sin(angularDistance) * cos(bearingRad),
                )
            val newLat = Math.toDegrees(newLatRad)

            val newLonRad =
                lonRad +
                    atan2(
                        sin(bearingRad) * sin(angularDistance) * cos(latRad),
                        cos(angularDistance) - sin(latRad) * sin(newLatRad),
                    )
            val newLon = Math.toDegrees(newLonRad)

            // 계산된 위도/경도로 새로운 Location 객체를 생성하여 반환
            return createMockLocation(newLat, newLon, startPoint.speed)
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
