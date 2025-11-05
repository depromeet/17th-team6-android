package com.dpm.sixpack.data.source.remote.datasoruce

import com.dpm.sixpack.data.source.remote.datasoruce.api.AuthDataSource
import com.dpm.sixpack.data.source.remote.di.AuthRetrofit
import com.dpm.sixpack.data.source.remote.di.RefreshRetrofit
import com.dpm.sixpack.data.source.remote.dto.request.RefreshTokenRequestDto
import com.dpm.sixpack.data.source.remote.dto.request.SendSmsRequestDto
import com.dpm.sixpack.data.source.remote.dto.request.SignUpRequestDto
import com.dpm.sixpack.data.source.remote.dto.request.VerifySmsRequestDto
import com.dpm.sixpack.data.source.remote.dto.response.RefreshTokenResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.SignUpResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.VerifySmsResponseDto
import com.dpm.sixpack.data.source.remote.service.AuthService
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

/**
 * AuthDataSource 구현체
 * - 일반 API는 authService 사용 (AuthInterceptor + TokenAuthenticator 포함)
 * - 토큰 갱신 API는 refreshAuthService 사용 (데드락 방지)
 */
class AuthDataSourceImpl @Inject constructor(
    @AuthRetrofit private val authService: AuthService,
    @RefreshRetrofit private val refreshAuthService: AuthService,
    private val json: Json,
) : AuthDataSource {
    override suspend fun sendSmsCode(phoneNumber: String): Response<BaseResponse<Unit>> {
        val requestDto =
            SendSmsRequestDto(
                phoneNumber = phoneNumber,
            )
        return authService.sendSmsCode(request = requestDto)
    }

    override suspend fun verifySmsCode(
        phoneNumber: String,
        verificationCode: String,
    ): Response<BaseResponse<VerifySmsResponseDto>> {
        val requestDto =
            VerifySmsRequestDto(
                phoneNumber = phoneNumber,
                verificationCode = verificationCode,
            )
        return authService.verifySmsCode(request = requestDto)
    }

    override suspend fun signUp(
        nickname: String,
        phoneNumber: String,
        profileImage: File?,
    ): BaseResponse<SignUpResponseDto> {
        // Construct SignUpRequestDto internally
        val signUpData =
            SignUpRequestDto(
                nickname = nickname,
                phoneNumber = phoneNumber,
            )

        // Convert DTO to JSON string and create RequestBody
        val jsonString = json.encodeToString(signUpData)
        val dataRequestBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

        // Create MultipartBody.Part for profile image if exists
        val imagePart =
            profileImage?.let { file ->
                val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("profileImage", file.name, requestBody)
            }

        return authService.signUp(
            data = dataRequestBody,
            profileImage = imagePart,
        )
    }

    /**
     * 토큰 갱신 API - refreshAuthService 사용 (데드락 방지)
     * TokenAuthenticator에서 호출되므로 별도의 OkHttpClient 사용 필요
     */
    override suspend fun refreshToken(refreshToken: String): BaseResponse<RefreshTokenResponseDto> {
        val requestDto =
            RefreshTokenRequestDto(
                refreshToken = refreshToken,
            )
        return refreshAuthService.refreshToken(request = requestDto)
    }
}
