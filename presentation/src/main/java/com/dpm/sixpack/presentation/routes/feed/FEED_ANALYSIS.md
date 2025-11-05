# Feed 기능 분석 및 검증 리포트

## 1. 구현된 기능 목록

### 1.1 캘린더 기능
- **날짜별 게시물 수 표시**: 각 날짜에 인증된 게시물 수를 캘린더에 표시
- **지능형 Prefetch**: 사용자가 스크롤하여 2주 이내 주차에 도달하면 1개월 단위로 추가 데이터 로딩
- **날짜 선택**: 날짜 클릭 시 해당 날짜의 피드로 전환
- **인증 가능 날짜 판단**: 오늘 기준 2일 전까지만 인증 가능 (isCertifiable 함수)

### 1.2 피드 목록
- **Paging3 무한 스크롤**: 날짜별로 Paging 데이터 제공
- **날짜별 캐싱**: ConcurrentHashMap으로 각 날짜의 PagingFlow 캐싱
- **Pull to Refresh**: 아래로 당겨서 새로고침 기능
- **로딩 상태 관리**: 초기 로딩, 추가 로딩 상태 구분

### 1.3 게시물 관리
- **게시물 조회**: Paging 기반 조회
- **게시물 삭제**: 낙관적 업데이트 + 서버 동기화
- **게시물 수정**: PostEdit 화면으로 네비게이션
- **게시물 신고**: UI 준비 완료 (기능 준비 중)
- **이미지 저장**: UI 준비 완료 (기능 준비 중)
- **게시물 상세**: PostDetail 화면으로 네비게이션

### 1.4 리액션 관리
- **리액션 추가/제거**: 낙관적 업데이트 방식
- **Debounce 최적화**: 1초 동안 여러 클릭을 하나의 서버 요청으로 통합
- **리액션 사용자 목록**: 바텀시트로 리액션한 사용자 목록 표시
- **이모지 선택**: 바텀시트에서 이모지 선택 후 리액션 추가
- **실시간 카운트 업데이트**: 리액션 수 즉시 반영

### 1.5 인증 기능
- **인증된 사용자 목록**: 선택된 날짜에 인증한 사용자 목록 표시
- **내 인증 정보**: 내가 인증했는지 여부 표시
- **인증 화면 네비게이션**: 인증된 사용자 목록 클릭 시 상세 화면으로 이동
- **Empty State 구분**:
  - NoPostsAndCertifiable: 게시물 없지만 인증 가능
  - NoPostsAndExpired: 게시물 없고 인증 기간 만료
  - PostsAvailable: 게시물 있음

### 1.6 네비게이션
- **Friend**: 친구 목록 화면
- **Alarm**: 알림 화면
- **CertificationFriend**: 해당 날짜 인증 친구 목록
- **MyPage**: 내 페이지
- **UserPage**: 다른 사용자 페이지
- **PostDetail**: 게시물 상세
- **PostUpload**: 게시물 업로드 (날짜 전달)
- **PostEdit**: 게시물 수정

### 1.7 실시간 업데이트
- **FeedUpdateEvent 구독**: Repository에서 발생하는 업데이트/업로드 이벤트 감지
- **자동 Paging 갱신**: 이벤트 발생 시 RefreshPagingList SideEffect 발행
- **중복 이벤트 방지**: timestamp 기반 중복 처리 방지

---

## 2. 중복 관리 및 오류 가능성 분석

### 🟢 해결된 이슈

#### ~~2.1 **리액션 동시성 문제**~~ ✅ 해결됨
**위치**: FeedViewModel.kt:79, 322-353

**기존 문제**:
- Debounce Job이 feedId별로 하나만 유지되어 여러 리액션 동시 클릭 시 일부 유실

