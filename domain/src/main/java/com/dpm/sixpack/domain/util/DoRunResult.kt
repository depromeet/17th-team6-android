package com.dpm.sixpack.domain.util

import com.dpm.sixpack.domain.exception.DoRunException

/**
 * Clean Architecture용 Result 래퍼 클래스
 * 성공/실패를 명확하게 구분하고 에러 처리를 용이하게 합니다.
 */
sealed class DoRunResult<out T> {
    /**
     * 성공 케이스
     */
    data class Success<T>(val data: T) : DoRunResult<T>()

    /**
     * 실패 케이스
     */
    data class Failure(val exception: DoRunException) : DoRunResult<Nothing>()

    /**
     * 성공 여부 확인
     */
    val isSuccess: Boolean
        get() = this is Success

    /**
     * 실패 여부 확인
     */
    val isFailure: Boolean
        get() = this is Failure

    /**
     * 성공 데이터 반환 (실패시 null)
     */
    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Failure -> null
    }

    /**
     * 성공시 변환 수행
     */
    inline fun <R> map(transform: (T) -> R): DoRunResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Failure -> this
    }

    /**
     * 성공시 액션 수행
     */
    inline fun onSuccess(action: (T) -> Unit): DoRunResult<T> {
        if (this is Success) action(data)
        return this
    }

    /**
     * 실패시 액션 수행
     */
    inline fun onError(action: (DoRunException) -> Unit): DoRunResult<T> {
        if (this is Failure) action(exception)
        return this
    }

    /**
     * 성공/실패 모든 케이스 처리
     */
    inline fun <R> fold(
        onSuccess: (T) -> R,
        onError: (DoRunException) -> R
    ): R = when (this) {
        is Success -> onSuccess(data)
        is Failure -> onError(exception)
    }
}

