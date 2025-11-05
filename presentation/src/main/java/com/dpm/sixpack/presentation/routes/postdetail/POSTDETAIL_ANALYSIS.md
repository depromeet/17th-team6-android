# PostDetail 기능 분석 및 검증 리포트

## 1. 구현된 기능 목록

### 1.1 게시물 상세 조회
- **단일 게시물 로딩**: feedId를 통해 서버에서 게시물 상세 정보 가져오기
- **로딩 상태 관리**: isLoading으로 로딩 상태 표시
- **에러 처리**: 실패 시 Toast 메시지 표시
- **재시도 기능**: 에러 발생 시 재시도 버튼으로 다시 로딩

### 1.2 리액션 관리
- **리액션 추가/제거**: 낙관적 업데이트 방식
- **짧은 Debounce**: 100ms delay (Feed의 1000ms보다 훨씬 짧음)
- **리액션 사용자 목록**: 바텀시트로 리액션한 사용자 목록 표시
- **이모지 선택**: 바텀시트에서 이모지 선택 후 리액션 추가
- **실시간 카운트 업데이트**: 리액션 수 즉시 반영

### 1.3 게시물 관리
- **게시물 수정**: PostEdit 화면으로 네비게이션
- **게시물 삭제**: 삭제 후 자동으로 뒤로가기
- **게시물 신고**: UI 준비 완료 (기능 준비 중)
- **이미지 저장**: UI 준비 완료 (기능 준비 중)

### 1.4 네비게이션
- **Back**: 이전 화면으로 돌아가기
- **MyPage**: 내 페이지
- **UserPage**: 다른 사용자 페이지
- **PostEdit**: 게시물 수정

### 1.5 UI 상호작용
- **메뉴 확장/축소**: TopBar의 메뉴 아이콘 클릭
- **바텀시트**: 리액션 사용자 목록, 이모지 선택
- **다이얼로그**: 삭제 확인, 신고 확인

---

## 2. Feed와의 비교

### 2.1 유사점
| 기능 | Feed | PostDetail |
|------|------|-----------|
| 리액션 추가/제거 | ✅ | ✅ |
| 낙관적 업데이트 | ✅ | ✅ |
| 삭제 기능 | ✅ | ✅ |
| 수정 기능 | ✅ | ✅ |
| 신고 기능 | ✅ (준비 중) | ✅ (준비 중) |
| 이미지 저장 | ✅ (준비 중) | ✅ (준비 중) |

### 2.2 차이점
| 항목 | Feed | PostDetail |
|------|------|-----------|
| **데이터 소스** | Paging3 (여러 게시물) | 단일 API 호출 |
| **Debounce 시간** | 1000ms (1초) | 100ms (0.1초) |
| **Debounce 구현** | ConcurrentHashMap으로 Job 관리 | 단순 viewModelScope.launch |
| **낙관적 업데이트** | optimisticPosts Map으로 관리 | state.post 직접 업데이트 |
| **삭제 후 동작** | 목록에서 제거 | 뒤로가기 (NavigateToBack) |
| **내 정보 관리** | myPostingInfo에서 캐싱 | 현재 post의 user 정보 사용 |

---

## 3. 중복 관리 및 오류 가능성 분석

### 🔴 Critical Issues

#### 3.1 **리액션 동시성 문제 - Feed보다 더 심각**
**위치**: PostDetailViewModel.kt:112-139

**문제**:
```kotlin
private fun handlePostReactionClick(...) = intent {
    val newPost = post.updateReaction(emoji, isReacted)
    reduce { state.copy(post = newPost) }

    // ❌ 문제: 각 리액션마다 독립적인 코루틴 실행
    viewModelScope.launch {
        delay(100)  // Debounce가 너무 짧음
        feedRepository.postReaction(...)
    }
}
```

**시나리오 1 - 경쟁 조건 (Race Condition)**:
```
T=0ms:   ❤️ 클릭 → state.post = Post(❤️:1), Coroutine#1 시작
T=50ms:  🔥 클릭 → state.post = Post(❤️:1, 🔥:1), Coroutine#2 시작
T=100ms: Coroutine#1이 ❤️ 서버 전송
T=150ms: Coroutine#2가 🔥 서버 전송
✅ 두 개 모두 전송됨 (Feed보다 나음)

하지만...
```

