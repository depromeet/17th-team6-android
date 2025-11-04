package com.dpm.sixpack.data.repository

import com.dpm.sixpack.data.source.local.datastore.api.UserPreferenceDataSource
import com.dpm.sixpack.domain.repository.UserPreferenceRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class UserPreferenceRepositoryImpl @Inject constructor(
    private val userPreferenceDataSource: UserPreferenceDataSource,
) : UserPreferenceRepository {
    private val userId = userPreferenceDataSource.userId
    private val sessionId = userPreferenceDataSource.sessionId
    private val accessToken = userPreferenceDataSource.accessToken
    private val refreshToken = userPreferenceDataSource.refreshToken

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
    }
}
