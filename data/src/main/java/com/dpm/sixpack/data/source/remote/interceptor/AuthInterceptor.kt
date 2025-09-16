package com.dpm.sixpack.data.source.remote.interceptor

import com.dpm.sixpack.domain.repository.UserPreferenceRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

class AuthInterceptor
    @Inject
    constructor(
        private val userPreferenceRepository: UserPreferenceRepository,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()

            val userId =
                runBlocking {
                    try {
                        userPreferenceRepository.getUserId().toString()
                    } catch (e: Exception) {
                        Timber.e("AuthInterceptor: Failed to get UserId from DataStore. message: ${e.message}")
                        null
                    }
                }

            val requestBuilder = originalRequest.newBuilder()

            if (userId != null) {
                requestBuilder.addHeader(X_USER_ID, userId)
            } else {
                Timber.e("AuthInterceptor: UserId is null, proceeding without X-User-Id header.")
            }

            val newRequest = requestBuilder.build()
            return chain.proceed(newRequest)
        }

        companion object {
            private const val X_USER_ID = "X-User-Id"
        }
    }
