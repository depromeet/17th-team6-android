package com.dpm.sixpack.data.source.local.gps

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GpsDataSource @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
) {
    @SuppressLint("MissingPermission")
    val locationFlow: Flow<Location> =
        callbackFlow {
            val locationRequest =
                LocationRequest
                    .Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L)
                    .setWaitForAccurateLocation(true)
                    .setMinUpdateDistanceMeters(5.0f)
                    .build()

            val locationCallback =
                object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        result.lastLocation?.let { location ->
                            trySend(location)
                        }
                    }
                }

            Timber.d("Data : Location 수집을 시작합니다. (새 구독 발생)")
            locationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper(),
            )

            // Flow 구독이 취소되면 (awaitClose) 위치 업데이트를 중단
            awaitClose {
                Timber.d("Data : Location 수집을 중단합니다. (구독 취소)")
                locationClient.removeLocationUpdates(locationCallback)
            }
        }
}
