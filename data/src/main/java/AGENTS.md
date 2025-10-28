# API Layer Generation Prompt (SixPack Project)

## Objective
Based on a new API endpoint's specifications, generate **all necessary files** across the `data` and `domain` modules. This includes **DTOs**, **Domain Models**, the **Retrofit Service** function, the **DataSource** function, the **Repository Interface**, the **Repository Implementation Skeleton**, and related **DI Module** entries.

Adhere strictly to the SixPack project's Clean Architecture and coding conventions as defined in the examples below.

## Instructions
You will be given a set of inputs defining a single API endpoint. Your task is to generate or update all corresponding code blocks/files (`DTO`, `Domain`, `Service`, `DataSource`, `Repository Interface`, `Repository Impl`, `DI Modules`) **in a single response**.

Follow the precise structure, naming conventions, and file paths shown in the examples.

### Input Format
You will be provided inputs in the following format:
* **API**: [HTTP_METHOD] [URI_PATH] (e.g., `POST /api/runs/sessions/{sessionId}/finish`)
* **Service**: [InterfaceName] (e.g., `RunningSessionService`)
* **Parameters**: (Optional) [Annotation: "key": type] (e.g., `@Path: "sessionId": Long`, `@Body: "request": FinishRunningRequestDto`)
* **Request JSON**: (Optional) [JSON data for the request body]
* **Response JSON**: (Optional) [JSON data for the response]

---

## Important Rules

### Ruleset A: Data Model Generation (DTO & Domain)
1.  **DTO Location**: All DTOs must be generated for the `data` module, under a path like `data/source/remote/dto/[request_or_response]/`.
2.  **DTO Annotations**: All DTOs **must** use `@Serializable` and **must** use `@SerialName("json_key_name")` for *every* property.
3.  **DTO Mapper**: The primary Response DTO **must** contain an internal mapper function (e.g., `fun toDomainModel(): DomainModel`) that converts it to the corresponding `Domain Model`.
4.  **Single DTO File**: All DTOs related to a single API (including nested classes) **must** be placed in a **single** Kotlin file.
5.  **Domain Model Location**: All Domain Models must be generated for the `domain` module, under a path like `domain/model/`.
6.  **Domain Model Purity**: Domain Models **must** be pure Kotlin `data class`es with **no** annotations or mapper functions.
7.  **Single Domain File**: All closely related domain models **must** be placed in a **single** Kotlin file.
8.  Do not use the unnecessary `this` keyword in mappers.
9.  If the provided JSON response or request is a top-level empty object (`{}`), respond that no DTO/Model is needed, but **still generate Service/DataSource/Repository functions** that return appropriate types like `BaseResponse<Unit>` and `DoRunResult<Unit>`.
10. **Reuse Existing Models**: If a JSON structure perfectly matches an *example* in this prompt, reuse that exact class name.

### Ruleset B: API Layer Generation (Service, DataSource)
11. **Service Interface**:
    * Functions **must** be `suspend`.
    * Functions **must** return `BaseResponse<DtoName>` (or `BaseResponse<Unit>` if response is empty).
    * Must use Retrofit annotations (`@GET`, `@POST`, `@Path`, `@Body`, etc.).
    * If the target `Service` interface does not exist, create a new file for it. If it exists, add the new function signature.
12. **DataSource Class**:
    * Must contain a `suspend` function corresponding to the `Service` function.
    * The `DataSource` class **must** be named `[ServiceName]DataSource` (e.g., `RunningSessionDataSource`).
    * It **must** have an `@Inject constructor` injecting the corresponding `Service`.
    * **Function Parameters**:
        * For `@Path`, `@Query`, `@Header`, etc., parameters in the `Service` function, the `DataSource` function **must** accept the same parameters directly.
        * **For `@Body` parameters in the `Service` function (e.g., `@Body request: MyRequestDto`), the `DataSource` function MUST accept *all individual fields* defined within `MyRequestDto` as separate parameters.** The `DataSource` function will then be responsible for constructing the `MyRequestDto` instance internally before calling the `Service`.
    * The `DataSource` function **must** call the corresponding `Service` function and return its result (`BaseResponse<DtoName>`).
    * If the `DataSource` file does not exist, create a new file for it. If it exists, add the new function.
    * **No DI module generation** is needed for DataSources.

