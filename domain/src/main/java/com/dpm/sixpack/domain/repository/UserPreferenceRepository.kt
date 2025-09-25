package com.dpm.sixpack.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferenceRepository {
    suspend fun getUserId(): Long

    suspend fun getSessionId(): Long?

    suspend fun getIsOnboardingComplete(): Flow<Boolean>

    suspend fun updateUserId(userId: Long)

    suspend fun updateSessionId(sessionId: Long)

    suspend fun updateOnboardingComplete(isComplete: Boolean)

    suspend fun clearSessionId()
}
