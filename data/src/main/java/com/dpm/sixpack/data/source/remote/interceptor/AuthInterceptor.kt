package com.dpm.sixpack.data.source.remote.interceptor

import com.dpm.sixpack.domain.repository.UserPreferenceRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

/**
 * 모든 API 요청에 인증 헤더를 자동으로 추가하는 Interceptor
 */
class AuthInterceptor
    @Inject
    constructor(
        private val userPreferenceRepository: UserPreferenceRepository,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()

            // S3 presigned URL에는 헤더를 추가하지 않음 (서명 검증 실패 방지)
            if (isS3Request(originalRequest.url.host)) {
                return chain.proceed(originalRequest)
            }

            val requestBuilder = originalRequest.newBuilder()

            runBlocking {
                try {
                    // X-User-Id 헤더 추가
                    val userId = userPreferenceRepository.getUserId()
                    requestBuilder.addHeader(X_USER_ID, userId.toString())
                } catch (e: Exception) {
                    Timber.e("AuthInterceptor: Failed to get UserId: ${e.message}")
                }

                try {
                    // Authorization Bearer 헤더 추가
                    val accessToken = userPreferenceRepository.getAccessToken()
                    if (!accessToken.isNullOrBlank()) {
                        requestBuilder.addHeader(AUTHORIZATION, "$BEARER $accessToken")
                    } else {
                        Timber.d("AuthInterceptor: AccessToken is null, proceeding without Authorization header")
                    }
                } catch (e: Exception) {
                    Timber.e("AuthInterceptor: Failed to get AccessToken: ${e.message}")
                }
            }

            val newRequest = requestBuilder.build()
            return chain.proceed(newRequest)
        }

        private fun isS3Request(host: String): Boolean = host.contains("amazonaws.com")

        companion object {
            private const val X_USER_ID = "X-User-Id"
            private const val AUTHORIZATION = "Authorization"
            private const val BEARER = "Bearer"
        }
    }