### Ruleset C: Repository Layer Generation
13. **Repository Interface Location**: The corresponding `Repository` interface **must** be generated in the `domain` module, under a path like `domain/repository/`.
14. **Repository Interface Naming**: The interface **must** be named `[FeatureName]Repository` (e.g., `RunningGoalRepository`).
15. **Repository Interface Method**:
    * Define a method signature matching the DataSource function's purpose.
    * The method **must** return `DoRunResult<DomainModelName>` (or `DoRunResult<Unit>` if response is empty) for `suspend` functions, or `Flow<DoRunResult<DomainModelName>>` for functions returning a Flow. Use the *Domain Model*, not the DTO. (Assume `suspend` unless specified otherwise).
    * The exact package for `DoRunResult` is `com.dpm.sixpack.domain.util.DoRunResult`.
    * If the interface file does not exist, create it. If it exists, add the new method signature.
16. **Repository Implementation Location**: A skeleton `RepositoryImpl` class **must** be generated in the `data` module, under a path like `data/repository/`.
17. **Repository Implementation Naming**: The class **must** be named `[FeatureName]RepositoryImpl` (e.g., `RunningGoalRepositoryImpl`).
18. **Repository Implementation Structure**:
    * The class **must** implement the corresponding `Repository` interface.
    * It **must** have an `@Inject constructor` injecting its required `DataSource`(s).
    * It must provide strictly skeleton override implementations for all interface methods.
    * The function body must contain ONLY the comment // TODO: Implement and NOTHING else. Do NOT add any implementation logic, such as data mapping, calling DataSource functions, or wrapping results.
    * If the function requires a return value, use throw NotImplementedError("Implement Repository Logic") immediately after the // TODO comment.

### Ruleset D: Dependency Injection Generation
19. **Service DI Module (`ServiceModule.kt`)**: If you create a **new** `Service` interface (e.g., `NewService`), you **must** also generate the `@Provides` function for it inside the `ServiceModule` located at `data/source/remote/service/di/ServiceModule.kt`. If the file or module object does not exist, create them. If adding to an existing `Service`, do not generate DI code.
20. **Repository DI Module (`RepositoryModule.kt`)**: A `@Binds` function binding the `Repository` interface to its `RepositoryImpl` **must** be generated or added to the `RepositoryModule` located at `data/repository/di/RepositoryModule.kt`. If the file or module object does not exist, create them.

---

## Base Types to Use

* **BaseResponse Structure**: Assume the following structure for `BaseResponse`:
    ```kotlin
//    package com.dpm.sixpack.data.source.remote.util.base

    @Serializable
    data class BaseResponse<T>(
        @SerialName("status") val status: String,
        @SerialName("message") val message: String,
        @SerialName("timestamp") val timestamp: String,
        @SerialName("data") val data: T? = null,
    )
    ```
* **DoRunResult Structure**: Assume the following structure for `DoRunResult`:
    ```kotlin
//    package com.dpm.sixpack.domain.util

    sealed class DoRunResult<out T> {
        data class Success<T>(val data: T) : DoRunResult<T>()
        data class Failure(val exception: DoRunException) : DoRunResult<Nothing>()
        // Assume helper functions like isSuccess, getOrNull, etc., exist
    }
    ```

---

## Generation Steps (Complete Example Flow)

### 1. Example Input
* **API**: `GET /api/runs/goals/latest`
* **Service**: `RunningGoalService`
* **Parameters**: None
* **Request JSON**: None
* **Response JSON**:
    ```json
    {
      "id": 1,
      "createdAt": "2025-10-20T10:00:00Z",
      "updatedAt": "2025-10-20T10:00:00Z",
      "pausedAt": null,
      "clearedAt": null,
      "title": "오늘의 목표",
      "subTitle": "가볍게 뛰기",
      "type": "FREE",
      "pace": 540,
      "distance": 3000,
      "duration": 1800,
      "totalRoundCount": 1,
      "clearedRoundCount": 0
    }
    ```
2. Example Output (AI should generate all of this)
   Step 1: Generate Domain Model
   Kotlin

// File: domain/model/TodayRunningGoal.kt
// package com.dpm.sixpack.domain.model

data class TodayRunningGoal(
val id: Long,
val createdAt: String,
val updatedAt: String,
val pausedAt: String?,
val clearedAt: String?,
val title: String,
val subTitle: String?,
val type: String,
val pace: Int,
val distance: Int,
val duration: Int,
val totalRoundCount: Int,
val clearedRoundCount: Int,
)
Step 2: Generate DTO (with Mapper)
Kotlin

// File: data/source/remote/dto/response/TodayGoalResponseDto.kt
// package com.dpm.sixpack.data.source.remote.dto.response

// Assume domain model is accessible
// Assume kotlinx.serialization.* are available

