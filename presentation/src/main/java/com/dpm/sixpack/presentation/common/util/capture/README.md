# Compose View Capture Utility

Jetpack Compose 뷰를 Bitmap으로 캡처하고 갤러리에 저장하는 재사용 가능한 유틸리티입니다.

## 📦 구성 요소

```
presentation/common/util/capture/
├── CaptureController.kt      # 캡처 로직 관리 및 제어
├── CapturableModifier.kt     # Modifier.capturable() extension
├── ImageSaver.kt              # Bitmap을 갤러리에 저장
└── README.md                  # 사용 가이드 (현재 파일)
```

## ✨ 주요 특징

### 🚀 **성능 최적화**
- **GraphicsLayer API** 사용으로 하드웨어 가속 활용
- **Suspend 함수**로 메인 스레드 블로킹 방지
- Recomposition에 안전한 구조

### ♻️ **재사용성**
- 어떤 Composable에든 `Modifier.capturable()` 적용 가능
- PostEdit, Feed, RunningRecord 등 모든 화면에서 사용 가능
- Modifier chain으로 기존 코드와 쉽게 통합

### 🛡️ **안정성**
- Android 버전별 최적화 (API 29+ Scoped Storage, 이하 File API)
- 에러 핸들링 내장 (`Result<T>` 반환)
- 권한 자동 처리 (Android 10+는 권한 불필요)

## 🎯 빠른 시작

### 1. 기본 사용법

```kotlin
@Composable
fun MyScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // 1. CaptureController 생성
    val captureController = rememberCaptureController()

    Column {
        // 2. 캡처할 Composable에 modifier 적용
        PostImageWithRecord(
            postImageUrl = imageUrl,
            runningSummary = runningSummary,
            modifier = Modifier
                .fillMaxWidth()
                .capturable(captureController)  // 🎯 캡처 가능하게 설정
        )

        // 3. 버튼 클릭 시 캡처 및 저장
        Button(
            onClick = {
                coroutineScope.launch {
                    // 캡처 실행
                    val bitmap = captureController.captureAsync()

                    bitmap?.let {
                        // 갤러리에 저장
                        ImageSaver.saveToGallery(
                            context = context,
                            bitmap = it,
                            fileName = "sixpack_${System.currentTimeMillis()}"
                        ).onSuccess { uri ->
                            Toast.makeText(context, "저장 완료!", Toast.LENGTH_SHORT).show()
                        }.onFailure { exception ->
                            Toast.makeText(context, "저장 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        ) {
            Text("이미지 저장")
        }
    }
}
```

### 2. PostEditScreen 예제 (실제 사용 사례)

```kotlin
@Composable
fun PostEditRoute(
    viewModel: PostEditViewModel = hiltViewModel(),
    navigateBack: () -> Unit,
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // CaptureController 생성 (remember로 상태 유지)
    val captureController = rememberCaptureController()

    PostEditScreen(
        state = state,
        captureController = captureController,
        onSaveIconClick = {
            coroutineScope.launch {
                val bitmap = captureController.captureAsync()

                if (bitmap != null) {
                    val fileName = "sixpack_${System.currentTimeMillis()}"
                    ImageSaver.saveToGallery(context, bitmap, fileName)
                        .onSuccess { uri ->
                            Toast.makeText(
                                context,
                                "이미지가 갤러리에 저장되었습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .onFailure { exception ->
                            Toast.makeText(
                                context,
                                "저장 실패: ${exception.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    Toast.makeText(context, "캡처 실패", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )
}

@Composable
fun PostEditScreen(
    state: PostEditUiState,
    captureController: CaptureController,
    onSaveIconClick: () -> Unit,
) {
    Column {
        // ImageComponent에 captureController 전달
        EditablePostImage(
            postImageUrl = imageUrl,
            runningSummary = state.originalPost.runningInfo,
            captureController = captureController,  // 🎯 전달
            modifier = Modifier.fillMaxWidth()
        )
    }
}
```

## 📚 API 상세 설명

### CaptureController

Composable View의 캡처를 제어하는 컨트롤러입니다.

```kotlin
class CaptureController {
    /**
     * 비동기로 Composable을 Bitmap으로 캡처합니다.
     *
     * @return Bitmap? - 캡처된 Bitmap (실패 시 null)
     */
    suspend fun captureAsync(): Bitmap?
}

/**
 * CaptureController를 생성합니다.
 * Recomposition에 안전하도록 remember로 관리됩니다.
 */
@Composable
fun rememberCaptureController(): CaptureController
```

**사용 예:**
```kotlin
val controller = rememberCaptureController()
val bitmap = controller.captureAsync()  // suspend 함수
```

---

### Modifier.capturable()

Composable을 캡처 가능하게 만드는 Modifier extension입니다.

```kotlin
fun Modifier.capturable(controller: CaptureController): Modifier
```