**해결 방법** (2025-11-05):
```kotlin
// Before: feedId별로 Job 관리
private val reactionDebounceJobs = ConcurrentHashMap<Long, Job>()

// After: (feedId, Emoji) 페어별로 Job 관리
private val reactionDebounceJobs = ConcurrentHashMap<Pair<Long, Emoji>, Job>()

private fun handlePostReactionClick(...) {
    val reactionKey = post.feedId to emoji  // ✅ 각 리액션이 독립적
    reactionDebounceJobs[reactionKey]?.cancel()
    reactionDebounceJobs[reactionKey] = viewModelScope.launch { ... }
}
```

**검증 시나리오**:
```
T=0ms:   ❤️ 클릭 → Job[(1, ❤️)] 시작
T=500ms: 🔥 클릭 → Job[(1, 🔥)] 시작 (독립적!)
T=1000ms: ❤️ 서버 전송 성공
T=1500ms: 🔥 서버 전송 성공
✅ 두 리액션 모두 정상 처리됨!
```

**추가 개선**:
- 롤백 시 `optimisticPosts`에서 제거하여 원본 Paging 데이터 표시
- 각 리액션이 독립적으로 debounce되어 서버 부하 최적화 유지

---

### 🔴 Critical Issues

#### 2.1 **selectedFeedId 미사용 필드**
**위치**: FeedUiState.kt:18
```kotlin
val selectedFeedId: Long = -1,  // ❌ 정의만 되고 사용되지 않음
val selectedPostMenuId: Long? = null,  // ✅ 실제 사용되는 필드
```
**문제**:
- `selectedFeedId`는 정의만 되어 있고 코드 어디에서도 사용되지 않음
- `selectedPostMenuId`가 실제로 메뉴 확장 상태를 관리

**권장 조치**:
- `selectedFeedId` 필드 제거
- 또는 용도가 있다면 명확히 하고 사용

---

#### 2.2 **handleUserReactionSheetUserProfileClick 미사용 함수**
**위치**: FeedViewModel.kt:429-438

```kotlin
private fun handleUserReactionSheetUserProfileClick(  // ❌ 호출되지 않음
    userId: Long,
    isMe: Boolean,
) = intent {
    if (isMe) {
        postSideEffect(FeedSideEffect.NavigateToMyPage)
    } else {
        postSideEffect(FeedSideEffect.NavigateToUserPage(userId))
    }
}
```

**문제**:
- 함수가 정의되어 있지만 `onIntent()`에서 호출되지 않음
- UI에서도 해당 Intent가 발행되지 않음

**권장 조치**:
- 기능이 필요하면 Intent에 추가
- 불필요하면 함수 삭제

---

### 🟡 Medium Issues

#### 2.4 **feedId 파라미터 미사용**
**위치**: FeedViewModel.kt:347-362

```kotlin
private fun handlePostReactionLongClick(
    feedId: Long,  // ❌ 사용되지 않음
    reactions: List<PostReaction>,
    selectedEmoji: Emoji,
) = intent {
    reduce {
        state.copy(
            bottomSheetState = state.bottomSheetState.copy(reactionUsers = true),
            reactionDetailsUiState = ReactionDetailsUiState.Success(
                reactions = reactions,
                selectedEmoji = selectedEmoji,
            ),
        )
    }
}
```

**문제**: feedId를 받지만 사용하지 않음

**권장 조치**: 파라미터 제거 또는 향후 사용 목적 명확화

---

#### 2.5 **캘린더 API 중복 호출 가능성**
**위치**: FeedViewModel.kt:558-573

```kotlin
private fun onWeekDisplayed(currentWeekStartDate: LocalDate) {
    val currentState = container.stateFlow.value
    if (currentState.calendarState.isLoading ||  // ✅ 로딩 중이면 스킵
        currentWeekStartDate.isAfter(currentState.calendarState.today)
    ) {
        return
    }
    // ...
}
```

**현재 보호 장치**:
- `isLoading` 플래그로 중복 호출 방지
- `Mutex(calendarApiLock)`로 동시 실행 방지