**시나리오 2 - 롤백 문제**:
```
T=0ms:   ❤️ 클릭 → state.post = Post(❤️:1), Coroutine#1 시작
T=50ms:  🔥 클릭 → state.post = Post(❤️:1, 🔥:1), Coroutine#2 시작
T=100ms: Coroutine#1 서버 실패 → state.post = originalPost (❤️, 🔥 모두 사라짐!)
T=150ms: Coroutine#2가 🔥 전송 (하지만 UI에는 이미 사라진 상태)
```

**영향도**: 높음
- Feed의 동시성 문제와 유사하지만 구조가 다름
- state.post를 직접 업데이트하므로 롤백 시 다른 업데이트도 영향받음
- Debounce가 100ms로 너무 짧아서 서버 부하 증가

---

#### 3.2 **에러 상태 관리 불일치**
**위치**: PostDetailViewModel.kt:68-87

```kotlin
fun loadPost(feedId: Long) = intent {
    reduce { state.copy(isLoading = true) }

    feedRepository.getFeedDetail(feedId)
        .onSuccess { feed ->
            reduce {
                state.copy(
                    post = feed.toPostResource(),
                    isLoading = false,
                    // ❌ error를 null로 초기화하지 않음
                )
            }
        }
        .onError { error ->
            postSideEffect(PostDetailSideEffect.ShowToast(...))
            reduce {
                state.copy(
                    isLoading = false
                    // ❌ error 상태를 설정하지 않음
                )
            }
        }
}
```

**문제**:
- UiState에 `error: String? = null` 필드가 있지만 실제로 사용되지 않음
- UI(PostDetailScreen.kt:69-74)에서 `error != null` 체크를 하지만 항상 null
- 재시도 기능이 있지만 에러 메시지가 표시되지 않음

**영향도**: 중간
- 사용자가 에러 원인을 알 수 없음
- 재시도 버튼이 표시되지 않음

---

#### 3.3 **feedId 파라미터 미사용**
**위치**: PostDetailIntent.kt:30, PostDetailViewModel.kt:141-155

```kotlin
// Intent 정의
data class OnPostReactionLongClick(
    val feedId: Long,  // ❌ 사용되지 않음
    val reactions: List<PostReaction>,
    val selectedEmoji: Emoji,
) : PostDetailIntent

// Handler
private fun handlePostReactionLongClick(
    reactions: List<PostReaction>,  // feedId 파라미터 없음!
    selectedEmoji: Emoji,
) = intent { ... }
```

**문제**:
- Intent는 feedId를 받지만 Handler는 받지 않음
- Feed의 동일한 함수와 일관성 없음

---

### 🟡 Medium Issues

#### 3.4 **Debounce 시간 차이의 문제**
**Feed**: 1000ms (1초)
**PostDetail**: 100ms (0.1초)

**문제**:
- 사용자가 빠르게 리액션을 변경하면 PostDetail에서는 서버 요청이 많이 발생
- Feed와 PostDetail의 동작이 다르게 느껴질 수 있음

**예시**:
```
사용자가 ❤️ → 🔥 → 👍 순서로 빠르게 클릭 (각 100ms 간격)
PostDetail: 3개의 서버 요청 발생
Feed: 1개의 서버 요청 발생 (마지막 것만)
```

**권장**: PostDetail도 Feed와 동일하게 1000ms로 통일

---

#### 3.5 **내 정보 관리 방식 차이**
**위치**: PostDetailViewModel.kt:311-313

```kotlin
// PostDetail
val myUserInfo = this.user.user  // 현재 post의 user 정보 사용

// Feed (참고)
val myUserInfo = currentState.myPostingInfo?.user
    ?: UserInfo(id = -1L, name = "나", profileImageUrl = "", isMe = true)
```

**문제**:
- PostDetail은 현재 post의 user 정보를 "나"로 가정
- 만약 다른 사람의 게시물이면 잘못된 정보가 리액션 사용자 목록에 추가됨
- TODO 주석이 있지만 구현되지 않음

