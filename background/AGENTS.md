# Background Module - AI Agent Guidelines

This document provides guidance for AI agents (LLMs) when working with the background module of the SixPack Android application.

## Overview

The background module implements **foreground services** for long-running background operations. Currently, it contains `RunningService`, which tracks GPS location, step count, and real-time running metrics during a run session.

**Base Package**: `com.dpm.sixpack.runningservice`

## Package Structure

```
background/
├── src/main/
│   └── java/com/dpm/sixpack/runningservice/
│       ├── RunningService.kt        # Main foreground service
│       ├── RunningActions.kt        # Intent action constants
│       ├── Constants.kt             # Notification constants
│       └── di/
│           └── ServiceModule.kt     # DI module for service
└── build.gradle.kts
```

## Core Components

### 1. RunningService (`RunningService.kt`)

A **bound foreground service** that manages real-time running data collection.

**Key Characteristics**:
- Extends `LifecycleService` for lifecycle-aware coroutines
- Annotated with `@AndroidEntryPoint` for Hilt DI
- Runs in foreground with persistent notification
- Exposes `StateFlow<RealtimeRunningData?>` for UI observation
- Handles START/PAUSE/STOP actions via intents

**Service Structure**:

```kotlin
@AndroidEntryPoint
class RunningService : LifecycleService() {

    @Inject
    lateinit var getGpsDataUseCase: GetGpsDataUseCase

    @Inject
    lateinit var getStepCountUseCase: GetStepCountUseCase

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    private val binder = RunningBinder()

    // Public state exposed to UI
    private val _runningDataState = MutableStateFlow<RealtimeRunningData?>(null)
    val runningDataState get() = _runningDataState.asStateFlow()

    // Internal state
    private var isServiceRunning = false
    private var isPaused = true
    private var durationInSeconds: Int = 0
    private var lastLocation: Location? = null
    private var totalDistance: Double = 0.0
    private var stepsBeforePause: Long = 0L
    private var currentSteps: Long = 0L

    // Coroutine jobs
    private var timerJob: Job? = null
    private var locationJob: Job? = null
    private var stepJob: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let { action ->
            when (action) {
                RunningActions.START_OR_RESUME -> startOrResumeService()
                RunningActions.PAUSE -> pauseService()
                RunningActions.STOP -> stopService()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    inner class RunningBinder : Binder() {
        fun getService(): RunningService = this@RunningService
    }
}
```

**Key principles**:
- Use `LifecycleService` for automatic lifecycle management
- Inject use cases with `@Inject lateinit var`
- Use `MutableStateFlow` for reactive state updates
- Separate public state (`StateFlow`) from internal state (vars)
- Cancel all jobs in `pauseService()` and `stopService()`
- Use `lifecycleScope` for service-lifetime coroutines

### 2. Service Actions (`RunningActions.kt`)

Defines intent action constants for controlling the service.

```kotlin
object RunningActions {
    const val START_OR_RESUME = "ACTION_START_OR_RESUME_SERVICE"
    const val PAUSE = "ACTION_PAUSE_SERVICE"
    const val STOP = "ACTION_STOP_SERVICE"
}
```

**Usage in Activity/Composable**:
```kotlin
fun startRunningService(context: Context) {
    Intent(context, RunningService::class.java).also {
        it.action = RunningActions.START_OR_RESUME
        context.startService(it)
    }
}

fun pauseRunningService(context: Context) {
    Intent(context, RunningService::class.java).also {
        it.action = RunningActions.PAUSE
        context.startService(it)
    }
}

fun stopRunningService(context: Context) {
    Intent(context, RunningService::class.java).also {
        it.action = RunningActions.STOP
        context.startService(it)
    }
}
```

### 3. Notification Setup

The service runs as a foreground service with a persistent notification.

