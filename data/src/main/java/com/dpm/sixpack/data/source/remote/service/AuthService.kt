package com.dpm.sixpack.data.source.remote.service

import com.dpm.sixpack.data.source.remote.dto.request.RefreshTokenRequestDto
import com.dpm.sixpack.data.source.remote.dto.request.SendSmsRequestDto
import com.dpm.sixpack.data.source.remote.dto.request.VerifySmsRequestDto
import com.dpm.sixpack.data.source.remote.dto.response.RefreshTokenResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.SignUpResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.VerifySmsResponseDto
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import com.dpm.sixpack.data.source.remote.util.constant.ApiConstants.TOKEN_REFRESH_PATH
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AuthService {
    @POST("/api/auth/sms/send")
    suspend fun sendSmsCode(
        @Body request: SendSmsRequestDto,
    ): Response<BaseResponse<Unit>>

    @POST("/api/auth/sms/verify")
    suspend fun verifySmsCode(
        @Body request: VerifySmsRequestDto,
    ): Response<BaseResponse<VerifySmsResponseDto>>

    @Multipart
    @POST("/api/auth/signup")
    suspend fun signUp(
        @Part("data") data: RequestBody,
        @Part profileImage: MultipartBody.Part?,
    ): BaseResponse<SignUpResponseDto>

    @POST(TOKEN_REFRESH_PATH)
    suspend fun refreshToken(
        @Body request: RefreshTokenRequestDto,
    ): BaseResponse<RefreshTokenResponseDto>

    @POST("/api/auth/logout")
    suspend fun logout(): BaseResponse<Unit>

    @DELETE("/api/auth/withdraw")
    suspend fun withdraw(): BaseResponse<Unit>
}
