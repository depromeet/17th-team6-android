package com.dpm.sixpack.data.repository

import com.dpm.sixpack.data.cache.TokenMemoryCache
import com.dpm.sixpack.data.source.local.datastore.api.UserPreferenceDataSource
import com.dpm.sixpack.data.source.remote.datasource.UserDataSource
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
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 사용자 관련 Repository 구현체
 * - 프로필 API (조회, 수정)
 * - 토큰 및 세션 관리 (UserPreferenceRepository 통합)
 */
@Singleton
class UserRepositoryImpl
    @Inject
    constructor(
        private val userDataSource: UserDataSource,
        private val userPreferenceDataSource: UserPreferenceDataSource,
        private val tokenMemoryCache: TokenMemoryCache,
    ) : UserRepository {
        private val userId = userPreferenceDataSource.userId
        private val sessionId = userPreferenceDataSource.sessionId
        private val accessToken = userPreferenceDataSource.accessToken
        private val refreshToken = userPreferenceDataSource.refreshToken

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
    }
