package com.dpm.sixpack.data.source.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import javax.inject.Inject

class HttpResponseInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val responseBody = response.body
        val responseBodyString = responseBody?.string()

        val newBodyString = try {
            val jsonObject = responseBodyString?.let { JSONObject(it) } ?: JSONObject()
            if (!jsonObject.has("code")) {
                jsonObject.put("code", response.code)
            }
            jsonObject.toString()
        } catch (e: Exception) {
            responseBodyString
        }

        val newBody = newBodyString?.toResponseBody(responseBody?.contentType())

        return response.newBuilder()
            .body(newBody)
            .build()
    }
}
