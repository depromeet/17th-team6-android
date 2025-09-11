package com.dpm.sixpack.presentation.map.contract

import com.dpm.sixpack.presentation.base.UiIntent
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition

sealed interface MapIntent : UiIntent {
    // 권한
    data object RequestLocationPermission : MapIntent // 권한 요청

    data class UpdateLocationPermission(
        val isGranted: Boolean,
    ) : MapIntent // 권한 업데이트 여부

    // 초기 위치 설정
    data object SetInitialLocation : MapIntent

    data class UpdateUserLocation(
        val latLng: LatLng,
    ) : MapIntent // 사용자 위치 업데이트

    // 카메라 이동(스크롤)
    data class MoveCameraToPosition(
        val latLng: LatLng,
    ) : MapIntent

    // 카메라 설정
    data class ChangeCameraPosition(
        val cameraPosition: CameraPosition,
    ) : MapIntent

    // 러닝
    data class ChangeRunningMode(
        val mode: Boolean,
    ) : MapIntent

    data class StartMockSimulation(
        val mockPath: List<LatLng>
    ) : MapIntent

    data object StopMockSimulation : MapIntent

    // 네비게이션
}