**특징:**
- GraphicsLayer를 사용하여 하드웨어 가속
- 기존 Modifier와 체이닝 가능
- Optional 파라미터로 설계 가능 (`captureController: CaptureController? = null`)

**사용 예:**
```kotlin
Box(
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .capturable(captureController)  // 🎯 여기에 적용
)
```

---

### ImageSaver

Bitmap을 갤러리에 저장하는 유틸리티입니다.

```kotlin
object ImageSaver {
    /**
     * Bitmap을 갤러리에 저장합니다.
     *
     * @param context Android Context
     * @param bitmap 저장할 Bitmap
     * @param fileName 파일 이름 (확장자 제외)
     * @return Result<Uri> - 성공 시 Uri, 실패 시 예외
     */
    suspend fun saveToGallery(
        context: Context,
        bitmap: Bitmap,
        fileName: String
    ): Result<Uri>

    /**
     * 저장 가능 여부를 확인합니다.
     *
     * Android 10+ 에서는 항상 true 반환
     * Android 10 미만에서는 WRITE_EXTERNAL_STORAGE 권한 체크
     */
    fun canSaveToGallery(context: Context): Boolean
}
```

**저장 위치:**
- **Android 10+**: `Pictures/Sixpack/`
- **Android 10 미만**: `Pictures/Sixpack/`

**권한:**
- **Android 10+**: 권한 불필요 (Scoped Storage)
- **Android 10 미만**: `WRITE_EXTERNAL_STORAGE` 필요

---

## 🔧 다른 화면에 적용하기

### Feed 화면에 적용

```kotlin
@Composable
fun FeedPostCard(
    post: PostResource,
    onSaveClick: (Bitmap) -> Unit
) {
    val captureController = rememberCaptureController()

    Column {
        PostImageWithRecord(
            postImageUrl = post.postImageUrl,
            runningSummary = post.runningInfo,
            captureController = captureController,
            modifier = Modifier.fillMaxWidth()
        )

        IconButton(
            onClick = {
                scope.launch {
                    captureController.captureAsync()?.let { onSaveClick(it) }
                }
            }
        ) {
            Icon(Icons.Default.Download, "저장")
        }
    }
}
```

### Running Record 화면에 적용

```kotlin
@Composable
fun RunningRecordScreen() {
    val captureController = rememberCaptureController()

    Box(
        modifier = Modifier.capturable(captureController)
    ) {
        // 러닝 기록 UI
        RunningStatistics(...)
    }
}
```

## ⚙️ 권한 설정

`AndroidManifest.xml`에 다음 권한을 추가하세요:

```xml
<!-- Android 10 미만에서만 필요 -->
<uses-permission
    android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    android:maxSdkVersion="28" />
```

**참고:** Android 10 (API 29) 이상에서는 Scoped Storage를 사용하므로 권한이 필요하지 않습니다.

## 🐛 에러 핸들링

```kotlin
ImageSaver.saveToGallery(context, bitmap, fileName)
    .onSuccess { uri ->
        // 성공 처리
        Log.d("ImageSaver", "Saved to: $uri")
    }
    .onFailure { exception ->
        // 실패 처리
        when (exception) {
            is IOException -> {
                // IO 에러
            }
            is SecurityException -> {
                // 권한 에러
            }
            else -> {
                // 기타 에러
            }
        }
    }
```

## 🎨 성능 고려사항

### 1. **메모리 관리**
```kotlin
// ✅ Good - bitmap 사용 후 자동으로 GC
val bitmap = captureController.captureAsync()
bitmap?.let { ImageSaver.saveToGallery(context, it, fileName) }

// ❌ Bad - 메모리 누수 가능
val bitmaps = mutableListOf<Bitmap>()
repeat(100) {
    bitmaps.add(captureController.captureAsync()!!)  // 메모리 누수!
}
```

### 2. **코루틴 스코프**
```kotlin
// ✅ Good - 적절한 스코프 사용
val scope = rememberCoroutineScope()
Button(onClick = {
    scope.launch { captureController.captureAsync() }
})

// ❌ Bad - GlobalScope 사용 금지
GlobalScope.launch {  // 화면 종료 후에도 실행됨
    captureController.captureAsync()
}
```

### 3. **UI 스레드 블로킹 방지**
```kotlin
// ✅ Good - suspend 함수 사용
coroutineScope.launch {
    val bitmap = captureController.captureAsync()  // 비동기
}

// ❌ Bad - runBlocking은 UI 스레드를 블로킹함
runBlocking {
    val bitmap = captureController.captureAsync()
}
```

## 📝 TODO 및 개선 사항

- [ ] 캡처 품질 설정 옵션 추가
- [ ] 다양한 이미지 포맷 지원 (PNG, WEBP)
- [ ] 캡처 진행 상태 Flow 제공
- [ ] 캡처 실패 시 재시도 로직

## 📄 라이선스

이 코드는 Sixpack 프로젝트의 일부입니다.
