package com.dpm.sixpack.presentation.map

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.presentation.base.BaseViewModel
import com.dpm.sixpack.presentation.map.component.MapConstants.DEFAULT_ZOOM
import com.dpm.sixpack.presentation.map.contract.MapIntent
import com.dpm.sixpack.presentation.map.contract.MapSideEffect
import com.dpm.sixpack.presentation.map.contract.MapState
import com.dpm.sixpack.presentation.util.calculateDistance
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : BaseViewModel<MapState, MapIntent, MapSideEffect>() {

    override val initialState: MapState = MapState()
    override val container: Container<MapState, MapSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    override fun onIntent(intent: MapIntent) {
        when (intent) {
            is MapIntent.MoveCameraToPosition -> TODO()
            MapIntent.RequestLocationPermission -> TODO()
            is MapIntent.SetInitialLocation -> {
                setInitialLocation(intent.latLng)
            }

            is MapIntent.UpdateUserLocation -> {
                updateUserLocation(intent.latLng)
            }

            is MapIntent.UpdateLocationPermission -> {
                updateLocationPermission(intent.isGranted)
            }
        }
    }

    private fun setInitialLocation(latLng: LatLng) = intent {
        reduce {
            state.copy(
                isInitialLocationSet = true,
                cameraPosition = CameraPosition(latLng, DEFAULT_ZOOM)
            )
        }
    }

    private fun updateUserLocation(latLng: LatLng) = intent {
        if (state.path.size <= 5 ||
            state.path.lastOrNull()?.let {
                calculateDistance(latLng, it) > 5.0
            } == true
        ) {
            reduce {
                state.copy(path = state.path + latLng)
            }
        }
    }

    private fun updateLocationPermission(isGranted: Boolean) = intent {
        reduce {
            state.copy(isLocationPermissionGranted = isGranted)
        }
        if (isGranted) {
            postSideEffect(MapSideEffect.SetInitialLocation)
        }
    }
}