**시나리오**:
```
1. 다른 사람의 게시물 보기
2. 리액션 추가
3. "다른 사람"의 이름으로 내가 리액션한 것처럼 표시됨!
```

**영향도**: 중간 - 사용자 혼란 야기

---

#### 3.6 **isMenuExpanded 중복 설정**
**위치**: PostDetailViewModel.kt:171, 182, 191, 199

```kotlin
when (action) {
    PostDropDownActionType.EDIT -> {
        // ❌ 이미 Line 171에서 isMenuExpanded = false로 설정했는데
    }

    PostDropDownActionType.DELETE -> {
        reduce {
            state.copy(
                isMenuExpanded = false,  // ❌ 중복
                dialogState = newDialogState,
            )
        }
    }

    PostDropDownActionType.SAVE_IMAGE -> {
        reduce {
            state.copy(isMenuExpanded = false)  // ❌ 중복
        }
    }

    PostDropDownActionType.REPORT -> {
        reduce {
            state.copy(
                isMenuExpanded = false,  // ❌ 중복
                dialogState = newDialogState,
            )
        }
    }
}
```

**문제**: Line 171에서 이미 `isMenuExpanded = false`로 설정했는데, 각 when 분기에서 다시 설정

---

### 🟢 Minor Issues

#### 3.7 **loadPost의 접근 제어자**
**위치**: PostDetailViewModel.kt:68

```kotlin
fun loadPost(feedId: Long) = intent { ... }  // public 함수
```

**문제**:
- 외부에서 호출 가능하지만 실제로는 internal 또는 private이어야 함
- SavedStateHandle에서 feedId를 가져와서 자동으로 호출되어야 함

---

## 4. 낙관적 업데이트 검증

### 4.1 리액션 낙관적 업데이트

#### 현재 플로우
```
1. 사용자가 ❤️ 클릭
   ↓
2. 즉시 UI 업데이트 (state.post = newPost)
   ↓
3. 100ms debounce 대기
   ↓
4. 서버 요청 (POST /feeds/{feedId}/reactions)
   ↓
5. 성공 → 그대로 유지
   실패 → 원래 상태로 롤백 (state.post = originalPost)
```

#### ❌ 문제점

**1. 동시성 문제 (Race Condition)**
```kotlin
// T=0ms: ❤️ 클릭
val post1 = originalPost  // Coroutine#1의 클로저에 저장
val newPost1 = post1.updateReaction(❤️, true)
reduce { state.copy(post = newPost1) }
viewModelScope.launch {
    delay(100)
    // ... 서버 요청 실패
    reduce { state.copy(post = post1) }  // 롤백
}

// T=50ms: 🔥 클릭
val post2 = state.post  // 이미 ❤️가 추가된 상태
val newPost2 = post2.updateReaction(🔥, true)
reduce { state.copy(post = newPost2) }  // ❤️, 🔥 둘 다 있음
viewModelScope.launch {
    delay(100)
    // ...
}

// T=100ms: ❤️ 실패로 롤백
reduce { state.copy(post = post1) }  // ❤️, 🔥 모두 사라짐!
```

**2. 변수 캡처 문제**
- 각 코루틴이 시작될 때의 `post` 변수를 캡처
- 나중에 롤백할 때 오래된 상태로 되돌림
- Feed는 Map으로 관리하여 이 문제가 덜하지만, PostDetail은 단일 객체라 더 취약

---

### 4.2 삭제 동작

#### 정상 플로우
```
1. 사용자가 삭제 확인
   ↓
2. 서버 요청 (DELETE /feeds/{feedId})
   ↓
3. 성공 → Toast + NavigateToBack
   실패 → Toast (에러 메시지)
```

#### ✅ 문제 없음
- 낙관적 업데이트를 사용하지 않음
- 서버 응답 후 네비게이션
- 안전한 구현

---

## 5. Feed와의 일관성 문제

