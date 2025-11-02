# Domain Module - AI Agent Guidelines

This document provides guidance for AI agents (LLMs) when writing code for the domain layer of the SixPack Android application.

## Overview

The domain module implements **Clean Architecture** business logic. It is framework-agnostic, written in pure Kotlin (except for `android.location.Location`), and defines business rules, use cases, and repository contracts.

**Base Package**: `com.dpm.sixpack.domain`

## Package Structure

```
domain/
├── exception/          # Custom exception hierarchy
├── model/              # Domain models and data classes
│   └── params/         # Parameter objects for use cases
├── repository/         # Repository interfaces (implemented in data layer)
├── usecase/            # Business logic use cases
└── util/               # Utility classes (DoRunResult, constants)
```

## Core Patterns

### 1. Result Wrapper Pattern - `DoRunResult<T>`

**ALWAYS** use `DoRunResult<T>` for operations that can fail. Never throw exceptions directly to the presentation layer.

```kotlin
sealed class DoRunResult<out T> {
    data class Success<T>(val data: T) : DoRunResult<T>()
    data class Failure(val exception: DoRunException) : DoRunResult<Nothing>()
}
```

**When to use**:
- All repository methods that can fail
- All use cases that perform I/O operations
- Any operation with business rule validation

**Example**:
```kotlin
suspend fun someOperation(): DoRunResult<DataType> = try {
    val result = repository.fetch()
    result.fold(
        onSuccess = { data -> DoRunResult.Success(data) },
        onError = { exception -> DoRunResult.Failure(exception) }
    )
} catch (e: Exception) {
    DoRunResult.Failure(
        DoRunException.UnknownError(e.message ?: "Unknown error", e)
    )
}
```

### 2. Use Case Pattern

**Structure**: All use cases follow this pattern:

```kotlin
class FeatureNameUseCase @Inject constructor(
    private val repository: SomeRepository,
    private val otherRepository: OtherRepository,
) {
    suspend operator fun invoke(param: ParamType): DoRunResult<ResultType> {
        // Business logic here
    }
}
```

**Key principles**:
- Constructor injection with `@Inject`
- Single responsibility (one business operation)
- `operator fun invoke()` as entry point
- Return `DoRunResult<T>` for error-prone operations
- Use `@Singleton` ONLY for stateful use cases (GPS, sensors, state management)

**Singleton Guidelines**:
```kotlin
// Use @Singleton for stateful use cases
@Singleton
class GetRealtimeRunningDataUseCase @Inject constructor(
    private val gpsRepository: GpsRepository,
    private val sensorRepository: SensorRepository,
)

// DO NOT use @Singleton for stateless use cases
class StartRunningUseCase @Inject constructor(
    private val runningSessionRepository: RunningSessionRepository,
)
```

### 3. Repository Interface Pattern

**Structure**: Define contracts without implementation details:

```kotlin
interface FeatureRepository {
    // Single async operation
    suspend fun performAction(param: ParamType): DoRunResult<ResultType>

    // Reactive stream
    fun observeData(): Flow<DoRunResult<DataType>>

    // Synchronous operation (use sparingly)
    fun quickCheck(): Boolean
}
```

**Key principles**:
- Interface only (implementation in data layer)
- Methods return `DoRunResult<T>` for error-prone operations
- Use `Flow` for continuous streams (GPS, sensors, database queries)
- Use `suspend` for single async operations
- Pure Kotlin (no Android dependencies except `android.location.Location`)

## Exception Hierarchy

**ALWAYS** use these exception types:

```kotlin
sealed class DoRunException(message: String, cause: Throwable? = null)

├── NetworkError          // Network connectivity issues
├── ServerError          // HTTP 5xx errors (includes status code)
├── DataError            // Parsing, validation, conversion errors
├── BusinessError        // Business rule violations (optional error code)
└── UnknownError         // Catch-all for unexpected errors
```

