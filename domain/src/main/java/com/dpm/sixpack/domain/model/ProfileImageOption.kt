package com.dpm.sixpack.domain.model

/**
 * 프로필 이미지 처리 옵션
 */
enum class ProfileImageOption {
    /** 새 이미지로 교체 */
    SET,

    /** 이미지 삭제 */
    REMOVE,

    /** 기존 이미지 유지 */
    KEEP,
}
