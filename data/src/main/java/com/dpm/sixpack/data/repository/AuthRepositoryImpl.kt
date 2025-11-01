package com.dpm.sixpack.data.repository

import com.dpm.sixpack.data.source.remote.datasoruce.AuthDataSource
import com.dpm.sixpack.domain.exception.DoRunException
import com.dpm.sixpack.domain.model.SignUpResult
import com.dpm.sixpack.domain.model.SmsVerificationResult
import com.dpm.sixpack.domain.repository.AuthRepository
import com.dpm.sixpack.domain.repository.UserPreferenceRepository
import com.dpm.sixpack.domain.util.DoRunResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val userPreferenceRepository: UserPreferenceRepository,
) : AuthRepository {
    override suspend fun sendSmsCode(phoneNumber: String): DoRunResult<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val response = authDataSource.sendSmsCode(phoneNumber)

                if (response.data == null) {
                    DoRunResult.Success(Unit)
                } else {
                    DoRunResult.Success(Unit)
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

                val verificationResult =
                    response.data?.toSmsVerificationResult()
                        ?: throw DoRunException.DataError("서버 응답 데이터가 비어 있습니다.")

                // 기존 회원이면 userId와 token 저장
                if (verificationResult.isExistingUser) {
                    verificationResult.user?.let { user ->
                        userPreferenceRepository.updateUserId(user.id)
                    }
                    verificationResult.token?.let { token ->
                        userPreferenceRepository.updateAccessToken(token.accessToken)
                        userPreferenceRepository.updateRefreshToken(token.refreshToken)
                    }
                }

                DoRunResult.Success(verificationResult)
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

                // 회원가입 성공 시 userId와 token 저장
                userPreferenceRepository.updateUserId(signUpResult.user.id)
                userPreferenceRepository.updateAccessToken(signUpResult.token.accessToken)
                userPreferenceRepository.updateRefreshToken(signUpResult.token.refreshToken)

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
}
