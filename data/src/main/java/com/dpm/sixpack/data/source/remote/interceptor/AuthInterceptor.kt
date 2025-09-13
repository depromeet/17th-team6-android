package com.dpm.sixpack.data.source.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

class UserIdInterceptor @Inject constructor(
//        TODO UserPrefrence 구현
//    private val userPreferenceRepository: UserPreferenceRepository,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val mockUserId = "1"
//        TODO UserPrefrence 구현
//        val userId = runBlocking {
//            userPreferenceRepository.userPreferencesFlow
//                .map { it.userId }
//                .firstOrNull()
//        }

        Timber.d("UserIdInterceptor: Retrieved UserId -> $mockUserId")

        val requestBuilder = originalRequest.newBuilder()

        requestBuilder.addHeader(X_USER_ID, mockUserId)

        //        TODO UserPrefrence 구현
//        if (!userId.isNullOrBlank()) {
//            requestBuilder.addHeader(X_USER_ID, userId)
//        }

        val newRequest = requestBuilder.build()
        return chain.proceed(newRequest)
    }

    companion object {
        private const val X_USER_ID = "X-User-Id"
    }
}
