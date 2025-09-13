package com.dpm.sixpack.data.source.remote.repository

import com.dpm.sixpack.data.source.remote.dto.request.FinishRunningRequestDto
import com.dpm.sixpack.data.source.remote.dto.request.LocationDataRequestDto
import com.dpm.sixpack.data.source.remote.dto.request.SaveSegmentDataRequestsDto

object MockRequestDataFactory {


    fun createMockFinishRunningRequest(): FinishRunningRequestDto {
        return FinishRunningRequestDto(
            totalDistance = 2000.0, // 총 거리: 2km
            totalDuration = 600,    // 총 시간: 10분 (600초)
            avgPace = 300,          // 평균 페이스: 5'00"/km (300초)
            avgCadence = 175,       // 평균 케이던스
            maxCadence = 185        // 최대 케이던스
        )
    }

    /**
     * 5분마다 전송할 러닝 세그먼트(경로)의 Mock 데이터를 생성합니다.
     *
     * @return 5분 동안의 위치 및 상태 데이터 리스트를 포함하는 SaveSegmentDataRequestsDto
     */
    fun createMockSaveSegmentRequest(): SaveSegmentDataRequestsDto {
        val mockSegments = listOf(
            // 1분 30초 경과 시점
            LocationDataRequestDto(
                latitude = 37.5250,
                longitude = 126.9250,
                altitude = 25.0,
                speed = 11.8,
                pace = 305, // 5'05"/km
                cadence = 172,
                distance = 500.0, // 누적 500m
                time = "2025-09-14T03:01:30Z"
            ),
            // 3분 00초 경과 시점
            LocationDataRequestDto(
                latitude = 37.5265,
                longitude = 126.9265,
                altitude = 25.5,
                speed = 12.2,
                pace = 295, // 4'55"/km
                cadence = 178,
                distance = 1000.0, // 누적 1km
                time = "2025-09-14T03:03:00Z"
            ),
            // 4분 30초 경과 시점
            LocationDataRequestDto(
                latitude = 37.5280,
                longitude = 126.9280,
                altitude = 26.0,
                speed = 12.0,
                pace = 300, // 5'00"/km
                cadence = 175,
                distance = 1500.0, // 누적 1.5km
                time = "2025-09-14T03:04:30Z"
            )
        )
        return SaveSegmentDataRequestsDto(segment = mockSegments)
    }
}
