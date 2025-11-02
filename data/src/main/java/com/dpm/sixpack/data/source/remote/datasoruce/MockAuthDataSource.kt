package com.dpm.sixpack.data.source.remote.datasoruce

import com.dpm.sixpack.data.source.remote.datasoruce.api.AuthDataSource
import com.dpm.sixpack.data.source.remote.dto.response.SignUpResponseDto
import com.dpm.sixpack.data.source.remote.dto.response.TokenDto
import com.dpm.sixpack.data.source.remote.dto.response.UserDto
import com.dpm.sixpack.data.source.remote.dto.response.VerifySmsResponseDto
import com.dpm.sixpack.data.source.remote.util.base.BaseResponse
import retrofit2.Response
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * Mock 구현체 - 개발/테스트용
 * TODO: 서버 API가 안정화되면 AuthDataSourceImpl로 교체
 */
class MockAuthDataSource @Inject constructor() : AuthDataSource {
    private fun getCurrentTimestamp(): String =
        LocalDateTime
            .now()
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

    override suspend fun sendSmsCode(phoneNumber: String): Response<BaseResponse<Unit>> {
        // 200 성공 응답 반환
        return Response.success(
            200,
            BaseResponse(
                status = "200",
                message = "인증번호가 발송되었습니다.",
                timestamp = getCurrentTimestamp(),
                data = Unit,
            ),
        )
    }

    override suspend fun verifySmsCode(
        phoneNumber: String,
        verificationCode: String,
    ): Response<BaseResponse<VerifySmsResponseDto>> {
        // 200 성공 응답 - 기존 사용자로 모킹
        return Response.success(
            201,
            BaseResponse(
                status = "201",
                message = "인증 성공",
                timestamp = getCurrentTimestamp(),
                data =
                    VerifySmsResponseDto(
                        phoneNumber = phoneNumber,
                        isExistingUser = true,
                        user =
                            UserDto(
                                id = 1L,
                                nickname = "MockUser",
                            ),
                        token =
                            TokenDto(
                                accessToken = "mock_access_token",
                                refreshToken = "mock_refresh_token",
                            ),
                    ),
            ),
        )
    }

    override suspend fun signUp(
        nickname: String,
        phoneNumber: String,
        profileImage: File?,
    ): BaseResponse<SignUpResponseDto> {
        // 회원가입 성공 응답
        return BaseResponse(
            status = "200",
            message = "회원가입 성공",
            timestamp = getCurrentTimestamp(),
            data =
                SignUpResponseDto(
                    user =
                        UserDto(
                            id = 1L,
                            nickname = nickname,
                        ),
                    token =
                        TokenDto(
                            accessToken = "mock_access_token",
                            refreshToken = "mock_refresh_token",
                        ),
                ),
        )
    }
}