**Constants** (`Constants.kt`):
```kotlin
const val NOTIFICATION_CHANNEL_ID = "running_notification_channel"
const val NOTIFICATION_CHANNEL_NAME = "Running Tracker"
const val NOTIFICATION_ID = 1
```

**Notification Module** (`di/ServiceModule.kt`):
```kotlin
@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @Provides
    fun provideBaseNotificationBuilder(
        @ApplicationContext context: Context,
    ): NotificationCompat.Builder =
        NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_run)
            .setContentTitle("Running Session")
            .setContentText("00:00")
            .setPriority(NotificationCompat.PRIORITY_LOW)
}
```

**Creating Channel and Starting Foreground**:
```kotlin
private fun createNotificationChannel() {
    val channel = NotificationChannel(
        NOTIFICATION_CHANNEL_ID,
        NOTIFICATION_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_LOW,
    )
    notificationManager.createNotificationChannel(channel)
}

private fun startService() {
    createNotificationChannel()
    startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())
    startJobs()
}
```

**Key principles**:
- Create notification channel before starting foreground service
- Use `IMPORTANCE_LOW` to avoid sound/vibration
- Set `setOngoing(true)` to prevent swipe-to-dismiss
- Update notification content in real-time

### 4. Data Collection Pattern

The service collects data from three sources concurrently:

#### Timer (Duration)

```kotlin
private fun startTimer() {
    timerJob = lifecycleScope.launch {
        while (isActive) {
            durationInSeconds += 1
            delay(1000L)

            if (durationInSeconds % CALCULATE_PERIOD == 0 || durationInSeconds == 1) {
                paceAverage = calculateAvgPace(totalDistance, durationInSeconds)
                cadence = calculateAvgCadence(currentSteps, durationInSeconds)
            }

            postCurrentRunningDataState()
            updateNotification(durationInSeconds)
        }
    }
}
```

#### GPS Location (Distance)

```kotlin
private fun startLocationCollection() {
    locationJob = lifecycleScope.launch {
        getGpsDataUseCase().collect { result ->
            result.onSuccess { newLocation ->
                if (!isPaused) {
                    lastLocation?.let {
                        totalDistance += it.distanceTo(newLocation)
                    }
                    lastLocation = newLocation
                }
            }
        }
    }
}
```

#### Step Counter (Cadence)

```kotlin
private fun startStepCollection() {
    stepJob = lifecycleScope.launch {
        getStepCountUseCase().collect { result ->
            result.onSuccess { stepsSinceResume ->
                if (!isPaused) {
                    currentSteps = stepsBeforePause + stepsSinceResume.toLong()
                }
            }
        }
    }
}
```

**Key principles**:
- Use `lifecycleScope.launch` for service-lifetime coroutines
- Check `!isPaused` before updating state
- Accumulate distance using `Location.distanceTo()`
- Store jobs in variables for later cancellation
- Use `isActive` check in loops for cooperative cancellation

### 5. State Management

The service maintains both **internal state** (for calculations) and **public state** (for UI observation).

**Posting State Updates**:
```kotlin
private fun postCurrentRunningDataState() {
    lastLocation?.let {
        val roundedDistance = (round(totalDistance / 10.0) * 10).toInt()

        _runningDataState.value = RealtimeRunningData(
            latitude = it.latitude,
            longitude = it.longitude,
            altitude = it.altitude,
            speed = it.speed,
            pace = paceAverage,
            cadence = cadence,
            totalDistanceMeter = roundedDistance,
            duration = durationInSeconds,
            timestamp = System.currentTimeMillis(),
        )
    }
}
```

**Observing from UI**:
```kotlin
@Composable
fun RunningScreen() {
    val context = LocalContext.current
    var service by remember { mutableStateOf<RunningService?>(null) }

    val serviceConnection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                val runningBinder = binder as RunningService.RunningBinder
                service = runningBinder.getService()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                service = null
            }
        }
    }

    DisposableEffect(Unit) {
        Intent(context, RunningService::class.java).also {
            context.bindService(it, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        onDispose {
            context.unbindService(serviceConnection)
        }
    }

    val runningData by service?.runningDataState?.collectAsState()

    // Use runningData in UI
}
```