@Serializable
data class TodayGoalResponseDto(
@SerialName("id") val id: Long,
@SerialName("createdAt") val createdAt: String,
@SerialName("updatedAt") val updatedAt: String,
@SerialName("pausedAt") val pausedAt: String? = null,
@SerialName("clearedAt") val clearedAt: String? = null,
@SerialName("title") val title: String,
@SerialName("subTitle") val subTitle: String? = null,
@SerialName("type") val type: String,
@SerialName("pace") val pace: Int,
@SerialName("distance") val distance: Int,
@SerialName("duration") val duration: Int,
@SerialName("totalRoundCount") val totalRoundCount: Int,
@SerialName("clearedRoundCount") val clearedRoundCount: Int,
) {
fun toTodayRunningGoal() =
TodayRunningGoal(
id = id,
createdAt = createdAt,
updatedAt = updatedAt,
pausedAt = pausedAt,
clearedAt = clearedAt,
title = title,
subTitle = subTitle,
type = type,
pace = pace,
distance = distance,
duration = duration,
totalRoundCount = totalRoundCount,
clearedRoundCount = clearedRoundCount,
)
}
Step 3: Generate Service Interface (New File)
Kotlin

// File: data/source/remote/service/RunningGoalService.kt
// package com.dpm.sixpack.data.source.remote.service

// Assume BaseResponse and DTOs are accessible
// Assume Retrofit annotations are available

interface RunningGoalService {
@GET("/api/runs/goals/latest") // Example path, adjust as needed
suspend fun getTodayRunningGoal(): BaseResponse<TodayGoalResponseDto>
}

Step 4: Generate DataSource (New File or Update)
// File: data/source/remote/datasoruce/RunningGoalDataSource.kt
// package com.dpm.sixpack.data.source.remote.datasoruce


// Example for GET (no @Body parameter)
class RunningGoalDataSource @Inject constructor(
private val runningGoalService: RunningGoalService,
) {
suspend fun getTodayRunningGoal(): BaseResponse<TodayGoalResponseDto> =
runningGoalService.getTodayRunningGoal() // Direct call
}

// Example for POST (with @Body parameter in Service)
class FeedbackDataSource @Inject constructor(
private val feedbackService: FeedbackService, // Assumed Service
) {
suspend fun postFeedback(
// Individual fields from FeedbackRequestDto are passed here
rating: Int,
comment: String,
): BaseResponse<Unit> { // Assuming Unit for empty response
// Construct the DTO internally
val requestDto = FeedbackRequestDto(
rating = rating,
comment = comment
)
// Call the service with the constructed DTO
return feedbackService.postFeedback(request = requestDto)
}
}
Step 5: Generate Service DI Module Entry (Update Existing File)
Kotlin

// File: data/source/remote/service/di/ServiceModule.kt
// package com.dpm.sixpack.data.source.remote.service.di

// Assume Dagger annotations, Retrofit, Service interfaces are accessible

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
// [AI] RunningGoalService is new, adding provider.
@Provides
@Singleton
fun provideRunningGoalService(retrofit: Retrofit): RunningGoalService =
retrofit.create(RunningGoalService::class.java)

    // Assuming RunningSessionService already exists and provided here
    // @Provides
    // @Singleton
    // fun provideRunningSessionService(retrofit: Retrofit): RunningSessionService =
    //     retrofit.create(RunningSessionService::class.java)
}
Step 6: Generate Repository Interface (New File)
Kotlin

// File: domain/repository/RunningGoalRepository.kt
// package com.dpm.sixpack.domain.repository

// Assume DoRunResult and Domain Models are accessible

interface RunningGoalRepository {
suspend fun getTodayRunningGoal(): DoRunResult<TodayRunningGoal>
}
Step 7: Generate Repository Implementation Skeleton (New File)
Kotlin

// File: data/repository/RunningGoalRepositoryImpl.kt
// package com.dpm.sixpack.data.repository

// Assume Inject, DataSource, Repository Interface, DoRunResult, Domain Models are accessible

class RunningGoalRepositoryImpl @Inject constructor(
private val runningGoalDataSource: RunningGoalDataSource
) : RunningGoalRepository {
override suspend fun getTodayRunningGoal(): DoRunResult<TodayRunningGoal> {
// TODO: Implement
throw NotImplementedError("Implement Repository Logic")
}
}
Step 8: Generate Repository DI Module Entry (New File or Update)
Kotlin

// File: data/repository/di/RepositoryModule.kt
// package com.dpm.sixpack.data.repository.di

// Assume Dagger annotations, Repository Interfaces, Impl classes are accessible

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRunningGoalRepository(
        runningGoalRepositoryImpl: RunningGoalRepositoryImpl
    ): RunningGoalRepository

    // Add other repository bindings here
}
