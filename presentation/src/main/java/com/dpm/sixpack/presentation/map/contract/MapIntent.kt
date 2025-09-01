package com.dpm.sixpack.presentation.map.contract

import com.dpm.sixpack.presentation.base.UiIntent
import com.naver.maps.geometry.LatLng

sealed interface MapIntent : UiIntent {
    // 권한
    data object RequestLocationPermission : MapIntent // 권한 요청
    data class UpdateLocationPermission(val isGranted: Boolean) : MapIntent // 권한 업데이트 여부

    // 위치
    data class SetInitialLocation(val latLng: LatLng) : MapIntent // 초기 기기 위치 설정

    // 카메라 이동
    data class MoveCameraToPosition(val latLng: LatLng) : MapIntent

    // 네비게이션
}
