package com.dpm.sixpack.presentation.routes.running.map

import android.Manifest
import android.annotation.SuppressLint
import androidx.annotation.RequiresPermission
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.running.map.contract.MapIntent
import com.dpm.sixpack.presentation.routes.running.map.contract.MapSideEffect
import com.dpm.sixpack.presentation.routes.running.map.contract.MapUiState
import com.dpm.sixpack.presentation.routes.running.map.contract.MapViewState
import com.dpm.sixpack.presentation.routes.running.session.contract.state.PathState
import com.google.android.gms.location.FusedLocationProviderClient
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

/*
 * 지도에 관련된 로직만 처리하는 뷰모델
 * 1. 지도 위치 권한 관련
 * 2. 카메라 업데이트
 * 3. 위치 업데이트
 */

@HiltViewModel
class MapViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val fusedLocationProviderClient: FusedLocationProviderClient,
) : BaseViewModel<MapUiState, MapIntent, MapSideEffect>() {
    override val initialState: MapUiState = MapUiState()

    override val container: Container<MapUiState, MapSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    @SuppressLint("MissingPermission")
    override fun onIntent(intent: MapIntent) {
        when (intent) {
            MapIntent.SessionStartClick -> handleSessionStartButtonClick()
            MapIntent.SessionFinished -> handleSessionFinished()
            MapIntent.ToggleFollowingMode -> handleToggleFollowingMode()
            MapIntent.FollowingModeOff -> handleToggleFollowingModeOff()
            is MapIntent.UpdateUserLocation -> handleUserLocationChange(intent.latLng)
            is MapIntent.UpdatePermission -> handlePermissionUpdate(intent.isGranted)
            is MapIntent.UpdateRunningMapPath -> updateRunningMapPath(intent.pathState)
        }
    }

    private fun handleUserLocationChange(latLng: LatLng) {
        intent {
            if (state.isFollowingModeEnabled) {
                postSideEffect(MapSideEffect.SetCameraPosition(latLng))
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun handleToggleFollowingMode() =
        intent {
            val currentFollowMode = state.isFollowingModeEnabled
            if (!currentFollowMode) {
                loadLocationFromClient(
                    onSuccess = { latLng ->
                        viewModelScope.launch {
                            postSideEffect(MapSideEffect.SetCameraPosition(latLng))
                        }
                    },
                )
            }
            reduce {
                state.copy(
                    isFollowingModeEnabled = !currentFollowMode,
                )
            }
        }

    private fun handleToggleFollowingModeOff() {
        intent {
            reduce {
                state.copy(
                    isFollowingModeEnabled = false,
                )
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun handlePermissionUpdate(isGranted: Boolean) {
        if (isGranted) {
            loadLocationFromClient(
                onSuccess = { latLng ->
                    intent {
                        postSideEffect(MapSideEffect.SetCameraPosition(latLng))
                    }
                },
            )
        } else {
            intent {
                postSideEffect(MapSideEffect.SetCameraPosition(MapConstants.DEFAULT_CAMERA_POSITION.target))
            }
        }
    }

    private fun handleSessionStartButtonClick() =
        intent {
            reduce {
                state.copy(
                    mapViewState = MapViewState.Running(),
                )
            }

            postSideEffect(MapSideEffect.SetBottomBarVisibility(false))
        }

    private fun handleSessionFinished() =
        intent {
            reduce {
                state.copy(
                    mapViewState = MapViewState.Friend(),
                )
            }
            postSideEffect(MapSideEffect.NavigateToReport)
        }

    private fun updateRunningMapPath(newPathState: PathState) =
        intent {
            val mapViewState = state.mapViewState
            if (mapViewState is MapViewState.Running) {
                reduce {
                    state.copy(
                        mapViewState =
                            MapViewState.Running(
                                pathColorState = mapViewState.pathColorState.updatedWith(newPathState),
                            ),
                    )
                }
            }
        }

    // TODO SK: 이 함수 호출부 모두 Client 직접 사용하는 방식 말고 유스케이스 거치는 방식으로 변경하기
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun loadLocationFromClient(onSuccess: (LatLng) -> Unit) {
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    onSuccess(userLatLng)
                } else {
                    Timber.e("Last Location is Null")
                }
            }.addOnFailureListener {
                Timber.e("Load Location From Client failed: $it")
            }
    }
}