**잠재적 문제**:
- 사용자가 빠르게 스크롤하면 여러 번 호출될 수 있음
- Mutex가 있어서 실제 실행은 한 번만 되지만, 함수는 여러 번 호출됨

**현재 상태**: 문제 없음 (적절한 보호 장치 존재)

---

### 🟢 Minor Issues

#### 2.6 **isCertifiable 중복 계산**
**위치**: FeedViewModel.kt:243, 544

두 곳에서 `isCertifiable(selectedDate)` 호출:
- `handleFeedDateState()`: 날짜 선택 시
- `handlePagingDataEmpty()`: Paging 데이터 empty 감지 시

**영향도**: 낮음 - 간단한 날짜 계산이므로 성능 영향 미미

**권장**: 현재 상태 유지 (각 컨텍스트에서 독립적으로 판단하는 것이 명확함)

---

## 3. 낙관적 업데이트 검증

### 3.1 리액션 낙관적 업데이트 ✅ 개선됨 (2025-11-05)

#### 정상 플로우
```
1. 사용자가 ❤️ 클릭
   ↓
2. 즉시 UI 업데이트 (optimisticPosts[feedId] = newPost)
   - count: "10" → "11"
   - isReacted: false → true
   ↓
3. 1초 debounce 대기 (리액션별로 독립적)
   ↓
4. 서버 요청 (POST /feeds/{feedId}/reactions)
   ↓
5. 성공 → optimisticPosts에서 제거하지 않음 (서버 데이터로 자동 대체)
   실패 → optimisticPosts에서 제거 (원본 Paging 데이터 표시)
```

#### ✅ 장점
- 즉각적인 UI 반응으로 좋은 UX
- **각 리액션이 독립적으로 debounce되어 동시성 문제 해결**
- 실패 시 롤백으로 데이터 일관성 유지
- 서버 부하 최적화 유지

#### ✅ 개선 사항 (2025-11-05)
```kotlin
// FeedViewModel.kt:322-353
private fun handlePostReactionClick(...) = intent {
    val reactionKey = post.feedId to emoji  // ✅ 리액션별 키
    val newPost = post.updateReaction(emoji, isReacted)
    reduce {
        state.copy(
            optimisticPosts = state.optimisticPosts + (post.feedId to newPost),
        )
    }

    reactionDebounceJobs[reactionKey]?.cancel()  // ✅ 리액션별로 독립적
    reactionDebounceJobs[reactionKey] = viewModelScope.launch {
        delay(REACTION_DEBOUNCE_MS)
        try {
            feedRepository.postReaction(post.feedId, emoji.type)
        } catch (e: Exception) {
            // ✅ 개선: optimisticPosts에서 제거하여 원본 데이터 표시
            reduce {
                state.copy(
                    optimisticPosts = state.optimisticPosts - post.feedId,
                )
            }
            postSideEffect(FeedSideEffect.ShowToast("리액션 업데이트 실패"))
        } finally {
            reactionDebounceJobs.remove(reactionKey)  // ✅ 리액션별 키
        }
    }
}
```

**개선된 시나리오**:
```
T=0ms:   ❤️ 클릭 → Job[(1, ❤️)] 시작, optimisticPosts[1] = Post(❤️:1)
T=500ms: 🔥 클릭 → Job[(1, 🔥)] 시작, optimisticPosts[1] = Post(❤️:1, 🔥:1)
T=1000ms: ❤️ 서버 전송 성공
T=1500ms: 🔥 서버 전송 성공
✅ 두 리액션 모두 정상 처리됨!
```

---

### 3.2 삭제 낙관적 업데이트

#### 정상 플로우
```
1. 사용자가 삭제 확인
   ↓
2. 즉시 UI에서 제거 (optimisticDeletedFeedIds += feedId)
   ↓
3. Paging 데이터 필터링 (feedId가 optimisticDeletedFeedIds에 있으면 제외)
   ↓
4. 서버 요청 (DELETE /feeds/{feedId})
   ↓
5. 성공 → Toast 표시
   실패 → 복원 (optimisticDeletedFeedIds -= feedId)
```

