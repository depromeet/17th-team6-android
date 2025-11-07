# Core Module - AI Agent Guidelines

This document provides guidance for AI agents (LLMs) when working with the core module of the SixPack Android application.

## Overview

The core module provides **shared utilities, configurations, and base infrastructure** used across all other modules. It contains build configuration access, coroutine dispatchers, network monitoring, permission utilities, and time utilities.

**Base Package**: `com.dpm.sixpack.core`

## Package Structure

```
core/
├── configs/                 # Build configuration providers
│   ├── BuildConfigProvider.kt
│   └── ConfigModule.kt
├── di/                      # Dependency injection modules
│   └── MonitorModule.kt
├── network/                 # Network and coroutine utilities
│   ├── SixPackDispatchers.kt
│   └── di/
│       ├── DispatchersModule.kt
│       ├── CoroutineScopesModule.kt
│       └── NetworkModule.kt
├── permission/              # Permission utilities
│   ├── SixPackPermissions.kt
│   └── PermissionUtil.kt
└── util/                    # General utilities
    ├── NetworkMonitor.kt
    ├── TimeUtil.kt
    └── TimeZoneMonitor.kt
```

## Core Components

### 1. Build Configuration Provider

Provides safe access to BuildConfig values across modules.

**Interface** (`configs/BuildConfigProvider.kt`):
```kotlin
interface BuildConfigProvider {
    fun getNaverMapClientId(): String
    fun getBaseUrl(): String
    // Add more methods for other build config values
}
```

**Implementation**:
```kotlin
class BuildConfigProviderImpl @Inject constructor() : BuildConfigProvider {
    override fun getNaverMapClientId(): String = BuildConfig.NAVERMAP_CLIENT_ID
    override fun getBaseUrl(): String = BuildConfig.BASE_URL
}
```

**DI Module** (`configs/ConfigModule.kt`):
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class ConfigModule {
    @Binds
    @Singleton
    abstract fun bindBuildConfigProvider(
        buildConfigProviderImpl: BuildConfigProviderImpl
    ): BuildConfigProvider
}
```

**Usage in other modules**:
```kotlin
class SomeRepository @Inject constructor(
    private val buildConfigProvider: BuildConfigProvider,
) {
    fun getSdkKey() = buildConfigProvider.getNaverMapClientId()
}
```

**Key principles**:
- Never access `BuildConfig` directly from other modules
- Add a method to `BuildConfigProvider` interface for each config value
- Use dependency injection to access configuration

### 2. Coroutine Dispatchers

Provides qualified coroutine dispatchers for dependency injection.

**Dispatcher Qualifier** (`network/SixPackDispatchers.kt`):
```kotlin
@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(
    val dispatcher: SixPackDispatchers,
)

enum class SixPackDispatchers {
    Default,
    IO,
}
```

**DI Module** (`network/di/DispatchersModule.kt`):
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides
    @Dispatcher(SixPackDispatchers.IO)
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Dispatcher(SixPackDispatchers.Default)
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}
```

**Usage**:
```kotlin
class SomeRepository @Inject constructor(
    @Dispatcher(SixPackDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun fetchData() = withContext(ioDispatcher) {
        // I/O operations
    }
}
```

**Key principles**:
- Always inject dispatchers, never use `Dispatchers.IO` directly
- Use `@Dispatcher(SixPackDispatchers.IO)` for I/O operations
- Use `@Dispatcher(SixPackDispatchers.Default)` for CPU-intensive work
- This enables easy testing with TestDispatchers

### 3. Coroutine Scopes

Provides application-scoped coroutine scopes for background operations.

