# 화면 코드 생성 규칙

이 문서는 새로운 화면(Feature)을 생성할 때 따라야 할 규칙을 정의합니다. 일관된 코드 스타일과 아키텍처 유지를 위해 반드시 이 규칙을 숙지하고 준수해 주시기 바랍니다.

## 1. 아키텍처

- **MVI (Model-View-Intent)** 아키텍처 패턴을 사용합니다.
- **Orbit MVI** 프레임워크를 사용하여 구현합니다.

## 2. 패키지 구조

기능별로 패키지를 구성하며, 각 패키지는 아래와 같은 구조를 가집니다.

```
com.dpm.sixpack.presentation.routes.[feature_name]
├── ui
│   └── screen
│       └── [FeatureName]Screen.kt
├── contract
│   ├── [FeatureName]State.kt
│   ├── [FeatureName]Intent.kt
│   └── [FeatureName]SideEffect.kt
├── navigation
│   └── [FeatureName]Navigation.kt
├── [FeatureName]Route.kt
└── [FeatureName]ViewModel.kt
```

## 3. 네이밍 및 구현 컨벤션

### **[FeatureName]Route.kt**
- **역할**: 화면의 진입점(Entry Point) 역할을 하는 Composable 함수입니다.
- **구현**:
    - `hiltViewModel()`을 통해 ViewModel을 주입받습니다.
    - `viewModel.collectAsState()`로 State를 구독하여 `Screen`에 전달합니다.
    - `viewModel.collectSideEffect()`로 SideEffect를 구독하여 토스트 메시지 표시, 화면 이동 등의 1회성 이벤트를 처리합니다.
    - 화면 이동은 `onNavigateTo...`와 같은 람다 함수를 파라미터로 받아 `SideEffect` 처리 블록 내에서 호출하는 방식으로 수행합니다.

### **[FeatureName]ViewModel.kt**
- **역할**: 비즈니스 로직을 처리하고 화면의 상태(State)를 관리합니다.
- **구현**:
    - `com.dpm.sixpack.presentation.common.base.BaseViewModel`을 상속받아 구현합니다.
    - `onIntent` 함수 내의 `when` 문에서 각 Intent 케이스마다 `handle{인텐트이름}` 형태의 `private fun`을 호출하여 로직을 위임합니다.

### **[FeatureName]Screen.kt**
- **역할**: 실제 UI를 그리는 **Stateless** Composable 함수입니다.
- **구현**:
    - 파라미터로 `state`와 `onIntent: (Intent) -> Unit` 람다를 받습니다.
    - UI는 오직 전달받은 `state`에 기반하여 그려져야 합니다.
    - 버튼 클릭 등 모든 사용자 상호작용은 `onIntent` 람다를 통해 ViewModel로 전달되어야 합니다.
    - Composable 함수와 주요 UI 컴포넌트는 `@Preview` 어노테이션을 사용하여 미리보기를 제공해야 합니다. (필요시 `DoRunPreviewWrapper` 사용)

### **Contract**
- **[FeatureName]State.kt**:
    - 화면의 상태를 정의하는 `data class` 입니다.
    - `com.dpm.sixpack.presentation.common.base.UiState` 인터페이스를 구현합니다.
- **[FeatureName]Intent.kt**:
    - 사용자 입력(Intent)을 정의하는 `sealed interface` 입니다.
    - `com.dpm.sixpack.presentation.common.base.UiIntent` 인터페이스를 구현합니다.
- **[FeatureName]SideEffect.kt**:
    - 부수 효과(SideEffect)를 정의하는 `sealed interface` 입니다.
    - `com.dpm.sixpack.presentation.common.base.UiSideEffect` 인터페이스를 구현합니다.

### **[FeatureName]Navigation.kt**
- **역할**: Jetpack Navigation Component를 사용하여 화면으로 이동하는 경로와 방법을 정의합니다.
- **구현**: `NavGraphBuilder`의 확장 함수 형태로 작성하며, `composable` 함수를 사용하여 `[FeatureName]Route`를 NavGraph에 등록합니다.

## 4. 코드 스타일

- **Jetpack Compose**: UI는 Jetpack Compose를 사용하여 선언적으로 작성합니다.
- **Hilt**: `@HiltViewModel`을 사용하여 ViewModel의 의존성을 주입합니다.
- **공용 컴포넌트**: UI 구현 시, `com.dpm.sixpack.presentation.common.components` 패키지에 정의된 공용 컴포넌트를 최대한 재사용합니다.
- **테마**: UI 요소의 색상, 타이포그래피, 모양(Shape)은 `SixpackTheme`에서 정의된 값을 사용해야 합니다. (예: `SixpackTheme.colors.blue600`, `SixpackTheme.typography.b1Bold`, `SixpackTheme.shapes.round12`)

## 5. 예시

아래는 가상의 `Profile` 화면을 생성하는 예시입니다.

**ProfileRoute.kt**
```kotlin
@Composable
fun ProfileRoute(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToBack: () -> Unit,
) {
    val screenState by viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ProfileSideEffect.NavigateToBack -> onNavigateToBack()
        }
    }

    ProfileScreen(
        state = screenState,
        onIntent = viewModel::onIntent
    )
}
```

**ProfileViewModel.kt**
```kotlin
@HiltViewModel
class ProfileViewModel @Inject constructor(
    // ...
) : BaseViewModel<ProfileScreenState, ProfileIntent, ProfileSideEffect>() {

    override fun onIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.OnBackButtonClick -> handleOnBackButtonClick()
        }
    }

    private fun handleOnBackButtonClick() = intent {
        postSideEffect(ProfileSideEffect.NavigateToBack)
    }
}
```

**ProfileScreen.kt**
```kotlin
@Composable
fun ProfileScreen(
    state: ProfileScreenState,
    onIntent: (ProfileIntent) -> Unit,
) {
    // UI 구성 (state 기반)
    // Button(onClick = { onIntent(ProfileIntent.OnBackButtonClick) }) { ... }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    DoRunPreviewWrapper {
        ProfileScreen(
            state = ProfileScreenState(/* ... */),
            onIntent = {},
        )
    }
}
```

**ProfileNavigation.kt**
```kotlin
fun NavGraphBuilder.profileScreen(
    onNavigateToBack: () -> Unit,
) {
    composable(route = "profile") {
        ProfileRoute(onNavigateToBack = onNavigateToBack)
    }
}
```
