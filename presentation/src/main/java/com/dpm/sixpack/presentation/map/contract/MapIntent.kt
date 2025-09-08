package com.dpm.sixpack.presentation.map.contract

import com.dpm.sixpack.presentation.base.UiIntent
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.compose.LocationTrackingMode

sealed interface MapIntent : UiIntent {
    // 권한
    data object RequestLocationPermission : MapIntent // 권한 요청

    data class UpdateLocationPermission(
        val isGranted: Boolean,
    ) : MapIntent // 권한 업데이트 여부

    // 위치
    data class SetInitialLocation(
        val latLng: LatLng,
    ) : MapIntent // 초기 기기 위치 설정

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
        val curLatLng: LatLng?,
        val curTrackingMode: LocationTrackingMode?
    ) : MapIntent

    // 네비게이션
}
