package com.dpm.sixpack.domain.repository

interface UserPreferenceRepository {
    suspend fun getUserId(): Long

    suspend fun getSessionId(): Long?

    suspend fun updateUserId(userId: Long)

    suspend fun updateSessionId(sessionId: Long)

    suspend fun clearSessionId()

    suspend fun getFcmDeviceToken(): String?

    suspend fun getAccessToken(): String?

    suspend fun getRefreshToken(): String?

    suspend fun updateFcmDeviceToken(token: String)

    suspend fun updateAccessToken(token: String)

    suspend fun updateRefreshToken(token: String)

    suspend fun clearTokens()
}
