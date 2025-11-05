package com.dpm.sixpack.data.source.remote.interceptor

import com.dpm.sixpack.data.source.remote.util.constant.ApiConstants.TOKEN_REFRESH_PATH
import com.dpm.sixpack.domain.model.AuthEvent
import com.dpm.sixpack.domain.repository.AuthRepository
import com.dpm.sixpack.domain.repository.UserPreferenceRepository
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import javax.inject.Inject

/**
 * 401 Unauthorized 응답 시 자동으로 토큰을 갱신하는 Authenticator
 *
 * 동작 흐름:
 * 1. API 호출이 401 응답 반환
 * 2. OkHttp가 이 Authenticator 호출
 * 3. synchronized 블록으로 동시 요청 제어
 *    - 첫 번째 요청: 실제 토큰 갱신 API 호출
 *    - 나머지 요청: 대기 후 갱신된 토큰으로 재시도 또는 취소
 * 4. UserPreferenceRepository에서 RefreshToken 가져오기
 * 5. AuthRepository로 토큰 갱신 API 호출
 * 6. 성공 시: 토큰 저장 후 새 AccessToken으로 요청 재시도
 * 7. 실패 시: 토큰 삭제 및 AuthEvent 발행, 로그인 화면 리다이렉트
 */
class TokenAuthenticator
    @Inject
    constructor(
        private val userPreferenceRepository: UserPreferenceRepository,
        private val authRepository: AuthRepository,
    ) : Authenticator {
        // 동시 토큰 갱신 요청 방지를 위한 lock
        private val lock = Any()

        override fun authenticate(
            route: Route?,
            response: Response,
        ): Request? {
            // 토큰 갱신 API 자체가 401이면 재시도하지 않음
            if (response.request.url.encodedPath
                    .contains(TOKEN_REFRESH_PATH)
            ) {
                Timber.e("TokenAuthenticator: Token refresh API failed with 401, giving up.")
                return null
            }

            // 이미 재시도한 요청은 포기 (무한 루프 방지)
            if (response.request.header(RETRY_HEADER) != null) {
                Timber.e("TokenAuthenticator: Already retried, giving up.")
                return null
            }

            // 401이 아니면 처리하지 않음
            if (response.code != 401) {
                Timber.d("TokenAuthenticator: Response code is not 401, skipping token refresh.")
                return null
            }

            // synchronized 블록으로 동시 토큰 갱신 방지
            synchronized(lock) {
                return runBlocking {
                    try {
                        // 현재 저장된 AccessToken 확인
                        val currentAccessToken = userPreferenceRepository.getAccessToken()

                        // 이 요청이 사용한 토큰 추출
                        val requestToken =
                            response.request
                                .header("Authorization")
                                ?.removePrefix("Bearer ")
                                ?.trim()

                        // 토큰이 이미 갱신된 경우 (다른 스레드가 갱신 완료)
                        if (requestToken != currentAccessToken && !currentAccessToken.isNullOrBlank()) {
                            Timber.d("TokenAuthenticator: Token already refreshed by another request, using new token")
                            // 새 토큰으로 재시도
                            return@runBlocking response.request
                                .newBuilder()
                                .header("Authorization", "Bearer $currentAccessToken")
                                .header(RETRY_HEADER, "true")
                                .build()
                        }

                        // 첫 번째 요청이므로 실제로 토큰 갱신 수행
                        val currentRefreshToken = userPreferenceRepository.getRefreshToken()

                        if (currentRefreshToken.isNullOrBlank()) {
                            Timber.e("TokenAuthenticator: RefreshToken is null or blank, cannot refresh")
                            // 인증 세션 종료 (토큰 삭제 + AuthEvent 발행)
                            userPreferenceRepository.clearTokens()
                            userPreferenceRepository.emitAuthEvent(AuthEvent.TokenExpired)
                            return@runBlocking null
                        }

                        // AuthRepository로 토큰 갱신 API 호출
                        Timber.d("TokenAuthenticator: Attempting token refresh")
                        val result = authRepository.refreshToken(currentRefreshToken)

                        // when 표현식으로 Result 처리 (onSuccess/onError는 제대로 return되지 않음)
                        when (result) {
                            is DoRunResult.Success -> {
                                val authToken = result.data
                                Timber.d("TokenAuthenticator: Token refresh successful")
                                // 토큰 저장
                                userPreferenceRepository.updateAccessToken(authToken.accessToken)
                                userPreferenceRepository.updateRefreshToken(authToken.refreshToken)
                                // 새 AccessToken으로 원래 요청 재시도
                                response.request
                                    .newBuilder()
                                    .header("Authorization", "Bearer ${authToken.accessToken}")
                                    .header(RETRY_HEADER, "true")
                                    .build()
                            }

                            is DoRunResult.Failure -> {
                                val exception = result.exception
                                Timber.e("TokenAuthenticator: Token refresh failed: ${exception.message}")
                                // 인증 세션 종료 (토큰 삭제 + AuthEvent 발행)
                                // 모든 대기 중인 요청도 null을 반환받아 취소됨
                                userPreferenceRepository.clearTokens()
                                userPreferenceRepository.emitAuthEvent(
                                    AuthEvent.TokenRefreshFailed(exception.message ?: "Unknown error"),
                                )
                                null
                            }
                        }
                    } catch (e: Exception) {
                        Timber.e("TokenAuthenticator: Exception during token refresh: ${e.message}")
                        // 인증 세션 종료 (토큰 삭제 + AuthEvent 발행)
                        userPreferenceRepository.clearTokens()
                        userPreferenceRepository.emitAuthEvent(
                            AuthEvent.TokenRefreshFailed(e.message ?: "Unknown exception"),
                        )
                        null
                    }
                }
            }
        }

        companion object {
            private const val RETRY_HEADER = "X-Token-Retry"
        }
    }
