package com.dpm.sixpack.data.repository

import com.dpm.sixpack.data.source.remote.datasoruce.api.AuthDataSource
import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.model.SignUpResult
import com.dpm.sixpack.domain.model.SmsVerificationResult
import com.dpm.sixpack.domain.repository.AuthRepository
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
) : AuthRepository {
    override suspend fun sendSmsCode(phoneNumber: String): DoRunResult<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val response = authDataSource.sendSmsCode(phoneNumber)
                val statusCode = response.code()

                when {
                    response.isSuccessful -> DoRunResult.Success(Unit)
                    statusCode == 400 -> {
                        val errorMessage = response.body()?.message ?: "잘못된 전화번호 형식입니다."
                        DoRunResult.Failure(
                            DoRunException.ValidationError(message = errorMessage),
                        )
                    }
                    statusCode == 429 -> {
                        val errorMessage = response.body()?.message ?: "너무 많은 요청입니다. 1분에 1회만 요청 가능합니다."
                        DoRunResult.Failure(
                            DoRunException.RateLimitError(message = errorMessage),
                        )
                    }
                    else -> {
                        val errorMessage = response.body()?.message ?: "SMS 인증 코드 발송에 실패했습니다."
                        DoRunResult.Failure(
                            DoRunException.UnknownError(message = errorMessage),
                        )
                    }
                }
            } catch (e: Exception) {
                DoRunResult.Failure(
                    DoRunException.NetworkError(
                        message = "SMS 인증 코드 발송에 실패했습니다: ${e.message}",
                        cause = e,
                    ),
                )
            }
        }

    override suspend fun verifySmsCode(
        phoneNumber: String,
        verificationCode: String,
    ): DoRunResult<SmsVerificationResult> =
        withContext(Dispatchers.IO) {
            try {
                val response = authDataSource.verifySmsCode(phoneNumber, verificationCode)
                val statusCode = response.code()

                when (statusCode) {
                    200 -> {
                        val verificationResult =
                            response.body()?.data?.toSmsVerificationResult()
                                ?: throw DoRunException.DataError("서버 응답 데이터가 비어 있습니다.")

                        // isExistingUser 필드로 기존/신규 사용자 구분 (UseCase에서 처리)
                        DoRunResult.Success(verificationResult)
                    }
                    400 -> {
                        val errorMessage = response.body()?.message ?: "인증 코드가 일치하지 않습니다."
                        DoRunResult.Failure(
                            DoRunException.CodeMismatchError(message = errorMessage),
                        )
                    }
                    410 -> {
                        val errorMessage = response.body()?.message ?: "인증 시간이 만료되었습니다."
                        DoRunResult.Failure(
                            DoRunException.CodeExpiredError(message = errorMessage),
                        )
                    }
                    else -> {
                        val errorMessage = response.body()?.message ?: "SMS 인증 코드 확인에 실패했습니다."
                        DoRunResult.Failure(
                            DoRunException.NetworkError(message = errorMessage),
                        )
                    }
                }
            } catch (e: DoRunException) {
                DoRunResult.Failure(e)
            } catch (e: Exception) {
                DoRunResult.Failure(
                    DoRunException.NetworkError(
                        message = "SMS 인증 코드 확인에 실패했습니다: ${e.message}",
                        cause = e,
                    ),
                )
            }
        }

    override suspend fun signUp(
        nickname: String,
        phoneNumber: String,
        profileImage: File?,
    ): DoRunResult<SignUpResult> =
        withContext(Dispatchers.IO) {
            try {
                val response = authDataSource.signUp(nickname, phoneNumber, profileImage)

                val signUpResult =
                    response.data?.toSignUpResult()
                        ?: throw DoRunException.DataError("서버 응답 데이터가 비어 있습니다.")

                DoRunResult.Success(signUpResult)
            } catch (e: DoRunException) {
                DoRunResult.Failure(e)
            } catch (e: Exception) {
                DoRunResult.Failure(
                    DoRunException.NetworkError(
                        message = "회원가입에 실패했습니다: ${e.message}",
                        cause = e,
                    ),
                )
            }
        }

    override suspend fun refreshToken(refreshToken: String): DoRunResult<com.dpm.sixpack.domain.model.AuthToken> =
        withContext(Dispatchers.IO) {
            try {
                val response = authDataSource.refreshToken(refreshToken)

                val authToken =
                    response.data?.toAuthToken()
                        ?: throw DoRunException.DataError("서버 응답 데이터가 비어 있습니다.")

                DoRunResult.Success(authToken)
            } catch (e: DoRunException) {
                DoRunResult.Failure(e)
            } catch (e: Exception) {
                DoRunResult.Failure(
                    DoRunException.NetworkError(
                        message = "토큰 갱신에 실패했습니다: ${e.message}",
                        cause = e,
                    ),
                )
            }
        }

    override suspend fun logout(): DoRunResult<Unit> =
        withContext(Dispatchers.IO) {
            try {
                authDataSource.logout()
                DoRunResult.Success(Unit)
            } catch (e: DoRunException) {
                DoRunResult.Failure(e)
            } catch (e: Exception) {
                DoRunResult.Failure(
                    DoRunException.NetworkError(
                        message = "로그아웃에 실패했습니다: ${e.message}",
                        cause = e,
                    ),
                )
            }
        }

    override suspend fun withdraw(): DoRunResult<Unit> =
        withContext(Dispatchers.IO) {
            try {
                authDataSource.withdraw()
                DoRunResult.Success(Unit)
            } catch (e: DoRunException) {
                DoRunResult.Failure(e)
            } catch (e: Exception) {
                DoRunResult.Failure(
                    DoRunException.NetworkError(
                        message = "회원 탈퇴에 실패했습니다: ${e.message}",
                        cause = e,
                    ),
                )
            }
        }
}
