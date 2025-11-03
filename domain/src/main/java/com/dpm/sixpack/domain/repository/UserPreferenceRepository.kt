package com.dpm.sixpack.domain.repository

interface UserPreferenceRepository {
    suspend fun getUserId(): Long

    suspend fun getSessionId(): Long?

    suspend fun getFcmDeviceToken(): String?

    suspend fun getIsOnboardingComplete(): Boolean

    suspend fun updateUserId(userId: Long)

    suspend fun updateSessionId(sessionId: Long)

    suspend fun updateFcmDeviceToken(token: String)

    suspend fun updateOnboardingComplete(isComplete: Boolean)

    suspend fun clearSessionId()
}
