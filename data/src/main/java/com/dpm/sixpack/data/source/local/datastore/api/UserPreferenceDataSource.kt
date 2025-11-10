package com.dpm.sixpack.data.source.local.datastore.api

import kotlinx.coroutines.flow.Flow

interface UserPreferenceDataSource {
    val userId: Flow<Long>
    val sessionId: Flow<Long?>
    val fcmDeviceToken: Flow<String?>
    val accessToken: Flow<String?>
    val refreshToken: Flow<String?>

    suspend fun updateUserId(userId: Long)

    suspend fun updateSessionId(sessionId: Long)

    suspend fun updateFcmDeviceToken(token: String)

    suspend fun clearSessionId()

    suspend fun updateAccessToken(token: String)

    suspend fun updateRefreshToken(token: String)

    suspend fun clearTokens()

    suspend fun clearUserId()
}