**Key principles**:
- Use `MutableStateFlow` for reactive state
- Only update state on main/UI thread (StateFlow is thread-safe)
- Round/format data before posting to state
- Emit `null` when service stops

### 6. Lifecycle Management

**Service States**:
- `isServiceRunning`: Service is created and foreground notification shown
- `isPaused`: Data collection is paused but service still running

**State Transitions**:

```
[Stopped] --START--> [Running] --PAUSE--> [Paused] --RESUME--> [Running]
    ^                                                               |
    |--------------------------- STOP ------------------------------|
```

**Implementation**:
```kotlin
private fun startOrResumeService() {
    if (!isServiceRunning) {
        isServiceRunning = true
        startService()
    } else {
        resumeService()
    }
}

private fun startService() {
    isPaused = false
    initStates()
    createNotificationChannel()
    startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())
    startJobs()
}

private fun resumeService() {
    if (isPaused) {
        isPaused = false
        startJobs()
    }
}

private fun pauseService() {
    isPaused = true
    stepsBeforePause = currentSteps  // Save state before pausing
    cancelJobs()
}

private fun stopService() {
    isServiceRunning = false
    isPaused = true
    cancelJobs()
    initStates()
    stopForeground(STOP_FOREGROUND_REMOVE)
    stopSelf()
}
```

**Key principles**:
- Initialize state on START, not on RESUME
- Save accumulated values before PAUSE
- Cancel jobs on PAUSE and STOP
- Call `stopForeground()` and `stopSelf()` on STOP
- Reset all state on STOP

## Calculation Patterns

### Average Pace (seconds per kilometer)

```kotlin
private fun calculateAvgPace(
    totalDistanceInMeters: Double,
    durationInSeconds: Int,
): Int {
    if (durationInSeconds <= 0) return 0

    val speedInMps = totalDistanceInMeters / durationInSeconds
    if (speedInMps <= 0) return 0

    val secondsPerKilometer = 1000.0 / speedInMps
    return secondsPerKilometer.toInt()
}
```

### Average Cadence (steps per minute)

```kotlin
private fun calculateAvgCadence(
    totalSteps: Long,
    durationInSeconds: Int,
): Int {
    if (durationInSeconds <= 0) return 0

    val durationInMinutes = durationInSeconds / 60.0
    return (totalSteps / durationInMinutes).toInt()
}
```

**Key principles**:
- Always check for division by zero
- Return 0 for invalid/impossible values
- Use consistent units (meters, seconds)
- Recalculate periodically (e.g., every 3 seconds)

## AndroidManifest Configuration

**Required in app module's AndroidManifest.xml**:

```xml
<manifest>
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACTIVITY_RECOGNITION" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

    <application>
        <service
            android:name="com.dpm.sixpack.runningservice.RunningService"
            android:foregroundServiceType="location"
            android:exported="false" />
    </application>
</manifest>
```

**Key principles**:
- Declare `android:foregroundServiceType="location"` (required Android 10+)
- Set `android:exported="false"` for internal service
- Request all necessary permissions

## Testing Considerations

Testing foreground services is challenging but important:

```kotlin
@Test
fun `when START action then service starts in foreground`() {
    val scenario = ServiceScenario.launch(RunningService::class.java)

    scenario.onService { service ->
        val intent = Intent().apply {
            action = RunningActions.START_OR_RESUME
        }
        service.onStartCommand(intent, 0, 0)

        // Verify foreground state
        assertNotNull(service.runningDataState.value)
    }
}
```

**Mock Use Cases for Testing**:
```kotlin
class FakeGpsDataUseCase : GetGpsDataUseCase {
    override fun invoke(): Flow<DoRunResult<Location>> = flow {
        emit(DoRunResult.Success(mockLocation))
    }
}
```

