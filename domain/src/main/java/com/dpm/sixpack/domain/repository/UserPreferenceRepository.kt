package com.dpm.sixpack.domain.repository

import com.dpm.sixpack.domain.model.AuthEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface UserPreferenceRepository {
    /**
     * 토큰 갱신 실패시 로그아웃 시키고  로그인 화면으로 리다이렉트 시키려고 만듦
     */
    val authEvents: SharedFlow<AuthEvent>

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

    /**
     * 토큰 갱신 실패시 로그아웃 시키고 로그인 화면으로 리다이렉트 시키려고 만듦
     */
    suspend fun emitAuthEvent(event: AuthEvent)
}
