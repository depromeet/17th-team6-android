package com.dpm.sixpack.data.repository

import com.dpm.sixpack.data.source.local.datastore.api.UserPreferenceDataSource
import com.dpm.sixpack.domain.repository.UserPreferenceRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserPreferenceRepositoryImpl @Inject constructor(
    private val userPreferenceDataSource: UserPreferenceDataSource
) : UserPreferenceRepository {
    private val userId = userPreferenceDataSource.userId
    private val sessionId = userPreferenceDataSource.sessionId
    override suspend fun getUserId(): Long = userId.first()


    override suspend fun getSessionId(): Long = sessionId.first()


    override suspend fun updateUserId(userId: Long) {
        userPreferenceDataSource.updateUserId(userId = userId)
    }

    override suspend fun updateSessionId(sessionId: Long) {
        userPreferenceDataSource.updateSessionId(sessionId = sessionId)

    }
}