**DI Module** (`network/di/CoroutineScopesModule.kt`):
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopesModule {
    @Provides
    @Singleton
    @ApplicationScope
    fun providesApplicationScope(
        @Dispatcher(SixPackDispatchers.Default) dispatcher: CoroutineDispatcher,
    ): CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)
}
```

**Usage**:
```kotlin
class BackgroundWorker @Inject constructor(
    @ApplicationScope private val applicationScope: CoroutineScope,
) {
    fun startBackgroundWork() {
        applicationScope.launch {
            // Long-running background work
        }
    }
}
```

**Key principles**:
- Use `@ApplicationScope` for app-lifetime operations
- Don't use `GlobalScope` - always inject scopes
- Use `SupervisorJob()` to prevent child failures from cancelling parent

### 4. Network Monitor

Monitors network connectivity state.

**Interface** (`util/NetworkMonitor.kt`):
```kotlin
interface NetworkMonitor {
    val isOnline: Flow<Boolean>
}
```

**Implementation** (`util/ConnectivityManagerNetworkMonitor.kt`):
```kotlin
class ConnectivityManagerNetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatcher(SixPackDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : NetworkMonitor {
    override val isOnline: Flow<Boolean> = callbackFlow {
        val connectivityManager = context.getSystemService<ConnectivityManager>()
        // Implementation using ConnectivityManager callbacks
    }.flowOn(ioDispatcher)
}
```

**DI Module** (`di/MonitorModule.kt`):
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class MonitorModule {
    @Binds
    abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor
}
```

**Usage**:
```kotlin
class SomeViewModel @Inject constructor(
    private val networkMonitor: NetworkMonitor,
) : ViewModel() {
    val isOnline = networkMonitor.isOnline
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )
}
```

### 5. Permission Utilities

Centralized permission management.

**Permission Constants** (`permission/SixPackPermissions.kt`):
```kotlin
object SixPackPermissions {
    const val LOCATION_FINE = Manifest.permission.ACCESS_FINE_LOCATION
    const val LOCATION_COARSE = Manifest.permission.ACCESS_COARSE_LOCATION
    const val ACTIVITY_RECOGNITION = Manifest.permission.ACTIVITY_RECOGNITION

    val LOCATION_PERMISSIONS = arrayOf(
        LOCATION_FINE,
        LOCATION_COARSE,
    )

    val RUNNING_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        LOCATION_PERMISSIONS + ACTIVITY_RECOGNITION
    } else {
        LOCATION_PERMISSIONS
    }
}
```

**Permission Utility** (`permission/PermissionUtil.kt`):
```kotlin
object PermissionUtil {
    fun hasLocationPermission(context: Context): Boolean {
        return SixPackPermissions.LOCATION_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun hasRunningPermissions(context: Context): Boolean {
        return SixPackPermissions.RUNNING_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}
```

**Usage**:
```kotlin
class LocationService @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun startTracking() {
        if (PermissionUtil.hasLocationPermission(context)) {
            // Start location tracking
        }
    }
}
```

### 6. Time Utilities

Time-related utilities and timezone monitoring.

**Time Util** (`util/TimeUtil.kt`):
```kotlin
object TimeUtil {
    fun formatDuration(milliseconds: Long): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / (1000 * 60)) % 60
        val hours = (milliseconds / (1000 * 60 * 60))

        return when {
            hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
            else -> String.format("%02d:%02d", minutes, seconds)
        }
    }

    fun getCurrentTimestamp(): Long = System.currentTimeMillis()
}
```

**TimeZone Monitor** (`util/TimeZoneMonitor.kt`):
```kotlin
interface TimeZoneMonitor {
    val currentTimeZone: Flow<TimeZone>
}
```

## Common Patterns

### 1. Adding New Build Config Value

**Step 1**: Add to `local.properties`:
```properties
NEW_CONFIG_VALUE=your_value
```

**Step 2**: Add to `core/build.gradle.kts`:
```kotlin
android {
    defaultConfig {
        buildConfigField("String", "NEW_CONFIG_VALUE", "\"${properties["NEW_CONFIG_VALUE"]}\"")
    }
}
```

**Step 3**: Add method to `BuildConfigProvider`:
```kotlin
interface BuildConfigProvider {
    fun getNewConfigValue(): String
}

class BuildConfigProviderImpl @Inject constructor() : BuildConfigProvider {
    override fun getNewConfigValue(): String = BuildConfig.NEW_CONFIG_VALUE
}
```

### 2. Adding New Permission Group

**Step 1**: Define in `SixPackPermissions.kt`:
```kotlin
object SixPackPermissions {
    const val CAMERA = Manifest.permission.CAMERA
    const val READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE

    val MEDIA_PERMISSIONS = arrayOf(
        CAMERA,
        READ_STORAGE,
    )
}
```

**Step 2**: Add utility method in `PermissionUtil.kt`:
```kotlin
object PermissionUtil {
    fun hasMediaPermissions(context: Context): Boolean {
        return SixPackPermissions.MEDIA_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}
```

### 3. Creating a New Utility Class

**Guidelines**:
- Keep utilities as `object` for singleton behavior
- Use pure functions when possible (no state)
- Add comprehensive KDoc comments
- Make functions inline when appropriate

```kotlin
object StringUtil {
    /**
     * Formats a phone number to Korean format (010-1234-5678).
     *
     * @param phoneNumber Raw phone number (e.g., "01012345678")
     * @return Formatted phone number (e.g., "010-1234-5678")
     */
    fun formatPhoneNumber(phoneNumber: String): String {
        if (phoneNumber.length != 11) return phoneNumber

        return buildString {
            append(phoneNumber.substring(0, 3))
            append("-")
            append(phoneNumber.substring(3, 7))
            append("-")
            append(phoneNumber.substring(7))
        }
    }
}
```

## Build Configuration

### build.gradle.kts

```kotlin
plugins {
    alias(libs.plugins.sixpack.android.library)
    alias(libs.plugins.sixpack.hilt)
}

android {
    namespace = "com.dpm.sixpack.core"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        val properties = gradleLocalProperties(rootDir, providers)

        buildConfigField(
            "String",
            "NAVERMAP_CLIENT_ID",
            "\"${properties["NAVERMAP_CLIENT_ID"]}\""
        )
        buildConfigField(
            "String",
            "BASE_URL",
            "\"${properties["BASE_URL"]}\""
        )
    }
}

dependencies {
    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Network
    implementation(libs.androidx.core.ktx)
}
```

**Key principles**:
- Enable `buildConfig = true` to generate BuildConfig
- Read values from `local.properties` using `gradleLocalProperties`
- Use `buildConfigField` to expose values to code
- Keep dependencies minimal (no UI libraries)

### local.properties

Required configuration values:

```properties
NAVERMAP_CLIENT_ID=your_naver_map_client_id
BASE_URL=https://your-api-base-url.com
```

**IMPORTANT**:
- Never commit `local.properties` to version control
- Add `.gitignore` entry for `local.properties`
- Document required keys in README or AGENTS.md

## Testing Considerations

The core module should be highly testable:

```kotlin
@Test
fun `formatDuration formats milliseconds correctly`() {
    val result = TimeUtil.formatDuration(3661000) // 1 hour, 1 minute, 1 second
    assertEquals("1:01:01", result)
}

@Test
fun `hasLocationPermission returns true when all permissions granted`() {
    val context = mockk<Context>()
    every {
        ContextCompat.checkSelfPermission(context, any())
    } returns PackageManager.PERMISSION_GRANTED

    val result = PermissionUtil.hasLocationPermission(context)
    assertTrue(result)
}
```

## Common Mistakes to Avoid

### ❌ DON'T: Access BuildConfig directly from other modules

```kotlin
// In data or presentation module
class SomeClass {
    val key = BuildConfig.NAVERMAP_CLIENT_ID // Error!
}
```

### ✅ DO: Use BuildConfigProvider

```kotlin
class SomeClass @Inject constructor(
    private val buildConfigProvider: BuildConfigProvider,
) {
    val key = buildConfigProvider.getNaverMapClientId()
}
```

### ❌ DON'T: Use Dispatchers directly

```kotlin
suspend fun fetchData() = withContext(Dispatchers.IO) { // Bad
    // I/O operation
}
```

### ✅ DO: Inject dispatchers

```kotlin
class Repository @Inject constructor(
    @Dispatcher(SixPackDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun fetchData() = withContext(ioDispatcher) {
        // I/O operation
    }
}
```

### ❌ DON'T: Use GlobalScope

```kotlin
GlobalScope.launch { // Bad
    // Background work
}
```

### ✅ DO: Inject application scope

```kotlin
class Worker @Inject constructor(
    @ApplicationScope private val scope: CoroutineScope,
) {
    fun start() {
        scope.launch {
            // Background work
        }
    }
}
```

### ❌ DON'T: Hardcode permissions

```kotlin
if (ContextCompat.checkSelfPermission(
    context,
    "android.permission.ACCESS_FINE_LOCATION"
) == PackageManager.PERMISSION_GRANTED) { }
```

### ✅ DO: Use SixPackPermissions constants

```kotlin
if (PermissionUtil.hasLocationPermission(context)) {
    // Use location
}
```

## Summary Checklist

When working with the core module, verify:

- [ ] BuildConfig values accessed via `BuildConfigProvider` interface
- [ ] All new config values added to `local.properties` and BuildConfigProvider
- [ ] Coroutine dispatchers injected with `@Dispatcher` qualifier
- [ ] Application scope injected with `@ApplicationScope` qualifier
- [ ] Network connectivity monitored via `NetworkMonitor` interface
- [ ] Permissions centralized in `SixPackPermissions`
- [ ] Permission checks use `PermissionUtil` helper methods
- [ ] Utility classes are stateless `object`s with pure functions
- [ ] Core module has no UI dependencies (no Compose, no Activities)
- [ ] All utilities have comprehensive KDoc comments
- [ ] Unit tests cover utility functions
- [ ] Dependencies kept minimal and essential