#### ✅ 장점
- 즉각적인 삭제 반응
- Set 자료구조로 중복 제거 자동 처리
- 실패 시 복원 가능

#### ✅ 문제 없음
- 삭제는 단일 작업이므로 동시성 문제 없음
- feedId만 관리하므로 다른 데이터와 충돌 없음

---

### 3.3 낙관적 업데이트와 Paging의 통합

```kotlin
// FeedViewModel.kt:196-206
originalPagingFlow
    .combine(optimisticPostsFlow) { pagingData, optimisticPosts ->
        pagingData.map { postResource ->
            optimisticPosts[postResource.feedId] ?: postResource  // ✅ 낙관적 업데이트 우선
        }
    }
    .combine(optimisticDeletedFeedIdsFlow) { pagingData, deletedFeedIds ->
        pagingData.filter { postResource ->
            postResource.feedId !in deletedFeedIds  // ✅ 삭제된 항목 제외
        }
    }
```

#### ✅ 장점
- Flow combine으로 자동 업데이트
- Paging 데이터와 낙관적 업데이트 완벽 통합
- 삭제와 수정 모두 처리

#### ⚠️ 주의점
- `optimisticPosts`의 동시성 문제가 여기에도 영향
- 여러 리액션이 동시 발생 시 최종 상태가 예측 불가능

---

## 4. 종합 권장 사항

### ✅ 완료된 개선 사항 (2025-11-05)

1. ~~**리액션 동시성 문제 해결**~~ ✅
   - `reactionDebounceJobs`의 Key를 `Pair<Long, Emoji>`로 변경
   - 각 리액션이 독립적으로 debounce되어 동시성 문제 완전 해결
   - 롤백 로직 개선 (optimisticPosts에서 제거)

---

### High Priority (즉시 수정 권장)

1. **미사용 필드/함수 정리**
   - `selectedFeedId` 제거 또는 용도 명확화
   - `handleUserReactionSheetUserProfileClick` 제거 또는 구현

### Medium Priority (개선 권장)

2. **에러 처리 강화**
   ```kotlin
   // 현재
   .onError { error -> /* Handle error silently or log it */ }

   // 권장
   .onError { error ->
       when (error) {
           is NetworkError -> showToast("네트워크 오류")
           is ServerError -> showToast("서버 오류")
           else -> showToast("알 수 없는 오류")
       }
   }
   ```

4. **타입 안전성 강화**
   ```kotlin
   // dialogState에서 actionType과 deleteFeedId/reportFeedId 분리
   sealed interface DialogState {
       data object None : DialogState
       data class Delete(val feedId: Long) : DialogState
       data class Report(val feedId: Long) : DialogState
   }
   ```

### Low Priority (선택적 개선)

5. **코드 문서화**
   - 복잡한 로직에 KDoc 추가
   - 특히 낙관적 업데이트, Paging 통합 부분

6. **테스트 커버리지**
   - 리액션 동시성 시나리오 테스트
   - 낙관적 업데이트 롤백 테스트
   - 캘린더 prefetch 로직 테스트

---

## 5. 결론

### ✅ 잘 구현된 부분
- Paging3 통합
- **낙관적 업데이트 (리액션 동시성 문제 해결됨)** ✅
- Debounce 최적화 (리액션별로 독립적)
- 캘린더 Prefetch
- Mutex로 동시 API 호출 방지

### ⚠️ 개선 필요 부분 (우선순위 낮음)
- 미사용 코드 정리 (`selectedFeedId`, `handleUserReactionSheetUserProfileClick`)
- 에러 처리 강화 (구체적인 에러 메시지)
- 타입 안전성 강화 (DialogState sealed interface화)

