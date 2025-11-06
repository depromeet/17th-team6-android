package com.dpm.sixpack.data.source.remote.datasoruce.api

import com.dpm.sixpack.data.source.remote.dto.response.RefreshTokenResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.SignUpResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.VerifySmsResponseDto
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import retrofit2.Response
import java.io.File

interface AuthDataSource {
    suspend fun sendSmsCode(phoneNumber: String): Response<BaseResponse<Unit>>

    suspend fun verifySmsCode(
        phoneNumber: String,
        verificationCode: String,
    ): Response<BaseResponse<VerifySmsResponseDto>>

    suspend fun signUp(
        nickname: String,
        phoneNumber: String,
        profileImage: File?,
    ): BaseResponse<SignUpResponseDto>

    suspend fun refreshToken(refreshToken: String): BaseResponse<RefreshTokenResponseDto>

    suspend fun logout(): BaseResponse<Unit>

    suspend fun withdraw(): BaseResponse<Unit>
}
