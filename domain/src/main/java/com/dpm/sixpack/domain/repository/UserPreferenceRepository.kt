package com.dpm.sixpack.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferenceRepository {

    fun getUserIdFlow(): Flow<Long>

    suspend fun getUserId(): Long

    suspend fun getSessionId(): Long?

    suspend fun getAccessToken(): String?

    suspend fun getRefreshToken(): String?

    suspend fun updateUserId(userId: Long)

    suspend fun updateSessionId(sessionId: Long)

    suspend fun clearSessionId()

    suspend fun updateAccessToken(token: String)

    suspend fun updateRefreshToken(token: String)

    suspend fun clearTokens()
}
