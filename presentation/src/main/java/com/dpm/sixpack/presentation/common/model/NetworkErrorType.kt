package com.dpm.sixpack.presentation.common.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 네트워크 및 서버 에러 타입을 정의하는 sealed class
 */
@Parcelize
sealed class NetworkErrorType(
    open val title: String,
    open val description: String,
) : Parcelable {
    /**
     * 404 에러: 페이지를 찾을 수 없음
     */
    @Parcelize
    data object NotFound : NetworkErrorType(
        title = "요청하신 페이지를 찾을 수 없어요.",
        description = "찾으시는 페이지가 사라졌거나 이동된 것 같아요.",
    )

    /**
     * 500 에러: 서버 내부 오류
     */
    @Parcelize
    data object ServerError : NetworkErrorType(
        title = "일시적인 오류가 발생했어요.",
        description = "죄송합니다. 서버에 잠시 문제가 생겼어요.\n잠시 후 다시 시도해주세요.",
    )

    /**
     * 502 에러: 서버 간 통신 오류
     */
    @Parcelize
    data object BadGateway : NetworkErrorType(
        title = "연결이 잠시 끊긴 것 같아요.",
        description = "서버 간 연결이 불안정해 내용을 불러올 수 없어요.\n잠시 후 다시 시도해주세요.",
    )

    /**
     * 네트워크 연결 오류
     */
    @Parcelize
    data object NetworkConnection : NetworkErrorType(
        title = "네트워크 연결이 불안정해요.",
        description = "인터넷 연결 상태를 확인한 뒤 다시 시도해주세요.",
    )

    /**
     * 커스텀 에러 (기타 에러 처리용)
     */
    @Parcelize
    data class Custom(
        override val title: String,
        override val description: String,
    ) : NetworkErrorType(
            title = title,
            description = description,
        )
}