## Common Mistakes to Avoid

### ❌ DON'T: Use GlobalScope for jobs

```kotlin
GlobalScope.launch {
    // Bad: Service might stop before this completes
}
```

### ✅ DO: Use lifecycleScope

```kotlin
lifecycleScope.launch {
    // Good: Automatically cancelled when service destroyed
}
```

### ❌ DON'T: Forget to cancel jobs

```kotlin
private fun pauseService() {
    isPaused = true
    // Missing: cancelJobs()
}
```

### ✅ DO: Always cancel jobs on pause/stop

```kotlin
private fun pauseService() {
    isPaused = true
    cancelJobs()
}
```

### ❌ DON'T: Start foreground service without notification

```kotlin
private fun startService() {
    startJobs() // Bad: No notification
}
```

### ✅ DO: Call startForeground immediately

```kotlin
private fun startService() {
    createNotificationChannel()
    startForeground(NOTIFICATION_ID, notification)
    startJobs()
}
```

### ❌ DON'T: Forget to save state before pause

```kotlin
private fun pauseService() {
    isPaused = true
    // Missing: stepsBeforePause = currentSteps
}
```

### ✅ DO: Save accumulated values

```kotlin
private fun pauseService() {
    isPaused = true
    stepsBeforePause = currentSteps
    cancelJobs()
}
```

### ❌ DON'T: Update state when paused

```kotlin
getGpsDataUseCase().collect { result ->
    lastLocation = result.data // Bad: Updates even when paused
}
```

### ✅ DO: Check pause state before updating

```kotlin
getGpsDataUseCase().collect { result ->
    if (!isPaused) {
        lastLocation = result.data
    }
}
```

## Common Patterns

### 1. Binding to Service from Composable

```kotlin
@Composable
fun RunningScreen() {
    val context = LocalContext.current
    var service by remember { mutableStateOf<RunningService?>(null) }

    val serviceConnection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                val runningBinder = binder as RunningService.RunningBinder
                service = runningBinder.getService()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                service = null
            }
        }
    }

    DisposableEffect(Unit) {
        Intent(context, RunningService::class.java).also { intent ->
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        onDispose {
            context.unbindService(serviceConnection)
        }
    }

    val runningData by service?.runningDataState?.collectAsState()

    // Use runningData
}
```

### 2. Controlling Service from ViewModel

```kotlin
class RunningViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel() {

    fun startRunning() {
        Intent(context, RunningService::class.java).also {
            it.action = RunningActions.START_OR_RESUME
            context.startService(it)
        }
    }

    fun pauseRunning() {
        Intent(context, RunningService::class.java).also {
            it.action = RunningActions.PAUSE
            context.startService(it)
        }
    }

    fun stopRunning() {
        Intent(context, RunningService::class.java).also {
            it.action = RunningActions.STOP
            context.startService(it)
        }
    }
}
```

## Summary Checklist

When working with the background module, verify:

- [ ] Service extends `LifecycleService` and has `@AndroidEntryPoint`
- [ ] Service declared in AndroidManifest with `foregroundServiceType="location"`
- [ ] Foreground notification created before `startForeground()`
- [ ] Notification channel created with appropriate importance
- [ ] All jobs use `lifecycleScope.launch`
- [ ] Jobs stored in variables and cancelled on pause/stop
- [ ] State saved before pausing (e.g., `stepsBeforePause`)
- [ ] State checks (`!isPaused`) before updates
- [ ] Public state exposed as `StateFlow` (not `MutableStateFlow`)
- [ ] Service properly stops with `stopForeground()` and `stopSelf()`
- [ ] Required permissions declared in manifest
- [ ] Binder pattern implemented for UI binding
- [ ] Calculations handle edge cases (division by zero)
- [ ] Use cases injected with Hilt
