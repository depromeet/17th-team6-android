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
    private val fcmDeviceToken = userPreferenceDataSource.fcmDeviceToken

    private val isOnboardingComplete = userPreferenceDataSource.isOnboardingComplete

    override suspend fun getUserId(): Long = userId.first()

    override suspend fun getSessionId(): Long? = sessionId.firstOrNull()

    override suspend fun getFcmDeviceToken(): String? = fcmDeviceToken.firstOrNull()

    override suspend fun getIsOnboardingComplete(): Boolean = isOnboardingComplete.first()

    override suspend fun updateUserId(userId: Long) {
        userPreferenceDataSource.updateUserId(userId = userId)
    }

    override suspend fun updateSessionId(sessionId: Long) {
        userPreferenceDataSource.updateSessionId(sessionId = sessionId)
    }

    override suspend fun updateFcmDeviceToken(token: String) {
        userPreferenceDataSource.updateFcmDeviceToken(token = token)
    }

    override suspend fun updateOnboardingComplete(isComplete: Boolean) {
        userPreferenceDataSource.updateOnboardingComplete(isComplete = isComplete)
    }

    override suspend fun clearSessionId() {
        userPreferenceDataSource.clearSessionId()
    }
}
