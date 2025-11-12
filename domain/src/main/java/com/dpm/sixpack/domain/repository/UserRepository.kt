package com.dpm.sixpack.domain.repository

import com.dpm.sixpack.domain.model.AuthEvent
import com.dpm.sixpack.domain.model.ProfileImageOption
import com.dpm.sixpack.domain.model.ProfileUpdateResponse
import com.dpm.sixpack.domain.model.UserProfile
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.firstOrNull
import java.io.File

/**
 * 사용자 관련 Repository Interface
 * - 프로필 API (조회, 수정)
 * - 토큰 및 세션 관리 (UserPreferenceRepository 통합)
 */
interface UserRepository {
    // ========== Profile API ==========

    /**
     * 내 프로필 상세 조회
     *
     * @return 사용자 프로필 정보
     */
    suspend fun getMyProfile(): DoRunResult<UserProfile>

    /**
     * 내 프로필 수정
     *
     * @param nickname 닉네임 (2~8자)
     * @param imageOption 프로필 이미지 처리 옵션
     * @param profileImage 프로필 이미지 파일 (imageOption=SET인 경우 필수)
     * @return 수정된 프로필 정보
     */
    suspend fun updateMyProfile(
        nickname: String,
        imageOption: ProfileImageOption,
        profileImage: File?,
    ): DoRunResult<ProfileUpdateResponse>

    // ========== User Preferences (Token & Session) ==========

    /**
     * 토큰 갱신 실패시 로그아웃 시키고 로그인 화면으로 리다이렉트 시키려고 만듦
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

    suspend fun getFcmDeviceToken(): String?

    suspend fun updateFcmDeviceToken(token: String)

    suspend fun postNewFcmToken(): DoRunResult<Unit>
}
