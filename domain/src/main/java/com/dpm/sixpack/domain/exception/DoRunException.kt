package com.dpm.sixpack.domain.exception

/**
 * Result에서 사용할 예외 클래스들
 */
sealed class DoRunException(
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause) {
    /**
     * 네트워크 관련 에러
     */
    data class NetworkError(
        override val message: String,
        override val cause: Throwable? = null,
    ) : DoRunException(message, cause)

    /**
     * 서버 에러 (HTTP 5xx)
     */
    data class ServerError(
        val code: Int,
        override val message: String,
        override val cause: Throwable? = null,
    ) : DoRunException("Server Error ($code): $message", cause)

    /**
     * 데이터 파싱/변환 에러
     */
    data class DataError(
        override val message: String,
        override val cause: Throwable? = null,
    ) : DoRunException(message, cause)

    /**
     * 데이터베이스 에러
     */
    data class DatabaseError(
        override val message: String,
        override val cause: Throwable? = null,
    ) : DoRunException(message, cause)

    /**
     * 비즈니스 로직 에러
     */
    data class BusinessError(
        override val message: String,
        val errorCode: String? = null,
        override val cause: Throwable? = null,
    ) : DoRunException(message, cause)

    /**
     * 유효성 검증 에러 (HTTP 400)
     */
    data class ValidationError(
        override val message: String,
        override val cause: Throwable? = null,
    ) : DoRunException(message, cause)

    /**
     * Rate Limit 에러 (HTTP 429)
     */
    data class RateLimitError(
        override val message: String,
        override val cause: Throwable? = null,
    ) : DoRunException(message, cause)

    /**
     * 인증 코드 불일치 (HTTP 400)
     */
    data class CodeMismatchError(
        override val message: String,
        override val cause: Throwable? = null,
    ) : DoRunException(message, cause)

    /**
     * 인증 코드 만료 (HTTP 410)
     */
    data class CodeExpiredError(
        override val message: String,
        override val cause: Throwable? = null,
    ) : DoRunException(message, cause)

    /**
     * 미가입 사용자 (HTTP 201)
     * 인증은 성공했지만 회원가입이 필요한 경우
     */
    data class UserNotRegisteredError(
        override val message: String,
        override val cause: Throwable? = null,
    ) : DoRunException(message, cause)

    /**
     * 알 수 없는 에러
     */
    data class UnknownError(
        override val message: String = "Unknown error occurred",
        override val cause: Throwable? = null,
    ) : DoRunException(message, cause)
}