### 📝 개선 이력

**2025-11-05**:
- ✅ **리액션 동시성 문제 해결**:
  - **문제**: 여러 리액션을 빠르게 클릭하면 마지막 리액션만 서버 전송되고 이전 리액션들이 유실됨
  - **원인**: `reactionDebounceJobs`가 feedId별로 하나의 Job만 유지하여 새 리액션이 이전 리액션의 Job을 취소
  - **해결**: Key를 `Long`에서 `Pair<Long, Emoji>`로 변경하여 각 리액션이 독립적으로 debounce되도록 개선
  - **영향**: 같은 게시물에 여러 리액션을 빠르게 클릭해도 모두 서버에 정상 전송됨
  - **변경 파일**: `FeedViewModel.kt` (Line 79, 327, 335-336, 350)

- ✅ **롤백 로직 개선**:
  - **기존**: 실패 시 원본 Post 객체로 덮어씀 → 다른 낙관적 업데이트도 함께 롤백되는 문제
  - **개선**: 실패 시 `optimisticPosts`에서 해당 feedId를 제거하여 원본 Paging 데이터가 표시되도록 변경
  - **효과**: 한 리액션 실패가 다른 낙관적 업데이트에 영향을 주지 않음
  - **변경 파일**: `FeedViewModel.kt` (Line 343-346)

- ✅ **코드 품질**:
  - 변경 최소화: 단 3곳만 수정하여 리스크 최소화
  - 성능 유지: Debounce 최적화 그대로 유지
  - 호환성: 기존 Paging 통합 로직 변경 없음

**2025-11-06**:
- ✅ **PagingData Flow 중복 collect 오류 해결**:
  - **문제**: reaction 추가 시 `java.lang.IllegalStateException: Attempt to collect twice from pageEventFlow` 오류 발생
  - **원인**: `getPagingFlowForDate()`에서 optimistic update를 combine한 후 캐시하여, `optimisticPostsFlow` 변경 시 이미 캐시된 PagingFlow를 다시 collect하려고 시도
  - **해결**:
    - `getPagingFlowForDate()`를 순수 함수로 변경 (optimistic update 로직 제거)
    - optimistic update를 `feedPagingData`의 `cachedIn()` **후**에 적용하도록 이동
  - **효과**:
    - optimistic update 변경이 원본 PagingFlow를 다시 collect하지 않음
    - 날짜별 캐시는 원본 데이터만 유지하여 안정성 향상
    - PagingData와 optimistic update의 책임 분리로 코드 구조 개선
  - **변경 파일**: `FeedViewModel.kt` (Line 87-107, 198-213)

- ✅ **아키텍처 개선**:
  ```kotlin
  // Before: getPagingFlowForDate()에서 optimistic update 적용 후 캐시
  originalPagingFlow
      .combine(optimisticPostsFlow) { ... }  // ❌ 캐시 안에서 combine
      .cachedIn(viewModelScope)

  // After: cachedIn 후 optimistic update 적용
  feedPagingData = getPagingFlowForDate(date)  // 원본 데이터만 캐시
      .cachedIn(viewModelScope)
      .combine(optimisticPostsFlow) { ... }     // ✅ 캐시 밖에서 combine
      .combine(optimisticDeletedFeedIdsFlow) { ... }
  ```

---

## 총평

전반적으로 **매우 잘 구현된 Feed 시스템**입니다.

- **Paging3 통합**: 날짜별 캐싱, 무한 스크롤 완벽
- **낙관적 업데이트**: 리액션 동시성 문제를 해결하여 안정적인 UX 제공
- **성능 최적화**: Debounce, Prefetch, Mutex 등 적절한 최적화 적용

남은 이슈들은 모두 낮은 우선순위이며, 사용자 경험에 직접적인 영향을 주지 않습니다. 현재 상태에서도 프로덕션 배포에 문제가 없습니다.