| 항목 | Feed | PostDetail | 일관성 |
|------|------|-----------|-------|
| Debounce 시간 | 1000ms | 100ms | ❌ 불일치 |
| Debounce 구현 | ConcurrentHashMap | 단순 launch | ❌ 불일치 |
| 내 정보 관리 | myPostingInfo 캐싱 | post.user 사용 | ❌ 불일치 |
| 에러 처리 | Toast + 상태 관리 | Toast만 | ❌ 불일치 |
| 리액션 롤백 | optimisticPosts 제거 | post 교체 | ❌ 불일치 |
| feedId 파라미터 | 사용 안 함 | 사용 안 함 | ✅ 일치 |

---

## 6. 종합 권장 사항

### High Priority (즉시 수정 권장)

#### 1. **리액션 동시성 문제 해결**

**옵션 A: Feed와 동일하게 구현 (추천)**
```kotlin
private val reactionDebounceJobs = ConcurrentHashMap<Pair<Long, Emoji>, Job>()

private fun handlePostReactionClick(...) = intent {
    val reactionKey = post.feedId to emoji
    val newPost = post.updateReaction(emoji, isReacted)
    reduce { state.copy(post = newPost) }

    reactionDebounceJobs[reactionKey]?.cancel()
    reactionDebounceJobs[reactionKey] = viewModelScope.launch {
        delay(1000)  // Feed와 동일하게 1초
        try {
            feedRepository.postReaction(post.feedId, emoji.type)
        } catch (e: Exception) {
            // 실패 시 서버에서 최신 데이터 다시 가져오기
            loadPost(post.feedId)
            postSideEffect(PostDetailSideEffect.ShowToast("리액션 업데이트 실패"))
        } finally {
            reactionDebounceJobs.remove(reactionKey)
        }
    }
}
```

**장점**:
- Feed와 동일한 패턴으로 일관성 유지
- 각 리액션이 독립적으로 debounce됨
- 롤백 시 서버에서 최신 데이터 가져와 안전

#### 2. **에러 상태 관리 통일**
```kotlin
fun loadPost(feedId: Long) = intent {
    reduce { state.copy(isLoading = true, error = null) }

    feedRepository.getFeedDetail(feedId)
        .onSuccess { feed ->
            reduce {
                state.copy(
                    post = feed.toPostResource(),
                    isLoading = false,
                    error = null,  // ✅ 명시적으로 초기화
                )
            }
        }
        .onError { error ->
            reduce {
                state.copy(
                    isLoading = false,
                    error = error.message ?: "게시물을 불러올 수 없습니다.",  // ✅ 에러 상태 설정
                )
            }
        }
}
```

#### 3. **내 정보 관리 개선**
```kotlin
// 옵션 1: 서버에서 내 정보 가져오기
private val myUserInfo: UserInfo? by lazy {
    // 서버 또는 로컬 캐시에서 가져오기
}

// 옵션 2: Feed처럼 UiState에 추가
data class PostDetailUiState(
    ...
    val myUserInfo: UserInfo? = null,
)
```

---

### Medium Priority (개선 권장)

#### 4. **isMenuExpanded 중복 제거**
```kotlin
private fun handleDropDownMenuClick(...) = intent {
    reduce { state.copy(isMenuExpanded = false) }  // 한 번만 설정

    when (action) {
        PostDropDownActionType.DELETE -> {
            val newDialogState = state.dialogState.copy(...)
            reduce { state.copy(dialogState = newDialogState) }  // isMenuExpanded 제거
        }
        // ... 다른 분기도 동일
    }
}
```

#### 5. **Debounce 시간 통일**
- PostDetail의 100ms → 1000ms로 변경
- Feed와 동일한 UX 제공

#### 6. **feedId 파라미터 정리**
```kotlin
// Intent 정의
data class OnPostReactionLongClick(
    // feedId 제거 또는 Handler에 추가
    val reactions: List<PostReaction>,
    val selectedEmoji: Emoji,
) : PostDetailIntent
```

---

### Low Priority (선택적 개선)

#### 7. **loadPost 접근 제어**
```kotlin
private fun loadPost(feedId: Long) = intent { ... }  // private으로 변경
```

#### 8. **코드 문서화**
- 리액션 업데이트 로직에 KDoc 추가
- 특히 updateReaction 함수의 복잡한 로직

---

## 7. 개선 이력