**Example usage**:
```kotlin
// Business validation error
if (param.isInvalid) {
    return DoRunResult.Failure(
        DoRunException.BusinessError("Invalid parameter", errorCode = "INVALID_PARAM")
    )
}

// Catch unexpected errors
try {
    // operation
} catch (e: Exception) {
    DoRunResult.Failure(DoRunException.UnknownError(e.message ?: "Unknown", e))
}
```

## Domain Models

### Creating Domain Models

**Guidelines**:
- Use `data class` for all domain models
- Immutable by default (use `val`)
- Include computed properties if needed
- No Android framework types
- Use meaningful property names with full words (avoid abbreviations)

**Example**:
```kotlin
data class RunningSession(
    val id: Long,
    val startTime: Long,              // Unix timestamp
    val endTime: Long,                // Unix timestamp
    val durationMs: Long,             // Duration in milliseconds
    val distanceMeter: Double,        // Distance in meters
    val avgPace: Int,                 // Seconds per km
) {
    // Computed properties
    val durationMinutes: Double
        get() = durationMs / 60_000.0

    val distanceKm: Double
        get() = distanceMeter / 1000.0
}
```

### Common Domain Models

**RealtimeRunningData**: Current running metrics during a session
```kotlin
data class RealtimeRunningData(
    val latitude: Double,
    val longitude: Double,
    val pace: Int,                    // seconds per km
    val cadence: Int,                 // steps per minute
    val totalDistanceMeter: Int,
    val duration: Int,                // seconds
    val timestamp: Long,
)
```

**RunRecord**: Completed running session
```kotlin
data class RunRecord(
    val sessionId: Long,
    val startTime: Long,
    val endTime: Long,
    val durationMs: String,
    val course: Course?,
)
```

**Course**: GPS path for a session
```kotlin
data class Course(
    val id: Long,
    val name: String,
    val distanceMeter: Double,
    val path: List<SimpleLocation>,
)
```

## Code Patterns

### 1. Creating a New Use Case

```kotlin
class NewFeatureUseCase @Inject constructor(
    private val repository: SomeRepository,
    private val preferenceRepository: UserPreferenceRepository,
) {
    suspend operator fun invoke(param: ParamType): DoRunResult<ResultType> {
        // 1. Validate input
        if (param.isInvalid) {
            return DoRunResult.Failure(
                DoRunException.BusinessError("Validation failed")
            )
        }

        // 2. Perform business logic
        return repository.performOperation(param)
            .onSuccess { data ->
                // Side effects (optional)
                preferenceRepository.saveState(data)
            }
    }
}
```

### 2. Combining Multiple Repository Calls

**Sequential operations** (one depends on another):
```kotlin
suspend fun invoke(): DoRunResult<Result> {
    // Get session ID first
    val sessionId = preferenceRepository.getSessionId()
        ?: return DoRunResult.Failure(
            DoRunException.BusinessError("No active session")
        )

    // Then use it
    return repository.finishSession(sessionId)
        .onSuccess {
            preferenceRepository.clearSessionId()
        }
}
```

**Independent operations** (both needed):
```kotlin
suspend fun invoke(): DoRunResult<Result> {
    val result1 = repository1.fetchData()
    val result2 = repository2.fetchData()

    return when {
        result1 is DoRunResult.Success && result2 is DoRunResult.Success -> {
            val combined = combine(result1.data, result2.data)
            DoRunResult.Success(combined)
        }
        result1 is DoRunResult.Failure -> result1
        else -> result2
    }
}
```

### 3. Flow Combination Pattern

For real-time data streams:

```kotlin
@Singleton
class GetRealtimeDataUseCase @Inject constructor(
    private val gpsRepository: GpsRepository,
    private val sensorRepository: SensorRepository,
) {
    operator fun invoke(): Flow<DoRunResult<RealtimeData>> {
        return combine(
            gpsRepository.getLocationFlow(),
            sensorRepository.getStepFlow(),
        ) { locationResult, stepResult ->
            // Both must succeed
            if (locationResult is DoRunResult.Success &&
                stepResult is DoRunResult.Success) {

                val data = RealtimeData(
                    location = locationResult.data,
                    steps = stepResult.data,
                )
                DoRunResult.Success(data)
            } else {
                // Return first failure
                (locationResult as? DoRunResult.Failure)
                    ?: (stepResult as DoRunResult.Failure)
            }
        }
    }
}
```

