package com.dpm.sixpack.domain.repository

interface UserPreferenceRepository {
    suspend fun getUserId(): Long

    suspend fun getSessionId(): Long?

    suspend fun updateUserId(userId: Long)

    suspend fun updateSessionId(sessionId: Long)

    suspend fun clearSessionId()
}