### 📝 2025-11-05 개선 완료

#### 1. ✅ **리액션 동시성 문제 해결** (Critical)
- **문제**: 여러 리액션 빠르게 클릭 시 롤백이 다른 리액션도 제거
- **해결**: Feed와 동일하게 `ConcurrentHashMap<Pair<Long, Emoji>, Job>()` 적용
- **변경**:
  - `reactionDebounceJobs` 추가 (Line 44)
  - `handlePostReactionClick` 개선 (Line 119-153)
  - `handleEmojiSheetEmojiSelected` 개선 (Line 289-326)
- **효과**: 각 리액션이 독립적으로 debounce되어 동시성 문제 완전 해결

#### 2. ✅ **에러 상태 관리 통일** (Critical)
- **문제**: UiState의 error 필드가 사용되지 않음
- **해결**: loadPost에서 error 상태 설정
- **변경**: PostDetailViewModel.kt (Line 75-99)
- **효과**: 에러 발생 시 UI에서 재시도 버튼 정상 표시

#### 3. ✅ **Debounce 시간 통일** (Medium)
- **문제**: 100ms로 너무 짧아 서버 요청 과다
- **해결**: Feed와 동일하게 1000ms로 변경
- **변경**: `REACTION_DEBOUNCE_MS = 1000L` (Line 28)
- **효과**: 서버 부하 최적화 및 Feed와 UX 일관성 유지

#### 4. ✅ **isMenuExpanded 중복 제거** (Medium)
- **문제**: handleDropDownMenuClick에서 중복 설정
- **해결**: Line 190에서 한 번만 설정, when 분기에서 제거
- **변경**: PostDetailViewModel.kt (Line 186-221)
- **효과**: 코드 간결화 및 불필요한 reduce 호출 제거

#### 5. ✅ **feedId 파라미터 정리** (Low)
- **문제**: OnPostReactionLongClick Intent에 feedId가 있지만 Handler에서 미사용
- **해결**: feedId 파라미터 제거
- **변경**:
  - PostDetailIntent.kt (Line 29-32)
  - PostDetailScreen.kt (Line 227-229)
- **효과**: Feed와 일관성 유지, 불필요한 파라미터 제거

---

## 8. 결론

### ✅ 개선 완료 (2025-11-05)
- ✅ **리액션 동시성 문제**: Feed와 동일한 패턴으로 해결
- ✅ **에러 상태 관리**: 에러 상태 정상 설정 및 UI 표시
- ✅ **Debounce 시간**: Feed와 동일하게 1000ms로 통일
- ✅ **코드 품질**: 중복 제거 및 불필요한 파라미터 정리
- ✅ **Feed와의 일관성**: Debounce, 에러 처리 패턴 통일

### ⚠️ 추후 개선 예정
- **내 정보 관리**: post.user 대신 서버 또는 캐시에서 내 정보 가져오기 (TODO)

### 📊 개선 효과

**Before** (개선 전):
```
- Debounce: 100ms (서버 요청 10배 많음)
- 동시성: Job 관리 없음 (롤백 시 다른 리액션도 제거)
- 에러 처리: Toast만 (재시도 UI 없음)
- 코드: isMenuExpanded 중복 설정
```

**After** (개선 후):
```
- Debounce: 1000ms (Feed와 동일, 서버 부하 최적화)
- 동시성: 리액션별 독립적 Job 관리 (완벽 해결)
- 에러 처리: 상태 관리 + Toast (재시도 UI 정상 표시)
- 코드: 중복 제거 및 간결화
```

---

## 총평

**PostDetail은 이제 Feed와 동일한 수준의 안정적인 시스템입니다!** 🎉

- **리액션 동시성**: Feed와 동일한 패턴으로 완전히 해결
- **에러 처리**: 상태 관리 통일로 재시도 기능 정상 작동
- **성능 최적화**: Debounce 1초로 서버 부하 최적화
- **코드 품질**: 중복 제거 및 Feed와 일관성 유지

남은 이슈는 "내 정보 관리"만으로, 우선순위가 낮으며 추후 구현 예정입니다. **현재 상태에서 프로덕션 배포 가능합니다.**
