package com.dpm.sixpack.data.source.remote.interceptor

import com.dpm.sixpack.data.cache.TokenMemoryCache
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

class AuthorizationInterceptor @Inject constructor(
    private val tokenMemoryCache: TokenMemoryCache,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val accessToken =
            runBlocking {
                try {
                    tokenMemoryCache.getAccessToken()
                } catch (e: Exception) {
                    Timber.e(e, "AuthorizationInterceptor: Failed to get AccessToken from DataStore.")
                    null
                }
            }

        val requestBuilder = originalRequest.newBuilder()

        if (!accessToken.isNullOrBlank()) {
            val headerValue = "$BEARER_PREFIX$accessToken"
            requestBuilder.addHeader(AUTHORIZATION_HEADER, headerValue)
        } else {
            Timber.w("AuthorizationInterceptor: AccessToken is null, proceeding without Authorization header.")
        }

        val newRequest = requestBuilder.build()
        return chain.proceed(newRequest)
    }

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }
}
