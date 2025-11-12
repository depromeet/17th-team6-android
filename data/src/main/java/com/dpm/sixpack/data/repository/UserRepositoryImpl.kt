package com.dpm.sixpack.data.repository

import android.util.Log
import com.dpm.sixpack.data.cache.TokenMemoryCache
import com.dpm.sixpack.data.source.local.datastore.api.UserPreferenceDataSource
import com.dpm.sixpack.data.source.remote.datasource.UserDataSource
import com.dpm.sixpack.data.source.remote.dto.request.NewFcmTokenRequestDto
import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.model.AuthEvent
import com.dpm.sixpack.domain.model.ProfileImageOption
import com.dpm.sixpack.domain.model.ProfileUpdateResponse
import com.dpm.sixpack.domain.model.UserProfile
import com.dpm.sixpack.domain.repository.UserRepository
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 사용자 관련 Repository 구현체
 * - 프로필 API (조회, 수정)
 * - 토큰 및 세션 관리 (UserPreferenceRepository 통합)
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val userPreferenceDataSource: UserPreferenceDataSource,
    private val tokenMemoryCache: TokenMemoryCache,
) : UserRepository {
    private val userId = userPreferenceDataSource.userId
    private val sessionId = userPreferenceDataSource.sessionId
    private val accessToken = userPreferenceDataSource.accessToken
    private val refreshToken = userPreferenceDataSource.refreshToken
    private val fcmDeviceToken = userPreferenceDataSource.fcmDeviceToken

    private val _authEvents = MutableSharedFlow<AuthEvent>(replay = 0, extraBufferCapacity = 1)
    override val authEvents: SharedFlow<AuthEvent> = _authEvents.asSharedFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        // 앱 시작 시 DataStore에서 토큰을 읽어 메모리 캐시에 로드
        scope.launch {
            try {
                val cachedUserId = userId.first()
                val cachedAccessToken = accessToken.firstOrNull()
                tokenMemoryCache.setTokens(cachedUserId, cachedAccessToken)
                Timber.d("UserRepository: Initial tokens loaded to memory cache")
            } catch (e: Exception) {
                Timber.e("UserRepository: Failed to load initial tokens: ${e.message}")
            }
        }
    }

    // ========== Profile API ==========
    override suspend fun getMyProfile(): DoRunResult<UserProfile> =
        try {
            val response = userDataSource.getMyProfile()

            // DTO를 Domain Model로 변환
            val userProfile = response.data?.toUserProfile()

            if (userProfile != null) {
                DoRunResult.Success(userProfile)
            } else {
                DoRunResult.Failure(
                    DoRunException.UnknownError(
                        message = "프로필 조회 응답 데이터가 없습니다.",
                    ),
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to get profile")
            DoRunResult.Failure(
                DoRunException.NetworkError(
                    message = e.message ?: "프로필 조회에 실패했습니다.",
                ),
            )
        }

    override suspend fun updateMyProfile(
        nickname: String,
        imageOption: ProfileImageOption,
        profileImage: File?,
    ): DoRunResult<ProfileUpdateResponse> =
        try {
            // imageOption을 String으로 변환
            val imageOptionStr =
                when (imageOption) {
                    ProfileImageOption.SET -> "SET"
                    ProfileImageOption.REMOVE -> "REMOVE"
                    ProfileImageOption.KEEP -> "KEEP"
                }

            // DataSource 호출
            val response =
                userDataSource.updateMyProfile(
                    nickname = nickname,
                    imageOption = imageOptionStr,
                    profileImage = profileImage,
                )

            // DTO를 Domain Model로 변환
            val profileUpdate = response.data?.toProfileUpdateResponse()

            if (profileUpdate != null) {
                DoRunResult.Success(profileUpdate)
            } else {
                DoRunResult.Failure(
                    DoRunException.UnknownError(
                        message = "프로필 수정 응답 데이터가 없습니다.",
                    ),
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to update profile")
            DoRunResult.Failure(
                DoRunException.NetworkError(
                    message = e.message ?: "프로필 수정에 실패했습니다.",
                ),
            )
        }

    // ========== User Preferences (Token & Session) ==========

    override fun getUserIdFlow(): Flow<Long> = userId

    override suspend fun getUserId(): Long = userId.first()

    override suspend fun getSessionId(): Long? = sessionId.firstOrNull()

    override suspend fun getAccessToken(): String? = accessToken.firstOrNull()

    override suspend fun getRefreshToken(): String? = refreshToken.firstOrNull()

    override suspend fun updateUserId(userId: Long) {
        userPreferenceDataSource.updateUserId(userId = userId)
        // 메모리 캐시에도 userId 업데이트
        val currentAccessToken = tokenMemoryCache.getAccessToken()
        tokenMemoryCache.setTokens(userId, currentAccessToken)
    }

    override suspend fun updateSessionId(sessionId: Long) {
        userPreferenceDataSource.updateSessionId(sessionId = sessionId)
    }

    override suspend fun clearSessionId() {
        userPreferenceDataSource.clearSessionId()
    }

    override suspend fun updateAccessToken(token: String) {
        userPreferenceDataSource.updateAccessToken(token = token)
        // 메모리 캐시에도 AccessToken 업데이트
        val currentUserId = tokenMemoryCache.getUserId()
        tokenMemoryCache.setTokens(currentUserId, token)
    }

    override suspend fun updateRefreshToken(token: String) {
        userPreferenceDataSource.updateRefreshToken(token = token)
    }

    override suspend fun clearTokens() {
        userPreferenceDataSource.clearTokens()
        // 메모리 캐시도 초기화
        tokenMemoryCache.clear()
        // Emit LoggedOut event when tokens are cleared
        _authEvents.tryEmit(AuthEvent.LoggedOut)
    }

    override suspend fun emitAuthEvent(event: AuthEvent) {
        _authEvents.tryEmit(event)
    }

    override suspend fun getFcmDeviceToken(): String? = fcmDeviceToken.firstOrNull()

    override suspend fun updateFcmDeviceToken(token: String) {
        userPreferenceDataSource.updateFcmDeviceToken(token = token)
    }

    override suspend fun postNewFcmToken(): DoRunResult<Unit> =
        try {
            val token = getFcmDeviceToken() ?: throw DoRunException.DataError("기기에 저장된 fcm 토큰이 없습니다.")
            userDataSource.postNewFcmToken(NewFcmTokenRequestDto(token))
            DoRunResult.Success(Unit)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                DoRunResult.Failure(DoRunException.DataError("유효하지 않거나 만료되었거나 누락된 토큰"))
            } else {
                DoRunResult.Failure(DoRunException.NetworkError(e.message.toString()))
            }
        } catch (e: IllegalArgumentException) {
            Log.e("PostFcmToken", "Retrofit 설정 오류일 가능성 (IllegalArgumentException):", e)
            DoRunResult.Failure(DoRunException.UnknownError("Retrofit 설정 오류: ${e.message}"))

            // 네트워크 연결 자체의 문제
        } catch (e: IOException) {
            Log.e("PostFcmToken", "네트워크 오류 (IOException):", e)
            DoRunResult.Failure(DoRunException.NetworkError("네트워크 연결 오류: ${e.message}"))

            // JSON 파싱 오류 (요청 또는 응답)
        } catch (e: kotlinx.serialization.SerializationException) {
            Log.e("PostFcmToken", "JSON 직렬화/역직렬화 오류 (SerializationException):", e)
            DoRunResult.Failure(DoRunException.DataError("데이터 처리 오류: ${e.message}"))

            // 모든 그 외 예외 (가장 마지막에 위치)
        } catch (e: Exception) {
            // Logcat에서 이 로그를 확인하세요!
            Log.e("PostFcmToken", "알 수 없는 오류 발생 (Exception):", e)
            DoRunResult.Failure(DoRunException.UnknownError("알 수 없는 오류 발생: ${e.message}"))
        }
}
