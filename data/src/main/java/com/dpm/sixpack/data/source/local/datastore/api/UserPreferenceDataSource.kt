package com.dpm.sixpack.data.source.local.datastore.api

import kotlinx.coroutines.flow.Flow

interface UserPreferenceDataSource {
    val userId: Flow<Long>

    val sessionId: Flow<Long?>

    val isOnboardingComplete: Flow<Boolean>

    suspend fun updateOnboardingComplete(isComplete : Boolean)

    suspend fun updateUserId(userId: Long)

    suspend fun updateSessionId(sessionId: Long)

    suspend fun clearSessionId()
}
