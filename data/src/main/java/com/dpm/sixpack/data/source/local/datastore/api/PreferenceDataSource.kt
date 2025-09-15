package com.dpm.sixpack.data.source.local.datastore.api

import kotlinx.coroutines.flow.Flow

interface PreferenceDataSource {
    val userId: Flow<Long>

    val sessionId: Flow<Long>

    suspend fun updateUserId(userId: Long)

    suspend fun updateSessionId(sessionId: Long)
}
