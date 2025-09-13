package com.dpm.sixpack.domain.exception

/**
 * Result에서 사용할 예외 클래스들
 */
sealed class DoRunException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    /**
     * 네트워크 관련 에러
     */
    data class NetworkError(
        override val message: String,
        override val cause: Throwable? = null
    ) : DoRunException(message, cause)

    /**
     * 서버 에러 (HTTP 5xx)
     */
    data class ServerError(
        val code: Int,
        override val message: String,
        override val cause: Throwable? = null
    ) : DoRunException("Server Error ($code): $message", cause)


    /**
     * 데이터 파싱/변환 에러
     */
    data class DataError(
        override val message: String,
        override val cause: Throwable? = null
    ) : DoRunException(message, cause)

    /**
     * 비즈니스 로직 에러
     */
    data class BusinessError(
        override val message: String,
        val errorCode: String? = null,
        override val cause: Throwable? = null
    ) : DoRunException(message, cause)

    /**
     * 알 수 없는 에러
     */
    data class UnknownError(
        override val message: String = "Unknown error occurred",
        override val cause: Throwable? = null
    ) : DoRunException(message, cause)
}
