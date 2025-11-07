package com.dpm.sixpack.data.source.remote.interceptor

import com.dpm.sixpack.data.cache.TokenMemoryCache
import com.dpm.sixpack.data.source.remote.util.constant.ApiConstants.TOKEN_REFRESH_PATH
import com.dpm.sixpack.domain.model.AuthEvent
import com.dpm.sixpack.domain.repository.AuthRepository
import com.dpm.sixpack.domain.repository.UserRepository
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
 * 4. UserRepository에서 RefreshToken 가져오기
 * 5. AuthRepository로 토큰 갱신 API 호출
 * 6. 성공 시: DataStore와 메모리 캐시 모두 업데이트, 새 AccessToken으로 요청 재시도
 * 7. 실패 시: 토큰 삭제 및 AuthEvent 발행, 로그인 화면 리다이렉트
 */
class TokenAuthenticator
    @Inject
    constructor(
        private val tokenMemoryCache: TokenMemoryCache,
        private val userPreferenceRepository: UserRepository,
        private val authRepository: AuthRepository,
    ) : Authenticator {
        // 동시 토큰 갱신 요청 방지를 위한 lock
        private val lock = Any()

        override fun authenticate(
            route: Route?,
            response: Response,
        ): Request? {
            val requestUrl = response.request.url.encodedPath
            val statusCode = response.code

            Timber.d("╔═══════════════════════════════════════════════════════════╗")
            Timber.d("║ TokenAuthenticator: authenticate 호출됨                    ║")
            Timber.d("╚═══════════════════════════════════════════════════════════╝")
            Timber.d("TokenAuthenticator: URL = $requestUrl")
            Timber.d("TokenAuthenticator: Status Code = $statusCode")

            // 401이 아니면 처리하지 않음
            if (statusCode != 401) {
                Timber.d("TokenAuthenticator: ℹ️ 401이 아닙니다. 토큰 갱신 안함 (code=$statusCode)")
                return null
            }

            Timber.w("TokenAuthenticator: ⚠️ 401 Unauthorized 감지! 토큰 갱신 시작")

            // 토큰 갱신 API 자체가 401이면 재시도하지 않음
            if (requestUrl.contains(TOKEN_REFRESH_PATH)) {
                Timber.e("TokenAuthenticator: ❌ 토큰 갱신 API 자체가 401 실패. 더 이상 재시도 안함")
                return null
            }

            // 이미 재시도한 요청은 포기 (무한 루프 방지)
            if (response.request.header(RETRY_HEADER) != null) {
                Timber.e("TokenAuthenticator: ❌ 이미 재시도한 요청입니다. 무한 루프 방지를 위해 포기")
                return null
            }

            Timber.d("TokenAuthenticator: 🔒 synchronized 블록 진입 (동시 토큰 갱신 방지)")

            // synchronized 블록으로 동시 토큰 갱신 방지
            synchronized(lock) {
                return runBlocking {
                    try {
                        Timber.d("TokenAuthenticator: 1️⃣ 메모리 캐시에서 현재 AccessToken 조회")
                        // 현재 메모리 캐시에서 AccessToken 확인 (빠름)
                        val currentAccessToken = tokenMemoryCache.getAccessToken()

                        // 이 요청이 사용한 토큰 추출
                        val requestToken =
                            response.request
                                .header("Authorization")
                                ?.removePrefix("Bearer ")
                                ?.trim()

                        val maskedCurrentToken =
                            currentAccessToken?.take(10) + "..." + currentAccessToken?.takeLast(10)
                        val maskedRequestToken = requestToken?.take(10) + "..." + requestToken?.takeLast(10)
                        Timber.d("TokenAuthenticator: 현재 캐시 토큰 = $maskedCurrentToken")
                        Timber.d("TokenAuthenticator: 요청에 사용된 토큰 = $maskedRequestToken")

                        // 토큰이 이미 갱신된 경우 (다른 스레드가 갱신 완료)
                        if (requestToken != currentAccessToken && !currentAccessToken.isNullOrBlank()) {
                            Timber.d("TokenAuthenticator: ✅ 토큰이 이미 갱신되었습니다! (다른 스레드가 갱신 완료)")
                            Timber.d("TokenAuthenticator: 새 토큰으로 요청 재시도")
                            // 새 토큰으로 재시도
                            return@runBlocking response.request
                                .newBuilder()
                                .header("Authorization", "Bearer $currentAccessToken")
                                .header(RETRY_HEADER, "true")
                                .build()
                        }

                        Timber.d("TokenAuthenticator: 2️⃣ 이 스레드가 토큰 갱신 수행")
                        Timber.d("TokenAuthenticator: RefreshToken 조회 중...")

                        // 첫 번째 요청이므로 실제로 토큰 갱신 수행
                        val currentRefreshToken = userPreferenceRepository.getRefreshToken()

                        // 토큰이 이미 클리어된 경우 (다른 스레드가 실패 후 토큰 삭제 완료)
                        if (currentAccessToken.isNullOrBlank() && currentRefreshToken.isNullOrBlank()) {
                            Timber.w("TokenAuthenticator: ⚠️ 토큰이 이미 클리어되었습니다! (다른 스레드가 실패 처리 완료)")
                            Timber.w("TokenAuthenticator: 추가 AuthEvent 발행 없이 요청 취소")
                            return@runBlocking null
                        }

                        if (currentRefreshToken.isNullOrBlank()) {
                            Timber.e("TokenAuthenticator: ❌ RefreshToken이 null 또는 blank입니다!")
                            Timber.e("TokenAuthenticator: 토큰 갱신 불가능 → 인증 세션 종료")
                            // 인증 세션 종료 (DataStore와 메모리 캐시 모두 삭제)
                            userPreferenceRepository.clearTokens() // 내부에서 tokenMemoryCache.clear() 호출
                            userPreferenceRepository.emitAuthEvent(AuthEvent.TokenExpired)
                            Timber.e("TokenAuthenticator: 🚪 AuthEvent.TokenExpired 발행 → 로그인 화면으로 이동")
                            return@runBlocking null
                        }

                        val maskedRefreshToken =
                            currentRefreshToken.take(10) + "..." + currentRefreshToken.takeLast(10)
                        Timber.d("TokenAuthenticator: RefreshToken = $maskedRefreshToken")

                        // AuthRepository로 토큰 갱신 API 호출
                        Timber.d("TokenAuthenticator: 3️⃣ 토큰 갱신 API 호출 시작")
                        val result = authRepository.refreshToken(currentRefreshToken)

                        when (result) {
                            is DoRunResult.Success -> {
                                val authToken = result.data
                                val newMaskedToken =
                                    authToken.accessToken.take(10) + "..." + authToken.accessToken.takeLast(10)
                                Timber.d("TokenAuthenticator: ✅ 토큰 갱신 성공!")
                                Timber.d("TokenAuthenticator: 새 AccessToken = $newMaskedToken")
                                Timber.d("TokenAuthenticator: 4️⃣ DataStore와 메모리 캐시 업데이트 중...")
                                // DataStore에 토큰 저장 (내부에서 메모리 캐시도 자동 업데이트)
                                userPreferenceRepository.updateAccessToken(authToken.accessToken)
                                userPreferenceRepository.updateRefreshToken(authToken.refreshToken)
                                Timber.d("TokenAuthenticator: 5️⃣ 새 토큰으로 원래 요청 재시도")
                                Timber.d("╔═══════════════════════════════════════════════════════════╗")
                                Timber.d("║ TokenAuthenticator: 토큰 갱신 완료! 요청 재시도           ║")
                                Timber.d("╚═══════════════════════════════════════════════════════════╝")
                                // 새 AccessToken으로 원래 요청 재시도
                                response.request
                                    .newBuilder()
                                    .header("Authorization", "Bearer ${authToken.accessToken}")
                                    .header(RETRY_HEADER, "true")
                                    .build()
                            }

                            is DoRunResult.Failure -> {
                                val exception = result.exception
                                Timber.e("TokenAuthenticator: ❌ 토큰 갱신 실패!")
                                Timber.e("TokenAuthenticator: 에러 메시지 = ${exception.message}")
                                Timber.e("TokenAuthenticator: 인증 세션 종료 → DataStore와 메모리 캐시 초기화")
                                // 인증 세션 종료 (DataStore와 메모리 캐시 모두 삭제)
                                userPreferenceRepository.clearTokens() // 내부에서 tokenMemoryCache.clear() 호출
                                userPreferenceRepository.emitAuthEvent(
                                    AuthEvent.TokenRefreshFailed(exception.message ?: "Unknown error"),
                                )
                                Timber.e("TokenAuthenticator: 🚪 AuthEvent.TokenRefreshFailed 발행 → 로그인 화면으로 이동")
                                null
                            }
                        }
                    } catch (e: Exception) {
                        Timber.e("TokenAuthenticator: ❌ 예외 발생!")
                        Timber.e("TokenAuthenticator: 예외 타입 = ${e.javaClass.simpleName}")
                        Timber.e("TokenAuthenticator: 예외 메시지 = ${e.message}")
                        Timber.e(e, "TokenAuthenticator: 스택 트레이스:")
                        Timber.e("TokenAuthenticator: 인증 세션 종료")
                        // 인증 세션 종료 (DataStore와 메모리 캐시 모두 삭제)
                        userPreferenceRepository.clearTokens() // 내부에서 tokenMemoryCache.clear() 호출
                        userPreferenceRepository.emitAuthEvent(
                            AuthEvent.TokenRefreshFailed(e.message ?: "Unknown exception"),
                        )
                        Timber.e("TokenAuthenticator: 🚪 AuthEvent.TokenRefreshFailed 발행 → 로그인 화면으로 이동")
                        null
                    }
                }
            }
        }

        companion object {
            private const val RETRY_HEADER = "X-Token-Retry"
        }
    }
