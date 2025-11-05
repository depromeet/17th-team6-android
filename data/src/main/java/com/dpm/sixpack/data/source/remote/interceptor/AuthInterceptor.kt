package com.dpm.sixpack.data.source.remote.interceptor

import com.dpm.sixpack.data.cache.TokenMemoryCache
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

/**
 * 모든 API 요청에 인증 헤더를 자동으로 추가하는 Interceptor
 * TokenMemoryCache를 통해 메모리에 캐싱된 토큰을 사용하여 성능 최적화 (runBlocking 제거)
 */
class AuthInterceptor
    @Inject
    constructor(
        private val tokenMemoryCache: TokenMemoryCache,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val requestUrl = originalRequest.url.encodedPath

            Timber.d("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
            Timber.d("AuthInterceptor: 요청 시작")
            Timber.d("AuthInterceptor: URL = $requestUrl")
            Timber.d("AuthInterceptor: Method = ${originalRequest.method}")

            val requestBuilder = originalRequest.newBuilder()

            // X-User-Id 헤더 추가 (메모리 캐시에서 즉시 가져옴, runBlocking 불필요)
            val userId = tokenMemoryCache.getUserId()
            if (userId != null) {
                requestBuilder.addHeader(X_USER_ID, userId.toString())
                Timber.d("AuthInterceptor: ✅ X-User-Id 헤더 추가 = $userId")
            } else {
                Timber.w("AuthInterceptor: ⚠️ UserId가 null입니다. X-User-Id 헤더 추가 안함")
            }

            // Authorization Bearer 헤더 추가 (메모리 캐시에서 즉시 가져옴, runBlocking 불필요)
            val accessToken = tokenMemoryCache.getAccessToken()
            if (!accessToken.isNullOrBlank()) {
                requestBuilder.addHeader(AUTHORIZATION, "$BEARER $accessToken")
                val maskedToken = accessToken.take(10) + "..." + accessToken.takeLast(10)
                Timber.d("AuthInterceptor: ✅ Authorization 헤더 추가 = Bearer $maskedToken")
            } else {
                Timber.w("AuthInterceptor: ⚠️ AccessToken이 null 또는 blank입니다. Authorization 헤더 추가 안함")
            }

            val newRequest = requestBuilder.build()
            Timber.d("AuthInterceptor: 요청 진행 중...")

            val response = chain.proceed(newRequest)

            Timber.d("AuthInterceptor: 응답 받음 - Status Code = ${response.code}")
            Timber.d("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

            return response
        }

        companion object {
            private const val X_USER_ID = "X-User-Id"
            private const val AUTHORIZATION = "Authorization"
            private const val BEARER = "Bearer"
        }
    }
