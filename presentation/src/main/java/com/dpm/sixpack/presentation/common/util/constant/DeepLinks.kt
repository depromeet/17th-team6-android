package com.dpm.sixpack.presentation.common.util.constant

/**
 * 앱 전체에서 사용되는 딥링크 URI 상수 관리
 *
 * 딥링크 스킴: dorundorun://
 *
 * 알림 타입별 딥링크 매핑:
 * - CHEER_FRIEND: 친구 응원 → dorundorun://friend/profile/{friendId}
 * - FEED_UPLOADED: 친구의 피드 업로드 → dorundorun://feed/{feedId}
 * - FEED_REACTION: 피드 리액션 → dorundorun://feed/{feedId}
 * - FEED_REMINDER: 피드 업로드 독촉 → dorundorun://feed/upload
 * - RUNNING_PROGRESS_REMINDER: 러닝 진행 독촉 → dorundorun://running/start
 * - NEW_USER_RUNNING_REMINDER: 신규 가입 러닝 독촉 → dorundorun://running/start
 * - NEW_USER_FRIEND_REMINDER: 신규 가입 친구추가 독촉 → dorundorun://friend/add
 */
object DeepLinks {
    /**
     * 딥링크 스킴
     */
    const val SCHEME = "dorundorun"

    /**
     * Friend 관련 딥링크
     */
    object Friend {
        /**
         * 친구 프로필 화면
         * 예시: dorundorun://friend/profile/123
         *
         * 사용처:
         * - CHEER_FRIEND: 친구 응원 알림
         */
        const val PROFILE = "$SCHEME://friend/profile"

        /**
         * 친구 추가 화면
         * 예시: dorundorun://friend/add
         *
         * 사용처:
         * - NEW_USER_FRIEND_REMINDER: 신규 가입 친구추가 독촉
         */
        const val ADD = "$SCHEME://friend/add"
    }

    /**
     * Feed 관련 딥링크
     */
    object Feed {
        /**
         * 피드 상세 보기 화면
         * 예시: dorundorun://feed/123
         *
         * 사용처:
         * - FEED_UPLOADED: 친구의 피드 업로드 알림
         * - FEED_REACTION: 피드 리액션 알림
         */
        const val DETAIL = "$SCHEME://feed"

        /**
         * 피드 업로드 화면 (새 글 작성)
         * 예시: dorundorun://feed/upload
         *
         * 사용처:
         * - FEED_REMINDER: 피드 업로드 독촉 알림
         */
        const val UPLOAD = "$SCHEME://feed/upload"
    }

    /**
     * Running 관련 딥링크
     */
    object Running {
        /**
         * 러닝 시작 화면
         * 예시: dorundorun://running/start
         *
         * 사용처:
         * - RUNNING_PROGRESS_REMINDER: 러닝 진행 독촉 알림
         * - NEW_USER_RUNNING_REMINDER: 신규 가입 러닝 독촉 알림
         */
        const val START = "$SCHEME://running/start"
    }
}