### 4. Distance Accumulation Pattern

For tracking cumulative metrics:

```kotlin
fun trackDistance(): Flow<Double> {
    var previousLocation: Location? = null
    var totalDistance = 0.0

    return gpsRepository.getLocationFlow()
        .map { result ->
            if (result is DoRunResult.Success) {
                val currentLocation = result.data

                previousLocation?.let { prev ->
                    totalDistance += prev.distanceTo(currentLocation)
                }
                previousLocation = currentLocation

                totalDistance
            } else {
                totalDistance
            }
        }
}
```

### 5. Error Handling with Fold

```kotlin
suspend fun invoke(): DoRunResult<Data> {
    return repository.fetch().fold(
        onSuccess = { data ->
            // Transform or validate
            if (data.isValid) {
                DoRunResult.Success(data.transform())
            } else {
                DoRunResult.Failure(
                    DoRunException.BusinessError("Invalid data")
                )
            }
        },
        onError = { exception ->
            // Log, wrap, or pass through
            DoRunResult.Failure(exception)
        }
    )
}
```

## Repository Guidelines

### Creating a New Repository Interface

```kotlin
interface NewFeatureRepository {
    /**
     * Performs some operation with parameters.
     *
     * @param param Input parameter
     * @return Success with result or Failure with error
     */
    suspend fun performOperation(param: ParamType): DoRunResult<ResultType>

    /**
     * Observes continuous data updates.
     *
     * @return Flow emitting Success with data or Failure with error
     */
    fun observeData(): Flow<DoRunResult<DataType>>

    /**
     * Quick synchronous check (use sparingly).
     */
    fun isFeatureEnabled(): Boolean
}
```

**Key principles**:
- Document each method with KDoc
- Use `suspend` for async operations
- Use `Flow` for streams
- Return `DoRunResult` for operations that can fail
- Avoid blocking operations

### Repository Methods by Purpose

**Starting operations**:
```kotlin
suspend fun start(param: StartParam): DoRunResult<Long> // Returns ID
```

**Finishing operations**:
```kotlin
suspend fun finish(id: Long): DoRunResult<SummaryResult>
```

**Saving data**:
```kotlin
suspend fun save(data: DataType): DoRunResult<Unit>
```

**Querying data**:
```kotlin
fun getAll(): Flow<List<DataType>>
fun getById(id: Long): Flow<DataType>
suspend fun findById(id: Long): DoRunResult<DataType>
```

**Real-time streams**:
```kotlin
fun getLocationFlow(): Flow<DoRunResult<Location>>
fun getSensorFlow(): Flow<DoRunResult<SensorData>>
```

## Existing Repositories

### RunningSessionRepository
- Manages remote session lifecycle (start, finish, sync)
- Use for: API operations, data synchronization

### RunningRepository
- Manages local session data and courses
- Use for: Database operations, local storage

### GpsRepository
- Provides GPS location stream
- Use for: Location tracking

### SensorRepository
- Provides step counter data
- Use for: Step counting, sensor access

### UserPreferenceRepository
- Manages user preferences and session state
- Use for: DataStore/SharedPreferences operations

## Testing Considerations

The domain layer is designed for easy testing:

```kotlin
@Test
fun `when input is invalid then returns business error`() = runTest {
    // Given
    val invalidParam = ParamType(value = -1)
    val mockRepo = mock<Repository>()
    val useCase = UseCase(mockRepo)

    // When
    val result = useCase.invoke(invalidParam)

    // Then
    assertThat(result).isInstanceOf(DoRunResult.Failure::class.java)
    assertThat((result as DoRunResult.Failure).exception)
        .isInstanceOf(DoRunException.BusinessError::class.java)
}
```

