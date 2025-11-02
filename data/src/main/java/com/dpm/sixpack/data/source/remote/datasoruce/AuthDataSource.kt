package com.dpm.sixpack.data.source.remote.datasoruce

import com.dpm.sixpack.data.source.remote.dto.request.SendSmsRequestDto
import com.dpm.sixpack.data.source.remote.dto.request.SignUpRequestDto
import com.dpm.sixpack.data.source.remote.dto.request.VerifySmsRequestDto
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

class AuthDataSource @Inject constructor(
    private val authService: AuthService,
    private val json: Json,
) {
    suspend fun sendSmsCode(phoneNumber: String): Response<BaseResponse<Unit>> {
        val requestDto =
            SendSmsRequestDto(
                phoneNumber = phoneNumber,
            )
        return authService.sendSmsCode(request = requestDto)
    }

    suspend fun verifySmsCode(
        phoneNumber: String,
        verificationCode: String,
    ): BaseResponse<VerifySmsResponseDto> {
        val requestDto =
            VerifySmsRequestDto(
                phoneNumber = phoneNumber,
                verificationCode = verificationCode,
            )
        return authService.verifySmsCode(request = requestDto)
    }

    suspend fun signUp(
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
}
