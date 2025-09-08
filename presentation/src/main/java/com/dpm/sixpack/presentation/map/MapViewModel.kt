package com.dpm.sixpack.presentation.map

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.presentation.base.BaseViewModel
import com.dpm.sixpack.presentation.map.component.MapConstants.MIN_DISTANCE_BETWEEN_PATH
import com.dpm.sixpack.presentation.map.component.MapConstants.MIN_LENGTH_PATH_ARRAY
import com.dpm.sixpack.presentation.map.contract.MapIntent
import com.dpm.sixpack.presentation.map.contract.MapSideEffect
import com.dpm.sixpack.presentation.map.contract.MapState
import com.dpm.sixpack.presentation.util.calculateDistance
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.compose.LocationTrackingMode
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class MapViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<MapState, MapIntent, MapSideEffect>() {
    override val initialState: MapState = MapState()
    override val container: Container<MapState, MapSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    override fun onIntent(intent: MapIntent) {
        when (intent) {
            is MapIntent.MoveCameraToPosition ->
                intent {
                    postSideEffect(MapSideEffect.ScrollCameraPosition(intent.latLng))
                }

            is MapIntent.ChangeCameraPosition ->
                intent {
                    postSideEffect(MapSideEffect.ChangeCameraPosition(intent.cameraPosition))
                }

            MapIntent.RequestLocationPermission -> TODO()

            is MapIntent.SetInitialLocation -> {
                setInitialLocation()
            }

            is MapIntent.UpdateUserLocation -> {
                updateUserLocation(intent.latLng)
            }

            is MapIntent.UpdateLocationPermission -> {
                updateLocationPermission(intent.isGranted)
            }

            is MapIntent.ChangeRunningMode -> {
                setRunningMode(intent.mode, intent.curLatLng, intent.curTrackingMode)
            }
        }
    }

    private fun setInitialLocation() =
        intent {
            reduce {
                state.copy(
                    isInitialLocationSet = true,
                )
            }
        }

    private fun updateUserLocation(latLng: LatLng) =
        intent {
            if (state.runningMode) {
                if (state.path.size < MIN_LENGTH_PATH_ARRAY ||
                    state.path.lastOrNull()?.let {
                        calculateDistance(latLng, it) > MIN_DISTANCE_BETWEEN_PATH
                    } == true
                ) {
                    reduce {
                        state.copy(path = state.path + latLng)
                    }
                }
            }
        }

    private fun updateLocationPermission(isGranted: Boolean) =
        intent {
            reduce {
                state.copy(isLocationPermissionGranted = isGranted)
            }
            postSideEffect(MapSideEffect.SetInitialLocation(isGranted))
        }

    private fun setRunningMode(
        mode: Boolean,
        curLatLng: LatLng?,
        curTrackingMode: LocationTrackingMode?,
    ) = intent {
        if (curTrackingMode != LocationTrackingMode.Follow) {
            curLatLng?.let { latLng ->
                postSideEffect(MapSideEffect.ScrollCameraPosition(latLng))
            }
        }

        reduce {
            state.copy(
                runningMode = mode,
                path = curLatLng?.let { listOf(it) } ?: emptyList(),
//                mapResetTrigger = if (mode) state.mapResetTrigger + 1 else state.mapResetTrigger
            )
        }
    }
}
