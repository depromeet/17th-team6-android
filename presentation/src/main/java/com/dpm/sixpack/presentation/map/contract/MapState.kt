package com.dpm.sixpack.presentation.map.contract

import com.dpm.sixpack.presentation.base.UiState
import com.naver.maps.geometry.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class MapState(
    val runningMode: Boolean = false,
    val isInitialLocationSet: Boolean = false, // 기기의 현재 위치가 초기 설정되었는지 여부
    val isLocationPermissionGranted: Boolean = false, // 위치 권한이 허용되었는지 여부
    val path: List<LatLng> = emptyList(), // 경로
    val mapResetTrigger: Int = 0,
) : UiState
