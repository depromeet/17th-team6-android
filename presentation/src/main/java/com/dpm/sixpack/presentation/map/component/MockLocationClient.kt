import android.Manifest
import android.location.Location
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class MockLocationClient(
    val fusedLocationClient: FusedLocationProviderClient,
    private val scope: CoroutineScope,
) {
    private var simulationJob: Job? = null

    /**
     * @param path 시뮬레이션할 경로 좌표 목록
     * @param delayMillis 각 좌표 사이의 딜레이 시간 (ms)
     */
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun start(
        path: List<LatLng>,
        delayMillis: Long = 1000L,
    ) {
        stop()

        simulationJob =
            scope.launch {
                try {
                    fusedLocationClient.setMockMode(true)
                    Timber.tag("MockLocationTester").d("Mock mode enabled.")

                    for (point in path) {
                        val mockLocation =
                            Location("MockProvider").apply {
                                latitude = point.latitude
                                longitude = point.longitude
                                accuracy = 1.0f
                                elapsedRealtimeNanos = System.nanoTime()
                            }
                        fusedLocationClient.setMockLocation(mockLocation)
                        Timber
                            .tag(
                                "MockLocationTester",
                            ).d("Set mock location to: ${point.latitude}, ${point.longitude}")
                        delay(delayMillis)
                    }
                } finally {
                    stopMockMode()
                    Timber.tag("MockLocationTester").d("Simulation finished or stopped.")
                }
            }
    }

    fun stop() {
        simulationJob?.cancel()
        simulationJob = null
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun stopMockMode() {
        fusedLocationClient.setMockMode(false)
    }
}
