package com.dpm.sixpack.presentation.common.util

import android.Manifest
import android.location.Location
import android.os.SystemClock
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class MockLocationClient(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val scope: CoroutineScope,
) {
    private var simulationJob: Job? = null

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun startWithLocation(
        path: List<Location>,
        delayMillis: Long = 1000L,
    ) {
        startSimulation(path, delayMillis) { it }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun startWithLatLng(
        path: List<LatLng>,
        delayMillis: Long = 1000L,
    ) {
        startSimulation(path, delayMillis) { it.toMockLocation() }
    }

    fun stop() {
        simulationJob?.cancel()
        simulationJob = null
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun <T> startSimulation(
        path: List<T>,
        delayMillis: Long,
        converter: (T) -> Location,
    ) {
        stop()
        simulationJob =
            scope.launch {
                try {
                    fusedLocationClient.setMockMode(true)
                    Timber.Forest.tag("MockLocationClient").d("Mock mode enabled.")

                    for (point in path) {
                        val mockLocation = converter(point)

                        fusedLocationClient.setMockLocation(mockLocation)
                        Timber.Forest
                            .tag("MockLocationClient")
                            .d("Set mock location to: ${mockLocation.latitude}, ${mockLocation.longitude}")

                        delay(delayMillis)
                    }
                } finally {
                    stopMockMode()
                    Timber.Forest.tag("MockLocationClient").d("Simulation finished or stopped.")
                }
            }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun stopMockMode() {
        fusedLocationClient.setMockMode(false)
    }

    private fun LatLng.toMockLocation(): Location =
        Location("MockProvider").apply {
            latitude = this@toMockLocation.latitude
            longitude = this@toMockLocation.longitude
            accuracy = 1.0f
            time = System.currentTimeMillis()
            elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
        }
}