## Common Mistakes to Avoid

### ❌ DON'T: Throw exceptions directly
```kotlin
suspend fun invoke(): Data {
    throw IllegalStateException("Error")
}
```

### ✅ DO: Use DoRunResult
```kotlin
suspend fun invoke(): DoRunResult<Data> {
    return DoRunResult.Failure(
        DoRunException.BusinessError("Error")
    )
}
```

### ❌ DON'T: Use Android framework classes
```kotlin
data class UserProfile(
    val photo: Bitmap  // Android class
)
```

### ✅ DO: Use pure Kotlin or primitives
```kotlin
data class UserProfile(
    val photoUrl: String
)
```

### ❌ DON'T: Add implementation details in repository interface
```kotlin
interface Repository {
    fun getData(): Flow<DoRunResult<Data>> {
        return database.query()  // Implementation!
    }
}
```

### ✅ DO: Keep interfaces abstract
```kotlin
interface Repository {
    fun getData(): Flow<DoRunResult<Data>>
}
```

### ❌ DON'T: Make all use cases singletons
```kotlin
@Singleton  // Unnecessary for stateless use case
class SaveDataUseCase @Inject constructor(...)
```

### ✅ DO: Only use @Singleton for stateful use cases
```kotlin
@Singleton  // Necessary for GPS stream management
class GetGpsDataUseCase @Inject constructor(...)

// No @Singleton for stateless operations
class SaveDataUseCase @Inject constructor(...)
```

## Architecture Principles

### 1. Clean Architecture Compliance
- No Android framework dependencies (except `android.location.Location`)
- Pure Kotlin
- Dependency inversion (depend on interfaces, not implementations)
- Single responsibility per use case

### 2. Error Handling Strategy
- Use `DoRunResult<T>` wrapper for all error-prone operations
- Typed exceptions with `DoRunException` hierarchy
- Functional error handling with `.fold`, `.onSuccess`, `.onError`

### 3. Data Flow Patterns
- Reactive streams with `Flow` for continuous data
- Suspend functions for single async operations
- StateFlow in service layer (not in domain directly)

### 4. Dependency Management
- Constructor injection with Hilt
- No DI modules in domain (modules in data layer)
- Use cases can compose other use cases or repositories

## Naming Conventions

**Use Cases**: `{Action}{Entity}UseCase`
- `StartRunningUseCase`
- `FinishRunningSessionUseCase`
- `GetRealtimeRunningDataUseCase`

**Repositories**: `{Entity}Repository`
- `RunningRepository`
- `UserPreferenceRepository`
- `GpsRepository`

**Models**: `{Entity}` or `{Purpose}{Entity}`
- `RunRecord`
- `RealtimeRunningData`
- `RunningSessionResult`

**Exceptions**: `{Type}Error`
- `NetworkError`
- `ServerError`
- `BusinessError`

## Metrics and Units

**Always use consistent units**:
- Distance: meters (`distanceMeter: Double`)
- Duration: seconds or milliseconds (`durationSec: Int`, `durationMs: Long`)
- Pace: seconds per kilometer (`pace: Int`)
- Cadence: steps per minute (`cadence: Int`)
- Speed: meters per second (`speed: Float`)
- Timestamps: Unix timestamp in milliseconds (`timestamp: Long`)

## Summary Checklist

When creating new domain code, verify:

- [ ] Uses `DoRunResult<T>` for operations that can fail
- [ ] Uses appropriate `DoRunException` type for errors
- [ ] Use case has single responsibility
- [ ] Repository is interface only (no implementation)
- [ ] Domain models are pure Kotlin data classes
- [ ] No Android framework dependencies
- [ ] Proper use of `@Singleton` (only for stateful use cases)
- [ ] Constructor injection with `@Inject`
- [ ] `operator fun invoke()` for use case entry point
- [ ] Consistent naming conventions
- [ ] Proper units for metrics
- [ ] KDoc comments for public APIs
