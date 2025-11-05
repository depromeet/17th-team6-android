package com.dpm.sixpack.data.repository

import com.dpm.sixpack.data.source.local.datastore.api.UserPreferenceDataSource
import com.dpm.sixpack.domain.model.AuthEvent
import com.dpm.sixpack.domain.repository.UserPreferenceRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferenceRepositoryImpl @Inject constructor(
    private val userPreferenceDataSource: UserPreferenceDataSource,
) : UserPreferenceRepository {
    private val userId = userPreferenceDataSource.userId
    private val sessionId = userPreferenceDataSource.sessionId
    private val accessToken = userPreferenceDataSource.accessToken
    private val refreshToken = userPreferenceDataSource.refreshToken

    private val _authEvents = MutableSharedFlow<AuthEvent>(replay = 0, extraBufferCapacity = 1)
    override val authEvents: SharedFlow<AuthEvent> = _authEvents.asSharedFlow()

    override suspend fun getUserId(): Long = userId.first()

    override suspend fun getSessionId(): Long? = sessionId.firstOrNull()

    override suspend fun getAccessToken(): String? = accessToken.firstOrNull()

    override suspend fun getRefreshToken(): String? = refreshToken.firstOrNull()

    override suspend fun updateUserId(userId: Long) {
        userPreferenceDataSource.updateUserId(userId = userId)
    }

    override suspend fun updateSessionId(sessionId: Long) {
        userPreferenceDataSource.updateSessionId(sessionId = sessionId)
    }

    override suspend fun clearSessionId() {
        userPreferenceDataSource.clearSessionId()
    }

    override suspend fun updateAccessToken(token: String) {
        userPreferenceDataSource.updateAccessToken(token = token)
    }

    override suspend fun updateRefreshToken(token: String) {
        userPreferenceDataSource.updateRefreshToken(token = token)
    }

    override suspend fun clearTokens() {
        userPreferenceDataSource.clearTokens()
        // Emit LoggedOut event when tokens are cleared
        _authEvents.tryEmit(AuthEvent.LoggedOut)
    }

    override suspend fun emitAuthEvent(event: AuthEvent) {
        _authEvents.tryEmit(event)
    }
}
