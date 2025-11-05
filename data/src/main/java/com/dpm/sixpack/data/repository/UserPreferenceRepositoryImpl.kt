package com.dpm.sixpack.data.repository

import com.dpm.sixpack.data.cache.TokenMemoryCache
import com.dpm.sixpack.data.source.local.datastore.api.UserPreferenceDataSource
import com.dpm.sixpack.domain.model.AuthEvent
import com.dpm.sixpack.domain.repository.UserPreferenceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferenceRepositoryImpl @Inject constructor(
    private val userPreferenceDataSource: UserPreferenceDataSource,
    private val tokenMemoryCache: TokenMemoryCache,
) : UserPreferenceRepository {
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
                Timber.d("UserPreferenceRepository: Initial tokens loaded to memory cache")
            } catch (e: Exception) {
                Timber.e("UserPreferenceRepository: Failed to load initial tokens: ${e.message}")
            }
        }
    }

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
