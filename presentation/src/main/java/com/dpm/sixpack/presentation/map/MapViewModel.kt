package com.dpm.sixpack.presentation.map

import MockLocationClient
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.annotation.RequiresPermission
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dpm.sixpack.core.permission.PermissionUtil
import com.dpm.sixpack.core.permission.SixPackPermissions
import com.dpm.sixpack.presentation.base.BaseViewModel
import com.dpm.sixpack.presentation.map.component.MapConstants.MIN_DISTANCE_BETWEEN_PATH
import com.dpm.sixpack.presentation.map.component.MapConstants.MIN_LENGTH_PATH_ARRAY
import com.dpm.sixpack.presentation.map.contract.MapIntent
import com.dpm.sixpack.presentation.map.contract.MapSideEffect
import com.dpm.sixpack.presentation.map.contract.MapState
import com.dpm.sixpack.presentation.util.calculateDistance
import com.google.android.gms.location.FusedLocationProviderClient
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class MapViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val fusedLocationClient: FusedLocationProviderClient,
    @ApplicationContext val context: Context,
) : BaseViewModel<MapState, MapIntent, MapSideEffect>() {
    private val mockLocationClient = MockLocationClient(fusedLocationClient, viewModelScope)

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
                setRunningMode(intent.mode)
            }

            is MapIntent.StartMockSimulation -> {
                startMockSimulation(intent.mockPath)
            }

            is MapIntent.StopMockSimulation -> {
                stopMockSimulation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setInitialLocation() {
        intent {
            reduce {
                state.copy(
                    isInitialLocationSet = true,
                )
            }
        }
    }

    private fun updateUserLocation(latLng: LatLng) =
        intent {
            val isSessionInProgress = state.runningMode || state.isMockSimulating

            if (isSessionInProgress) {
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
            postSideEffect(MapSideEffect.UpdateLocationPermission(isGranted))
        }

    @SuppressLint("MissingPermission")
    private fun setRunningMode(
        mode: Boolean,
    ) {
        intent {
            reduce {
                state.copy(
                    runningMode = mode,
                    path = emptyList(),
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startMockSimulation(mockPath: List<LatLng>) = intent {
        if (state.isMockSimulating) return@intent

        reduce { state.copy(isMockSimulating = true) }

        mockLocationClient.startWithLatLng(mockPath)
    }

    private fun stopMockSimulation() = intent {
        if (!state.isMockSimulating) return@intent

        mockLocationClient.stop()
        reduce { state.copy(isMockSimulating = false, path = emptyList()) }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun FusedLocationProviderClient.fetchLastLocation(
        context: Context,
        onSuccess: (location: Location) -> Unit,
        onFailure: () -> Unit,
    ) {
        intent {
            if (PermissionUtil.hasPermissions(context, SixPackPermissions.LocationPermissions)
            ) {
                lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            onSuccess(location)
                        } else {
                            onFailure()
                        }
                    }.addOnFailureListener {
                        onFailure()
                    }
            }
        }

    }
}
