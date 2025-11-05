package com.dpm.sixpack.data.cache

import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 토큰을 메모리에 캐싱하여 매번 DataStore를 읽지 않도록 최적화
 * - 간단한 메모리 캐시 역할만 수행
 * - UserPreferenceRepository에서 DataStore 업데이트 시 함께 업데이트
 * - AuthInterceptor/TokenAuthenticator에서 빠르게 접근
 */
@Singleton
class TokenMemoryCache
    @Inject
    constructor() {
        @Volatile
        private var cachedUserId: Long? = null

        @Volatile
        private var cachedAccessToken: String? = null

        fun getUserId(): Long? = cachedUserId

        fun getAccessToken(): String? = cachedAccessToken

        fun setTokens(
            userId: Long?,
            accessToken: String?,
        ) {
            cachedUserId = userId
            cachedAccessToken = accessToken
            Timber.d("TokenMemoryCache: Tokens cached (userId=$userId, hasToken=${accessToken != null})")
        }

        fun clear() {
            cachedUserId = null
            cachedAccessToken = null
            Timber.d("TokenMemoryCache: Cache cleared")
        }
    }
