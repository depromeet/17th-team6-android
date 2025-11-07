PostUploadScreen은 사용자가 달리기 기록을 마친 직후, 해당 기록을 기반으로 인증 피드를 생성하고 서버에 업로드하는 화면입니다.

## ✅ Implementation Status: COMPLETED

모든 요구사항이 구현되었습니다.

### 구현된 파일들:

#### Data Layer:
- `CreateSelfieRequestDto.kt` - API 요청 DTO
- `FeedService.kt` - createSelfie API 메서드 추가
- `FeedDataSource.kt` - createSelfie 메서드 추가
- `FeedRepositoryImpl.kt` - createSelfie 구현

#### Domain Layer:
- `FeedRepository.kt` - createSelfie 인터페이스 추가

#### Presentation Layer:
- `PostUpload` route - FeedRoute.kt에 추가
- `PostUploadIntent.kt` - 사용자 액션 정의
- `PostUploadUiState.kt` - 화면 상태 정의
- `PostUploadSideEffect.kt` - 부수 효과 정의
- `PostUploadViewModel.kt` - 상태 관리 및 비즈니스 로직
- `PostUploadScreen.kt` - UI 컴포저블 (PostEditScreen 90%+ 재사용)
- `PostUploadRoute.kt` - 라우트 컴포저블 및 권한/이미지 피커 처리
- `strings.xml` - feed_post_upload_submit_button 문자열 추가

### 주요 특징:
- ✅ UI는 PostEditScreen과 90% 이상 재사용
- ✅ sessionId와 runningSummary를 navigation arguments로 전달받음
- ✅ POST /api/selfie/feeds API 호출 구현
- ✅ 이미지 선택 기능 (선택적)
- ✅ 권한 처리 (이미지 선택)
- ✅ Multipart/form-data로 selfieImage와 data(JSON) 전송
- ✅ Orbit MVI 패턴 적용
- ✅ 에러 핸들링 및 사용자 피드백

1. 핵심 요구사항 (Core Requirements)
   UI 재사용: 화면의 전체적인 레이아웃 및 UI 구성요소(텍스트 입력란, 이미지 선택 영역 등)는 기존 PostEditScreen과 동일하거나 90% 이상 재사용합니다.

데이터 소스: PostEditScreen과 달리, 이 화면은 "기록 화면(RecordScreen)"으로부터 필수 데이터를 **내비게이션 인자(Navigation Arguments)**로 전달받아야 합니다.

주요 액션: 사용자가 '업로드' 버튼을 클릭하면, 전달받은 데이터와 화면에서 수정한 데이터를 조합하여 POST /api/selfie/feeds API를 호출합니다.

2. 데이터 흐름 (Data Flow)
   Input (From RecordScreen -> PostUploadScreen):

sessionId (Type: String 또는 Long)

용도: 서버 API(POST /api/selfie/feeds)의 data 파트(JSON)에 포함될 필수 식별자입니다. UI에 직접 표시되지 않을 수 있습니다.

runningSummary (Type: RunningSummary Data Class)

용도: 화면의 초기 UI 상태를 구성(pre-populate)하는 데 사용됩니다.

예시: runningSummary.distance (거리), runningSummary.time (시간), runningSummary.mapImageUrl (지도 이미지) 등을 UI에 즉시 표시합니다.

Internal State (Managed by PostUploadViewModel):

uiState.title (Type: String): 사용자가 입력/수정하는 피드 제목 (또는 내용).

uiState.selectedSelfieUri (Type: Uri?): 사용자가 갤러리에서 선택한 (선택적) 셀피 이미지의 URI.

uiState.mapImage (Type: String): runningSummary에서 받은 지도 이미지. (셀피가 없으면 이걸 보여줘야 함)

3. 주요 기능 및 API 연동 (Actions)
   초기화 (On-Entry):

ViewModel은 내비게이션 인자로 받은 sessionId와 runningSummary를 저장합니다.

runningSummary의 데이터를 uiState에 반영하여 UI를 갱신합니다. (예: 지도 이미지 표시, 달리기 통계 표시)

사용자 상호작용 (Identical to PostEditScreen):

텍스트 입력란 수정 기능.

셀피 이미지 선택/변경 기능 (선택사항).

최종 액션 (Primary Action: '업로드' 버튼 클릭):

Trigger: 사용자가 '업로드' 버튼 클릭.

API: POST /api/selfie/feeds

Content-Type: multipart/form-data

Request Body 구성:

data (Part 1, application/json):

CreateSelfieRequest DTO를 생성합니다.

이 DTO는 sessionId (Input), uiState.title (Internal State) 및 runningSummary의 기타 필요 데이터를 포함하여 JSON 문자열로 변환됩니다.

selfieImage (Part 2, image/*):

uiState.selectedSelfieUri가 null이 아닌 경우에만 이 파트를 추가합니다.

이미지는 이전에 정의한 ContentUriRequestBody를 사용하여 리사이징/압축된 바이너리 데이터로 전송됩니다.

null인 경우, selfieImage 파트 자체를 전송하지 않습니다.
