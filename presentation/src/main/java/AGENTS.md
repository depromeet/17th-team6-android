# Presentation Module - AI Agent Guidelines

이 문서는 AI 에이전트(LLM)가 SixPack Android 애플리케이션의 프레젠테이션 레이어 코드를 작성할 때 따라야 할 가이드라인을 제공합니다.

## 개요 (Overview)

프레젠테이션 모듈은 **Jetpack Compose**와 **Orbit 라이브러리를 사용한 MVI 패턴**으로 UI 레이어를 구현합니다. 사용자 상호작용을 처리하고, 데이터를 표시하며, 화면 간 네비게이션을 관리합니다.

**Base Package**: `com.dpm.sixpack.presentation`

## 목차

1. [아키텍처](#1-아키텍처-architecture)
2. [패키지 구조](#2-패키지-구조-package-structure)
3. [핵심 패턴](#3-핵심-패턴-core-patterns)
4. [공통 UI 컴포넌트](#4-공통-ui-컴포넌트-common-ui-components)
5. [Theme 시스템](#5-theme-시스템-theme-system)
6. [Utility Functions](#6-utility-functions)
7. [String Resources](#7-string-resources-문자열-리소스)
8. [Dialog 상태 관리](#8-dialog-상태-관리)
9. [Error Handling Pattern](#9-error-handling-pattern-에러-처리-패턴)
10. [Loading State Pattern](#10-loading-state-pattern-로딩-상태-패턴)
11. [Common Patterns](#11-common-patterns-자주-사용되는-패턴)
12. [Preview Pattern](#12-preview-pattern-프리뷰-패턴)
13. [흔한 실수 방지](#13-흔한-실수-방지-common-mistakes-to-avoid)
14. [File Naming Conventions](#14-file-naming-conventions-파일-명명-규칙)
15. [Testing Considerations](#15-testing-considerations-테스트-고려사항)
16. [Summary Checklist](#16-summary-checklist-요약-체크리스트)
17. [예시](#17-예시-examples)

---

## 1. 아키텍처 (Architecture)

- **MVI (Model-View-Intent)** 아키텍처 패턴을 사용합니다.
- **Orbit MVI** 프레임워크를 사용하여 구현합니다.
- **단방향 데이터 플로우**: Intent → ViewModel → State → UI → Intent

---

## 2. 패키지 구조 (Package Structure)

기능별로 패키지를 구성하며, 각 패키지는 아래와 같은 구조를 가집니다.

```
presentation/
├── common/                        # 공유 컴포넌트 및 유틸리티
│   ├── base/                      # Base 클래스 (BaseViewModel, UiState 등)
│   ├── components/                # 재사용 가능한 UI 컴포넌트
│   │   ├── auth/                  # 인증 관련 컴포넌트
│   │   ├── bottomsheet/           # Bottom sheets
│   │   ├── dialog/                # Dialogs
│   │   ├── image/                 # 이미지 컴포넌트
│   │   ├── post/                  # 포스트/피드 컴포넌트
│   │   ├── textfield/             # 텍스트 입력 컴포넌트
│   │   └── topbar/                # Top app bars
│   ├── model/                     # UI 모델
│   └── util/                      # 유틸리티 및 확장 함수
├── destinations/                  # Type-safe navigation routes
├── navigation/                    # Bottom navigation 및 tabs
├── routes/                        # 기능별 화면
│   └── [feature]/
│       ├── [Feature]Route.kt      # Route composable (진입점)
│       ├── [Feature]ViewModel.kt  # ViewModel with Orbit MVI
│       ├── contract/              # MVI contracts
│       │   ├── [Feature]State.kt
│       │   ├── [Feature]Intent.kt
│       │   └── [Feature]SideEffect.kt
│       ├── navigation/            # 기능별 네비게이션 로직
│       └── ui/                    # UI 컴포넌트 및 화면
│           ├── screen/
│           └── component/
└── theme/                         # Material theme 구성
```

---

## 3. 핵심 패턴 (Core Patterns)

### 3.1 MVI 패턴과 Orbit

**ALWAYS** 모든 화면에 Orbit MVI 패턴을 사용하세요. 패턴은 다음으로 구성됩니다:

#### BaseViewModel 구조

```kotlin
abstract class BaseViewModel<UI_STATE : UiState, UI_INTENT : UiIntent, SIDE_EFFECT : SideEffect> :
    ViewModel(),
    ContainerHost<UI_STATE, SIDE_EFFECT> {
    abstract val initialState: UI_STATE
    abstract override val container: Container<UI_STATE, SIDE_EFFECT>

    // UI에서 호출하는 단일 진입점
    abstract fun onIntent(intent: UI_INTENT)
}
```

#### Contract 파일

**ALWAYS** `contract/` 패키지에 세 개의 contract 파일을 생성하세요:

**1. State (`[Feature]State.kt`)**:
```kotlin
@Parcelize
data class FeatureState(
    val isLoading: Boolean = false,
    val data: DataType? = null,
    val errorMessage: String? = null,
    // computed properties 추가 가능
) : UiState {
    val isDataValid: Boolean
        get() = data != null && errorMessage == null
}
```

**2. Intent (`[Feature]Intent.kt`)**:
```kotlin
sealed interface FeatureIntent : UiIntent {
    data class OnDataChanged(val newData: String) : FeatureIntent
    data object OnButtonClick : FeatureIntent
    data object OnBackButtonClick : FeatureIntent
}
```

**3. SideEffect (`[Feature]SideEffect.kt`)**:
```kotlin
sealed interface FeatureSideEffect : SideEffect {
    data object NavigateToNextScreen : FeatureSideEffect
    data object NavigateBack : FeatureSideEffect
    data object ShowSuccessMessage : FeatureSideEffect
    data object ShowErrorMessage : FeatureSideEffect
}
```

#### ViewModel 구현

```kotlin
@HiltViewModel
class FeatureViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val someUseCase: SomeUseCase,
) : BaseViewModel<FeatureState, FeatureIntent, FeatureSideEffect>() {

    override val initialState: FeatureState = FeatureState()

    override val container: Container<FeatureState, FeatureSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    override fun onIntent(intent: FeatureIntent) {
        when (intent) {
            is FeatureIntent.OnDataChanged -> handleDataChanged(intent.newData)
            is FeatureIntent.OnButtonClick -> handleButtonClick()
            is FeatureIntent.OnBackButtonClick -> handleBackButton()
        }
    }

    private fun handleDataChanged(newData: String) = intent {
        reduce {
            state.copy(
                data = newData,
                errorMessage = null,
            )
        }
    }

    private fun handleButtonClick() = intent {
        reduce { state.copy(isLoading = true) }

        someUseCase(state.data)
            .onSuccess { result ->
                reduce { state.copy(isLoading = false) }
                postSideEffect(FeatureSideEffect.NavigateToNextScreen)
            }
            .onError { exception ->
                reduce {
                    state.copy(
                        isLoading = false,
                        errorMessage = exception.message,
                    )
                }
                postSideEffect(FeatureSideEffect.ShowErrorMessage)
            }
    }

    private fun handleBackButton() = intent {
        postSideEffect(FeatureSideEffect.NavigateBack)
    }
}
```

**핵심 원칙**:
- `intent { }` 블록 내에서 모든 상태 변경 수행
- `reduce { }`로 상태를 불변하게 업데이트
- `postSideEffect()`로 일회성 이벤트 (네비게이션, 토스트 등) 처리
- `onIntent()`에서 패턴 매칭으로 모든 Intent 타입 처리
- 모든 intent handler는 `private` 함수
- 프로세스 종료 후 복원을 위해 container에 `savedStateHandle` 사용

### 3.2 화면 구조 패턴

#### Route Composable (진입점)

**파일**: `routes/[feature]/[Feature]Route.kt`

```kotlin
@Composable
fun FeatureRoute(
    viewModel: FeatureViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToNext: () -> Unit,
) {
    val state by viewModel.container.stateFlow.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.container.sideEffectFlow.collect { sideEffect ->
            when (sideEffect) {
                FeatureSideEffect.NavigateBack -> onNavigateBack()
                FeatureSideEffect.NavigateToNextScreen -> onNavigateToNext()
                FeatureSideEffect.ShowSuccessMessage -> {
                    // 토스트 또는 스낵바 표시
                }
            }
        }
    }

    FeatureScreen(
        state = state,
        onIntent = viewModel::onIntent,
    )
}
```

**핵심 원칙**:
- Route는 진입점이며 네비게이션을 처리
- `hiltViewModel()`로 ViewModel 주입
- `collectAsState()`로 State 구독
- `LaunchedEffect(Unit)`에서 Side effects 수집
- 네비게이션 콜백을 파라미터로 전달
- State와 `onIntent`를 Screen composable에 전달

#### Screen Composable

**파일**: `routes/[feature]/ui/screen/[Feature]Screen.kt`

```kotlin
@Composable
internal fun FeatureScreen(
    state: FeatureState,
    onIntent: (FeatureIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            DoRunNavigationTopBar(
                navigateToBack = { onIntent(FeatureIntent.OnBackButtonClick) },
                titleContent = {
                    Text(
                        text = stringResource(R.string.feature_title),
                        style = SixpackTheme.typography.t1Bold,
                        color = SixpackTheme.colors.gray900,
                    )
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            // 화면 콘텐츠
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else {
                FeatureContent(
                    data = state.data,
                    onDataChange = { newData ->
                        onIntent(FeatureIntent.OnDataChanged(newData))
                    },
                )
            }

            DoRunDefaultButton(
                text = stringResource(R.string.feature_button_text),
                onClick = { onIntent(FeatureIntent.OnButtonClick) },
                enabled = state.isDataValid,
            )
        }
    }
}

@Preview
@Composable
private fun FeatureScreenPreview() {
    DoRunPreviewWrapper {
        FeatureScreen(
            state = FeatureState(),
            onIntent = {},
        )
    }
}
```

**핵심 원칙**:
- Screen은 stateless UI 컴포넌트
- Screen composable은 `internal` 가시성 사용
- `state`와 `onIntent`를 파라미터로 받음
- 화면 구조를 위해 `Scaffold` 사용
- `DoRunPreviewWrapper`로 프리뷰 제공
- 모든 텍스트에 `stringResource()` 사용

### 3.3 Navigation 시스템 (Type-Safe Navigation)

**IMPORTANT**: SixPack은 Jetpack Navigation의 Type-Safe Navigation을 사용합니다. 모든 Route는 `@Serializable`을 사용하여 정의해야 합니다.

#### Route 정의 (destinations 패키지)

모든 Route는 `com.dpm.sixpack.presentation.destinations` 패키지에 정의합니다.

**기본 Route 인터페이스:**
```kotlin
// destinations/Route.kt
@Serializable
sealed interface Route
```

**화면별 Route 정의:**

**1. 단일 화면 Route (data object 사용):**
```kotlin
// destinations/OnboardingRoute.kt
@Serializable
data object OnboardingRoute : Route

// destinations/SessionReportRoute.kt
@Serializable
data object SessionReportRoute : Route
```

**2. 탭 그룹 Route (sealed interface 사용):**
```kotlin
// destinations/MainRoute.kt
@Serializable
sealed interface MainRoute : Route {
    @Serializable
    data object Running : MainRoute

    @Serializable
    data object Feed : MainRoute

    @Serializable
    data object MyPage : MainRoute
}
```

**3. 파라미터가 있는 Route (data class 사용):**
```kotlin
// 예시: SessionReport에 파라미터가 필요한 경우
@Serializable
data class SessionReportRoute(
    val sessionId: Long,
) : Route
```

#### Navigation 함수 정의 (navigation 패키지)

각 화면별로 `navigation` 패키지에 Navigation 함수를 정의합니다.

**파일 위치:**
```
routes/[feature_name]/navigation/[FeatureName]Navigation.kt
```

**필수 구현 요소:**

**1. NavController 확장 함수 (navigate 함수):**
```kotlin
fun NavController.navigate[FeatureName](navOptions: NavOptions? = null) {
    navigate([FeatureName]Route, navOptions)
}

// 파라미터가 있는 경우
fun NavController.navigate[FeatureName](
    sessionId: Long,
    navOptions: NavOptions? = null,
) {
    navigate([FeatureName]Route(sessionId = sessionId), navOptions)
}
```

**2. NavGraphBuilder 확장 함수 (NavGraph 추가):**
```kotlin
fun NavGraphBuilder.add[FeatureName]NavGraph(
    onNavigateToBack: () -> Unit,
    // 필요한 navigation 콜백들 추가
) {
    composable<[FeatureName]Route> {
        [FeatureName]Route(
            onNavigateToBack = onNavigateToBack,
            // 필요한 navigation 콜백들 전달
        )
    }
}

// 파라미터가 있는 경우 - backStackEntry에서 추출
fun NavGraphBuilder.add[FeatureName]NavGraph(
    onNavigateToBack: () -> Unit,
) {
    composable<[FeatureName]Route> { backStackEntry ->
        val route = backStackEntry.toRoute<[FeatureName]Route>()
        [FeatureName]Route(
            sessionId = route.sessionId,
            onNavigateToBack = onNavigateToBack,
        )
    }
}
```

#### Navigation 사용 예시

**완전한 예시: SessionReport 화면**

**1. Route 정의:**
```kotlin
// destinations/SessionReportRoute.kt
package com.dpm.sixpack.presentation.destinations

import kotlinx.serialization.Serializable

@Serializable
data object SessionReportRoute : Route
```

**2. Navigation 함수 정의:**
```kotlin
// routes/sessionreport/navigation/SessionReportNavigation.kt
package com.dpm.sixpack.presentation.routes.sessionreport.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.SessionReportRoute
import com.dpm.sixpack.presentation.routes.sessionreport.SessionReportRoute

fun NavController.navigateSessionReport(navOptions: NavOptions? = null) {
    navigate(SessionReportRoute, navOptions)
}

fun NavGraphBuilder.addSessionReportNavGraph(onNavigateToBack: () -> Unit) {
    composable<SessionReportRoute> {
        SessionReportRoute(
            navigateBack = onNavigateToBack,
        )
    }
}
```

**3. MainNavHost에 추가:**
```kotlin
// app/src/main/java/com/dpm/sixpack/main/navigation/MainNavHost.kt
NavHost(
    navController = navigator.navController,
    startDestination = navigator.startDestination,
) {
    // ... 다른 화면들

    addSessionReportNavGraph(
        onNavigateToBack = navigator::popBackStack,
    )
}
```

**4. MainNavigator에 네비게이션 함수 추가:**
```kotlin
// app/src/main/java/com/dpm/sixpack/main/navigation/MainNavigator.kt
class MainNavigator(...) {
    // ...

    fun navigateToSessionReport() {
        navController.navigateSessionReport(
            navOptions {
                popUpTo(MainRoute.Running) {
                    inclusive = false
                }
            }
        )
    }
}
```

**5. 화면에서 호출:**
```kotlin
// Route에서 SideEffect 처리
viewModel.collectSideEffect { sideEffect ->
    when (sideEffect) {
        is MySideEffect.NavigateToSessionReport ->
            onNavigateToSessionReport()
    }
}
```

#### Navigation 체크리스트

새로운 화면의 Navigation을 구현할 때 확인하세요:

**Route 정의:**
- [ ] `destinations` 패키지에 Route 정의
- [ ] `@Serializable` 어노테이션 추가
- [ ] `Route` 인터페이스 상속
- [ ] 파라미터가 필요하면 `data class`, 아니면 `data object` 사용

**Navigation 함수:**
- [ ] `routes/[feature]/navigation/[Feature]Navigation.kt` 파일 생성
- [ ] `NavController.navigate[Feature]` 확장 함수 정의
- [ ] `NavGraphBuilder.add[Feature]NavGraph` 확장 함수 정의
- [ ] `composable<[Feature]Route>` 사용하여 type-safe navigation 구현

**통합:**
- [ ] `MainNavHost`에 `add[Feature]NavGraph` 호출 추가
- [ ] 필요시 `MainNavigator`에 `navigateTo[Feature]` 함수 추가
- [ ] Route에서 navigation 콜백 파라미터로 받기

### 3.4 앱 수준 Navigation 구조 (App-Level Navigation Structure)

**IMPORTANT**: 앱의 전체 네비게이션 구조는 `app` 모듈의 `main` 패키지에서 관리됩니다. 이 구조는 변경하지 말고, 새로운 화면을 추가할 때만 확장하세요.

#### MainActivity

앱의 진입점이며, 다음을 담당합니다:

**필수 구현 요소:**
```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var timeZoneMonitor: TimeZoneMonitor

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. SplashScreen 설치
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // 2. ViewModel의 isLoading을 기반으로 스플래시 유지
        splashScreen.setKeepOnScreenCondition { viewModel.isLoading.value }

        // 3. EdgeToEdge 활성화
        enableEdgeToEdge()

        setContent {
            val startDestination by viewModel.startDestination.collectAsStateWithLifecycle()
            val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

            // 4. 로딩 완료 후 UI 표시
            if (!isLoading) {
                val appState = rememberSixPackAppState(
                    navigator = rememberMainNavigator(startDestination = startDestination),
                    networkMonitor = networkMonitor,
                    timeZoneMonitor = timeZoneMonitor,
                )

                val currentTimeZone by appState.currentTimeZone.collectAsStateWithLifecycle()

                // 5. CompositionLocalProvider로 전역 상태 제공
                CompositionLocalProvider(
                    LocalTimeZone provides currentTimeZone,
                ) {
                    SixpackTheme(isDebug = BuildConfig.DEBUG) {
                        MainScreen(appState = appState)
                    }
                }
            }
        }
    }
}
```

**핵심 패턴:**
- `@AndroidEntryPoint`: Hilt 의존성 주입
- `installSplashScreen()`: 스플래시 화면 설치
- `setKeepOnScreenCondition`: ViewModel의 로딩 상태로 스플래시 유지
- `enableEdgeToEdge()`: 전체 화면 사용 (StatusBar, NavigationBar 영역까지)
- `rememberSixPackAppState`: 앱 전역 상태 관리
- `CompositionLocalProvider`: 전역 값 제공 (TimeZone 등)

#### MainViewModel

앱의 시작 화면(destination)과 로딩 상태를 관리합니다.

**구현:**
```kotlin
@HiltViewModel
class MainViewModel @Inject constructor(
    private val getOnboardingStatusUseCase: GetOnboardingStatusUseCase,
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _startDestination = MutableStateFlow<Route>(OnboardingRoute)
    val startDestination: StateFlow<Route> = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            // 온보딩 완료 여부나 로그인 여부에 따라 startDestination 결정
            val isOnboardingComplete = getOnboardingStatusUseCase()

            _startDestination.value = if (isOnboardingComplete) {
                MainRoute.Running
            } else {
                OnboardingRoute
            }

            delay(1000L) // 최소 스플래시 표시 시간
            _isLoading.value = false
        }
    }
}
```

**핵심 패턴:**
- `BaseViewModel`을 사용하지 않음 (MVI 패턴 불필요)
- `StateFlow`로 로딩 상태와 시작 화면 관리
- `init` 블록에서 비즈니스 로직 실행 (온보딩 상태, 로그인 상태 체크)
- 시작 화면을 동적으로 결정 (OnboardingRoute vs MainRoute)

#### MainScreen

앱의 메인 Scaffold와 BottomBar를 구성합니다.

**구현:**
```kotlin
@Composable
internal fun MainScreen(
    modifier: Modifier = Modifier,
    appState: SixPackAppState,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val isOffline by appState.isOffline.collectAsStateWithLifecycle()

    // 오프라인 상태 체크
    LaunchedEffect(isOffline) {
        if (isOffline) {
            snackbarHostState.showSnackbar(
                message = "인터넷 연결을 확인해주세요.",
                duration = Indefinite,
            )
        }
    }

    Scaffold(
        bottomBar = {
            MainBottomBar(
                modifier = Modifier.navigationBarsPadding(),
                visible = appState.navigator.shouldShowBottomBar(),
                mainNavTabs = MainNavTab.entries,
                currentTab = appState.navigator.currentTab,
                onTabSelected = { tab ->
                    appState.navigator.navigate(tab)
                },
            )
        },
        containerColor = SixpackTheme.colors.gray0,
    ) { paddingValue ->
        MainNavHost(
            appState = appState,
            onShowSnackbar = { message, action ->
                snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = action,
                    duration = SnackbarDuration.Short,
                ) == ActionPerformed
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValue.calculateBottomPadding()),
        )
    }
}
```

**핵심 패턴:**
- `Scaffold` + `bottomBar`: 하단 네비게이션 구조
- `SnackbarHostState`: 전역 스낵바 관리
- `isOffline` 체크: 네트워크 상태 모니터링
- `shouldShowBottomBar()`: 조건부 BottomBar 표시 (특정 화면에서만)
- `navigationBarsPadding()`: 시스템 바 영역 패딩
- `calculateBottomPadding()`: BottomBar 높이만큼 패딩

#### MainNavHost

앱의 모든 NavGraph를 구성합니다.

**구현:**
```kotlin
@Composable
internal fun MainNavHost(
    appState: SixPackAppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val navigator = appState.navigator

    NavHost(
        navController = navigator.navController,
        startDestination = navigator.startDestination,
    ) {
        // 단일 화면 추가
        composable<OnboardingRoute> {
            OnboardingRoute(
                onNavigateToSignUp = {
                    // TODO: SignUp Navigation
                },
                onNavigateToSignIn = {
                    // TODO: SignIn Navigation
                },
            )
        }

        // NavGraph 추가 (모듈화된 화면 그룹)
        addRunningSessionNavGraph(
            onNavigateToBack = navigator::popBackStack,
            navigateToSessionReport = navigator::navigateToSessionReport,
        )

        addSessionReportNavGraph(
            onNavigateToBack = navigator::popBackStack,
        )
    }
}
```

**핵심 패턴:**
- `NavHost`: 모든 화면의 컨테이너
- `startDestination`: MainNavigator에서 전달받은 동적 시작 화면
- `composable<Route>`: Type-safe navigation으로 단일 화면 등록
- `add[Feature]NavGraph`: NavGraphBuilder 확장 함수로 화면 그룹 등록
- `navigator::method`: 메서드 참조로 네비게이션 콜백 전달

**새로운 화면 추가 시:**
```kotlin
// 1. 단일 화면인 경우
composable<MyNewRoute> {
    MyNewRoute(
        onNavigateToBack = navigator::popBackStack,
    )
}

// 2. 화면 그룹인 경우 (여러 관련 화면)
addMyFeatureNavGraph(
    onNavigateToBack = navigator::popBackStack,
    navigateToNextScreen = navigator::navigateToNextScreen,
)
```

#### MainNavigator

NavController를 래핑하여 앱 전체의 네비게이션을 관리하는 클래스입니다.

**구현:**
```kotlin
class MainNavigator(
    val navController: NavHostController,
    val startDestination: Route,
) {
    private val previousDestination = mutableStateOf<NavDestination?>(null)

    // 현재 화면
    val currentDestination: NavDestination?
        @Composable get() {
            val currentEntry = navController.currentBackStackEntryFlow
                .collectAsState(initial = null)

            return currentEntry.value?.destination.also { destination ->
                if (destination != null) {
                    previousDestination.value = destination
                }
            } ?: previousDestination.value
        }

    // 현재 선택된 탭
    val currentTab: MainNavTab?
        @Composable get() = MainNavTab.find { tab ->
            currentDestination?.hasRoute(tab::class) == true
        }

    // 뒤로가기
    fun popBackStack() {
        navController.popBackStack()
    }

    // 특정 화면으로 이동
    fun navigateToSessionReport() {
        navController.navigateSessionReport(
            navOptions {
                popUpTo(MainRoute.Running) {
                    inclusive = false
                }
            }
        )
    }

    // 탭 네비게이션
    fun navigate(tab: MainNavTab) {
        val navOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        when (tab) {
            MainNavTab.RUNNING -> {
                navController.navigateRunning(navOptions)
            }
            MainNavTab.FEED -> {
                navController.navigateFeed(navOptions)
            }
            MainNavTab.MY_PAGE -> {
                navController.navigateMyPage(navOptions)
            }
        }
    }

    // BottomBar 표시 여부
    @Composable
    fun shouldShowBottomBar() = MainNavTab.contains {
        currentDestination?.hasRoute(it::class) == true
    }
}

@Composable
internal fun rememberMainNavigator(
    startDestination: Route,
    navController: NavHostController = rememberNavController(),
): MainNavigator = remember(navController, startDestination) {
    MainNavigator(navController, startDestination)
}
```

**핵심 패턴:**
- `NavHostController` 래핑: 전역 네비게이션 로직을 한곳에서 관리
- `currentDestination`: 현재 화면 추적 (previousDestination을 fallback으로 사용)
- `currentTab`: 현재 선택된 탭 계산 (BottomBar 상태 표시용)
- `shouldShowBottomBar()`: 현재 화면이 탭 화면인지 확인
- `navigate(tab)`: 탭 네비게이션 시 `saveState`/`restoreState` 사용
- `navOptions`: 백스택 관리를 위한 옵션 설정 (popUpTo, launchSingleTop 등)
- `rememberMainNavigator`: Composable 함수로 Navigator 인스턴스 생성

**새로운 네비게이션 함수 추가:**
```kotlin
// MainNavigator에 함수 추가
fun navigateToMyNewScreen() {
    navController.navigateMyNewScreen(
        navOptions {
            popUpTo(MainRoute.Home) {
                inclusive = false
            }
        }
    )
}
```

#### 체크리스트: 새로운 화면의 앱 수준 통합

새로운 화면을 앱에 통합할 때 확인하세요:

**MainNavHost 업데이트:**
- [ ] `MainNavHost`에 `composable<MyRoute>` 또는 `addMyNavGraph` 추가
- [ ] 필요한 네비게이션 콜백 전달 (onNavigateToBack, navigate... 등)

**MainNavigator 업데이트 (필요시):**
- [ ] 여러 곳에서 호출되는 화면이라면 `navigateToMyScreen()` 함수 추가
- [ ] 백스택 관리가 필요하면 `navOptions` 설정
- [ ] 탭 화면이라면 `MainNavTab`에 추가하고 `navigate(tab)` 업데이트

**Bottom Navigation (필요시):**
- [ ] 탭 화면인 경우 `MainNavTab` enum에 추가
- [ ] `shouldShowBottomBar()` 로직 확인

---

## 4. 공통 UI 컴포넌트 (Common UI Components)

**IMPORTANT**: 새로운 화면을 만들 때 항상 이 컴포넌트들을 먼저 확인하고 재사용하세요. 동일한 기능을 중복 구현하지 마세요.

### 4.1 Buttons

#### DoRunDefaultButton

표준 버튼 컴포넌트입니다. **모든 주요 액션 버튼은 이 컴포넌트를 사용하세요.**

**파라미터:**
- `text: String` - 버튼 텍스트
- `onClick: () -> Unit` - 클릭 이벤트
- `enabled: Boolean = true` - 활성화 상태
- `textColor: Color = SixpackTheme.colors.gray0` - 텍스트 색상
- `containerColor: Color = SixpackTheme.colors.blue600` - 배경 색상
- `disabledTextColor: Color = SixpackTheme.colors.gray400` - 비활성화 텍스트 색상
- `disabledContainerColor: Color = SixpackTheme.colors.gray100` - 비활성화 배경 색상
- `contentPadding: PaddingValues` - 내부 패딩

**특징:**
- 자동으로 `SixpackTheme.shapes.round12` 적용
- 버튼 텍스트는 `SixpackTheme.typography.b1Bold` 사용
- enabled 상태에 따라 자동으로 색상 변경

**사용 예시:**
```kotlin
// 기본 사용
DoRunDefaultButton(
    text = stringResource(R.string.common_next),
    onClick = { onIntent(SomeIntent.OnButtonClick) },
    enabled = state.isValid,
    modifier = Modifier.fillMaxWidth()
)

// 커스텀 색상
DoRunDefaultButton(
    text = stringResource(R.string.delete),
    onClick = { onIntent(SomeIntent.OnDeleteClick) },
    containerColor = SixpackTheme.colors.red,
    modifier = Modifier.fillMaxWidth()
)
```

### 4.2 Top Bars

#### DoRunNavigationTopBar

뒤로가기 버튼이 포함된 상단 앱바입니다. **뒤로가기가 필요한 모든 화면에서 사용하세요.**

**파라미터:**
- `navigateToBack: () -> Unit` - 뒤로가기 콜백
- `titleContent: @Composable (() -> Unit)? = null` - 중앙 타이틀 컴포저블

**특징:**
- 왼쪽에 뒤로가기 아이콘 자동 표시
- 44dp 높이 고정
- statusBarsPadding 자동 적용

**사용 예시:**
```kotlin
// 타이틀 없이
DoRunNavigationTopBar(
    navigateToBack = { onIntent(SomeIntent.OnBackButtonClick) }
)

// 타이틀 포함
DoRunNavigationTopBar(
    navigateToBack = { onIntent(SomeIntent.OnBackButtonClick) },
    titleContent = {
        Text(
            text = stringResource(R.string.signup_title),
            style = SixpackTheme.typography.t1Bold,
            color = SixpackTheme.colors.gray900
        )
    }
)
```

#### DoRunTopBarSlot

커스터마이징 가능한 TopBar 컴포넌트입니다. **더 복잡한 TopBar가 필요할 때 사용하세요.**

**파라미터:**
- `leadingContent: @Composable (() -> Unit)? = null` - 왼쪽 영역
- `content: @Composable (() -> Unit)? = null` - 중앙 영역
- `trailingContent: @Composable (() -> Unit)? = null` - 오른쪽 영역

**사용 예시:**
```kotlin
DoRunTopBarSlot(
    leadingContent = {
        IconButton(onClick = { onIntent(SomeIntent.OnBackButtonClick) }) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
        }
    },
    content = {
        Text(
            text = stringResource(R.string.title),
            style = SixpackTheme.typography.t1Bold
        )
    },
    trailingContent = {
        IconButton(onClick = { onIntent(SomeIntent.OnSettingsClick) }) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = null)
        }
    }
)
```

### 4.3 Dialogs

#### DoRunDefaultDialog

표준 다이얼로그 컴포넌트입니다.

**파라미터:**
- `title: String` - 다이얼로그 제목
- `subtitle: String?` - 부제목 (선택사항)
- `onDismissRequest: () -> Unit` - 다이얼로그 닫기 콜백
- `onCancelClick: () -> Unit` - 취소 버튼 클릭
- `onConfirmClick: () -> Unit` - 확인 버튼 클릭
- `confirmButtonType: DialogButtonType` - 확인 버튼 타입 (Primary/Secondary)

**사용 예시:**
```kotlin
if (state.showLogoutDialog) {
    DoRunDefaultDialog(
        title = stringResource(R.string.logout_dialog_title),
        subtitle = stringResource(R.string.logout_dialog_subtitle),
        onDismissRequest = { onIntent(SomeIntent.OnDismissLogoutDialog) },
        onCancelClick = { onIntent(SomeIntent.OnDismissLogoutDialog) },
        onConfirmClick = { onIntent(SomeIntent.OnLogoutConfirm) },
        confirmButtonType = DialogButtonType.Primary,
    )
}
```

### 4.4 Preview Wrapper

#### DoRunPreviewWrapper

**모든 @Preview 함수에서 반드시 사용해야 합니다.**

**특징:**
- SixpackTheme 자동 적용
- 디버그 모드로 설정 (isDebug = true)
- gray0 배경 Surface로 감싸짐

**사용 예시:**
```kotlin
@Preview
@Composable
private fun MyScreenPreview() {
    DoRunPreviewWrapper {
        MyScreen(
            state = MyState(),
            onIntent = {}
        )
    }
}
```

### 4.5 Permission Handling

#### PermissionHandler

권한 요청 및 상태 관리를 위한 Composable 함수입니다.

**파라미터:**
- `context: Context`
- `lifecycleOwner: LifecycleOwner`
- `permissionsToRequest: List<SixPackPermissions>` - 요청할 권한 리스트
- `onPermissionResult: (Boolean) -> Unit` - 권한 결과 콜백

**특징:**
- 최초 진입 시 자동으로 권한 요청
- 화면 재활성화(onResume) 시 권한 상태 체크
- 모든 권한이 허용되었을 때만 true 반환

**사용 예시:**
```kotlin
@Composable
fun MyRoute() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    PermissionHandler(
        context = context,
        lifecycleOwner = lifecycleOwner,
        permissionsToRequest = listOf(
            SixPackPermissions.LOCATION_FINE,
            SixPackPermissions.LOCATION_COARSE
        ),
        onPermissionResult = { isGranted ->
            if (isGranted) {
                // 권한 허용됨
            } else {
                // 권한 거부됨
            }
        }
    )
}
```

---

## 5. Theme 시스템 (Theme System)

**IMPORTANT**: 모든 UI 요소는 반드시 `SixpackTheme`에 정의된 값을 사용해야 합니다. 하드코딩된 색상, 폰트 크기, Shape를 직접 작성하지 마세요.

### 5.1 Colors (`SixpackTheme.colors`)

#### Primary Colors
- `blue600` - 주요 액션 버튼, 강조 색상 (#3E4FFF)
- `blue900`, `blue800`, `blue700`, `blue500`, `blue400`, `blue300`, `blue200`, `blue100` - 블루 계열 색상
- `lime600` - 보조 강조 색상 (#D2FF3E)

#### Grayscale Colors
- `gray900` - 주요 텍스트 (#232529)
- `gray800` - 부제목 텍스트 (#3B3E43)
- `gray700`, `gray600`, `gray500` - 중간톤 텍스트
- `gray400`, `gray300`, `gray200`, `gray100` - 비활성화, 테두리
- `gray50`, `gray10` - 배경색
- `gray0` - 흰색 배경 (#FFFFFF)

#### Semantic Colors
- `red` - 에러, 경고 (#FF443B)
- `redLight` - 에러 배경 (#FFE5E4)
- `yellow` - 주의 (#FFE14D)
- `green` - 성공 (#2DDD93)

**사용 예시:**
```kotlin
Text(
    text = "Title",
    color = SixpackTheme.colors.gray900
)

Surface(
    color = SixpackTheme.colors.gray0
)

Button(
    colors = ButtonDefaults.buttonColors(
        containerColor = SixpackTheme.colors.blue600
    )
)
```

### 5.2 Typography (`SixpackTheme.typography`)

**모든 타이포그래피는 Pretendard 폰트를 사용하며, -0.2sp letterSpacing이 적용되어 있습니다.**

#### Headline (h)
- `h1Bold` - 28sp, Bold, 38sp lineHeight
- `h1Medium` - 28sp, Medium, 38sp lineHeight
- `h2Bold` - 24sp, Bold, 34sp lineHeight (화면 타이틀용)
- `h2Medium` - 24sp, Medium, 34sp lineHeight
- `h2Regular` - 24sp, Regular, 34sp lineHeight
- `h3Bold` - 32sp, Bold, 42sp lineHeight
- `h4Bold` - 40sp, Bold, 50sp lineHeight

#### Title (t)
- `t1Bold` - 20sp, Bold, 28sp lineHeight
- `t1Medium` - 20sp, Medium, 28sp lineHeight
- `t1Regular` - 20sp, Regular, 28sp lineHeight
- `t2Bold` - 18sp, Bold, 26sp lineHeight
- `t2Medium` - 18sp, Medium, 26sp lineHeight
- `t2Regular` - 18sp, Regular, 26sp lineHeight

#### Body (b)
- `b1Bold` - 16sp, Bold, 24sp lineHeight (버튼 텍스트용)
- `b1Medium` - 16sp, Medium, 24sp lineHeight
- `b1Regular` - 16sp, Regular, 24sp lineHeight (본문용)
- `b2Bold` - 14sp, Bold, 21sp lineHeight
- `b2Medium` - 14sp, Medium, 21sp lineHeight
- `b2Regular` - 14sp, Regular, 21sp lineHeight

#### Caption (c)
- `c1Bold` - 12sp, Bold, 18sp lineHeight
- `c1Medium` - 12sp, Medium, 18sp lineHeight
- `c1Regular` - 12sp, Regular, 18sp lineHeight (작은 텍스트, 에러 메시지용)

**사용 예시:**
```kotlin
Text(
    text = stringResource(R.string.welcome),
    style = SixpackTheme.typography.h2Bold  // 화면 타이틀
)

Text(
    text = stringResource(R.string.body_text),
    style = SixpackTheme.typography.b1Regular
)

Text(
    text = stringResource(R.string.error_message),
    style = SixpackTheme.typography.c1Regular,
    color = SixpackTheme.colors.red
)
```

### 5.3 Shapes (`SixpackTheme.shapes`)

**모든 Shape는 RoundedCornerShape로 정의되어 있습니다:**
- `round4` - 4dp corner radius
- `round8` - 8dp corner radius
- `round12` - 12dp corner radius (기본 버튼, TextField용)
- `round16` - 16dp corner radius
- `round20` - 20dp corner radius
- `full` - 50% corner radius (완전한 원형)

**사용 예시:**
```kotlin
OutlinedTextField(
    shape = SixpackTheme.shapes.round12
)

Button(
    shape = SixpackTheme.shapes.round12
)

Surface(
    shape = SixpackTheme.shapes.round16
)
```

### 5.4 Dimensions (`SixPackDimen`)

- `SixPackDimen.defaultSideMargin` - 20.dp (기본 좌우 여백)

**사용 예시:**
```kotlin
Column(
    modifier = Modifier.padding(horizontal = SixPackDimen.defaultSideMargin)
)
```

---

## 6. Utility Functions

### 6.1 Format Extensions

#### formatSecondsToTime

총 초를 "HH:MM:SS" 형식으로 변환합니다.

**사용 예시:**
```kotlin
val totalSeconds = 3665
val timeString = formatSecondsToTime(totalSeconds) // "01:01:05"
```

#### formatPace

페이스(초/km)를 "M'SS\"" 형식으로 변환합니다.

**사용 예시:**
```kotlin
val paceInSeconds = 325
val paceString = formatPace(paceInSeconds) // "5'25\""
```

### 6.2 Modifier Extensions

#### noRippleClickable

리플 효과 없이 클릭 가능한 Modifier입니다.

**파라미터:**
- `enabled: Boolean = true`
- `onClickLabel: String? = null`
- `role: Role? = null`
- `onClick: () -> Unit`

**사용 예시:**
```kotlin
Box(
    modifier = Modifier
        .size(48.dp)
        .noRippleClickable {
            onIntent(SomeIntent.OnItemClick)
        }
)
```

#### throttleClick

클릭 이벤트를 쓰로틀링하여 중복 클릭을 방지합니다.

**사용 예시:**
```kotlin
DoRunDefaultButton(
    text = stringResource(R.string.button_text),
    onClick = throttleClick {
        onIntent(SomeIntent.OnButtonClick)
    },
)
```

---

## 7. String Resources (문자열 리소스)

**IMPORTANT**: UI에 표시되는 번역이 필요한 모든 텍스트는 반드시 `strings.xml` 리소스 파일을 사용해야 합니다. 하드코딩된 문자열을 직접 작성하지 마세요.

### 7.1 기본 원칙

1. **UI 텍스트는 모두 strings.xml에 정의**
    - Composable 함수의 `Text`, `OutlinedTextField`, 버튼 텍스트 등 모든 UI 텍스트는 `stringResource()` 사용
    - 토스트 메시지, 다이얼로그 메시지도 string 리소스 사용
    - 예외: 로그 메시지(Timber), 개발자용 주석

2. **ViewModel에서 String 리소스 사용**
    - ViewModel은 Context에 직접 접근할 수 없으므로 구체적인 SideEffect를 정의
    - SideEffect에서 메시지를 전달할 때 구체적인 타입으로 정의
    - Route에서 `stringResource()`로 변환하여 표시

3. **리소스 이름 규칙**
    - `[화면이름]_[카테고리]_[설명]` 형식 사용
    - 예시:
        - `signup_title_phone_input` - SignUp 화면의 타이틀
        - `signup_error_invalid_phone_number` - SignUp 화면의 에러 메시지
        - `signup_placeholder_verification_code` - SignUp 화면의 placeholder
        - `common_next`, `common_complete` - 여러 화면에서 공통으로 사용되는 텍스트

### 7.2 Composable에서 String 리소스 사용

```kotlin
// ✅ 올바른 예시
@Composable
fun MyScreen() {
    Text(
        text = stringResource(R.string.signup_title_phone_input),
        style = SixpackTheme.typography.h2Bold
    )

    OutlinedTextField(
        placeholder = { Text(stringResource(R.string.signup_placeholder_phone_number)) },
        label = { Text(stringResource(R.string.signup_label_phone_number)) }
    )

    DoRunDefaultButton(
        text = stringResource(R.string.common_next),
        onClick = { /* ... */ }
    )
}

// ❌ 잘못된 예시
@Composable
fun MyScreen() {
    Text(
        text = "환영합니다!", // 하드코딩된 텍스트
        style = SixpackTheme.typography.h2Bold
    )

    OutlinedTextField(
        placeholder = { Text("010-0000-0000") } // 하드코딩된 텍스트
    )
}
```

### 7.3 ViewModel과 SideEffect에서 String 리소스 사용

**IMPORTANT**: ViewModel이 Android framework의 R 클래스에 의존하지 않도록, **SideEffect를 구체적으로 정의**하는 것을 권장합니다. 이는 테스트 가능성을 높이고 더 명확한 의미를 제공합니다.

**SideEffect 정의 (contract):**
```kotlin
// ✅ 올바른 예시 - 구체적인 SideEffect 정의 (권장)
sealed interface SignUpSideEffect : SideEffect {
    data object NavigateToTermsAgreement : SignUpSideEffect
    data object NavigateBack : SignUpSideEffect

    // 각 메시지를 구체적인 SideEffect로 정의
    data object ShowInvalidPhoneNumberError : SignUpSideEffect
    data object ShowCodeSentSuccess : SignUpSideEffect
    data object ShowCodeSendFailedError : SignUpSideEffect
    data object ShowInvalidCodeLengthError : SignUpSideEffect
    data object ShowCodeMismatchError : SignUpSideEffect
    data object ShowCodeExpiredError : SignUpSideEffect
}

// ❌ 잘못된 예시 - 하드코딩된 문자열 사용
sealed interface SignUpSideEffect : SideEffect {
    data class ShowToast(
        val message: String, // 하드코딩된 문자열 사용 금지
    ) : SignUpSideEffect
}
```

**ViewModel 구현:**
```kotlin
// ✅ 올바른 예시 - R 클래스에 의존하지 않음 (권장)
@HiltViewModel
class SignUpViewModel @Inject constructor(...) : BaseViewModel<...>() {

    private fun handleSendCode() = intent {
        if (!state.isPhoneNumberValid) {
            postSideEffect(SignUpSideEffect.ShowInvalidPhoneNumberError)
            return@intent
        }
        // ...
        postSideEffect(SignUpSideEffect.ShowCodeSentSuccess)
    }
}

// ❌ 잘못된 예시 - 하드코딩된 문자열
@HiltViewModel
class SignUpViewModel @Inject constructor(...) : BaseViewModel<...>() {

    private fun handleSendCode() = intent {
        if (!state.isPhoneNumberValid) {
            postSideEffect(SignUpSideEffect.ShowToast("올바른 전화번호를 입력해주세요."))
            return@intent
        }
    }
}
```

**Route에서 SideEffect 처리:**
```kotlin
// ✅ 올바른 예시 - 구체적인 SideEffect 처리 (권장)
@Composable
fun SignUpRoute(...) {
    val context = LocalContext.current

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SignUpSideEffect.NavigateToTermsAgreement -> onNavigateToTermsAgreement()
            is SignUpSideEffect.NavigateBack -> onNavigateBack()
            is SignUpSideEffect.ShowInvalidPhoneNumberError -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.signup_error_invalid_phone_number),
                    Toast.LENGTH_SHORT,
                ).show()
            }
            is SignUpSideEffect.ShowCodeSentSuccess -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.signup_success_code_sent),
                    Toast.LENGTH_SHORT,
                ).show()
            }
            // ... 다른 SideEffect들
        }
    }
}
```

**구체적인 SideEffect를 사용하는 이유:**
1. **테스트 가능성**: ViewModel이 Android framework(R 클래스)에 의존하지 않아 단위 테스트가 쉬워집니다.
2. **명확성**: 각 SideEffect의 의미가 명확하여 코드 가독성이 향상됩니다.
3. **타입 안정성**: 컴파일 타임에 모든 SideEffect가 처리되었는지 확인할 수 있습니다.
4. **유지보수성**: 새로운 메시지가 추가될 때 ViewModel과 Route 모두 수정이 필요하지만, 변경 사항을 추적하기 쉽습니다.

### 7.4 strings.xml 구조

모든 string 리소스는 `presentation/src/main/res/values/strings.xml`에 정의합니다.

**카테고리별로 구분:**
```xml
<resources>
    <!-- region Common -->
    <string name="common_next">다음</string>
    <string name="common_complete">완료</string>
    <!-- endregion -->

    <!-- region Sign Up -->
    <string name="signup_title_phone_input">환영합니다!\n휴대폰 번호로 가입해주세요.</string>
    <string name="signup_title_verification_input">인증번호 6자리를\n입력해주세요.</string>
    <string name="signup_label_phone_number">휴대폰 번호</string>
    <string name="signup_placeholder_phone_number">010-0000-0000</string>
    <string name="signup_error_invalid_phone_number">올바른 전화번호를 입력해주세요.</string>
    <!-- endregion -->

    <!-- region Running Session -->
    <string name="session_title">러닝 세션</string>
    <!-- endregion -->
</resources>
```

### 7.5 주의사항

1. **다국어 지원 대비**
    - 현재는 한국어만 지원하지만, 추후 다국어 지원을 위해 모든 텍스트를 리소스로 관리

2. **동적 텍스트 (String Formatting)**
   ```xml
   <!-- strings.xml -->
   <string name="home_goal_session_count">%d회차 목표</string>
   ```
   ```kotlin
   // Composable
   Text(text = stringResource(R.string.home_goal_session_count, sessionCount))
   ```

3. **줄바꿈 포함 텍스트**
   ```xml
   <!-- strings.xml -->
   <string name="signup_title_phone_input">환영합니다!\n휴대폰 번호로 가입해주세요.</string>
   ```

---

## 8. Dialog 상태 관리

**IMPORTANT**: Dialogs는 **UI State**로 관리해야 하며, **SideEffects로 관리하지 마세요**.

### 8.1 ❌ DON'T: Use SideEffects for dialog visibility

```kotlin
// BAD: Dialog state in SideEffect
sealed interface FeatureSideEffect : SideEffect {
    data object ShowLogoutDialog : FeatureSideEffect
    data object ShowWithdrawDialog : FeatureSideEffect
}

// BAD: Managing dialog with local state in Route
@Composable
fun FeatureRoute(...) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.container.sideEffectFlow.collect { sideEffect ->
            when (sideEffect) {
                FeatureSideEffect.ShowLogoutDialog -> showLogoutDialog = true
            }
        }
    }
}
```

### 8.2 ✅ DO: Use UI State for dialog visibility

```kotlin
// GOOD: Dialog state in State
@Parcelize
data class FeatureState(
    val isLoading: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val showWithdrawDialog: Boolean = false,
) : UiState

// GOOD: Intents for dialog control
sealed interface FeatureIntent : UiIntent {
    data object OnLogoutClick : FeatureIntent
    data object OnDismissLogoutDialog : FeatureIntent
    data object OnLogoutConfirm : FeatureIntent
}

// GOOD: Handle in ViewModel
private fun handleLogoutClick() = intent {
    reduce { state.copy(showLogoutDialog = true) }
}

private fun handleDismissLogoutDialog() = intent {
    reduce { state.copy(showLogoutDialog = false) }
}

private fun handleLogoutConfirm() = intent {
    reduce { state.copy(isLoading = true) }

    logoutUseCase()
        .onSuccess {
            reduce {
                state.copy(
                    isLoading = false,
                    showLogoutDialog = false, // Close dialog on success
                )
            }
            postSideEffect(FeatureSideEffect.LogoutSuccess)
        }
        .onError {
            reduce {
                state.copy(
                    isLoading = false,
                    showLogoutDialog = false, // Close dialog on error
                )
            }
            postSideEffect(FeatureSideEffect.LogoutFailed)
        }
}

// GOOD: Use state in Screen
@Composable
internal fun FeatureScreen(
    state: FeatureState,
    onIntent: (FeatureIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Screen content...

    if (state.showLogoutDialog) {
        DoRunDefaultDialog(
            title = stringResource(R.string.logout_dialog_title),
            subtitle = stringResource(R.string.logout_dialog_subtitle),
            onDismissRequest = { onIntent(FeatureIntent.OnDismissLogoutDialog) },
            onCancelClick = { onIntent(FeatureIntent.OnDismissLogoutDialog) },
            onConfirmClick = { onIntent(FeatureIntent.OnLogoutConfirm) },
            confirmButtonType = DialogButtonType.Primary,
        )
    }
}
```

### 8.3 Why State over SideEffect?

**Use State when**:
- 값이 현재 UI 상태를 나타냄
- 다른 상태에서 파생 가능
- 구성 변경 시에도 유지되어야 함
- 사용자가 상호작용할 수 있음 (dialogs, bottom sheets 등)
- 예시: `showDialog`, `isBottomSheetOpen`, `expandedSectionId`

**Use SideEffect when**:
- 일회성 이벤트
- 재구성 시 반복되지 않아야 함
- 외부 액션을 트리거 (navigation, toast, snackbar)
- 예시: `NavigateToNextScreen`, `ShowSuccessMessage`, `ScrollToTop`

---

## 9. Error Handling Pattern (에러 처리 패턴)

### 9.1 기본 에러 처리

```kotlin
private fun handleSomeAction() = intent {
    reduce { state.copy(isLoading = true) }

    someUseCase()
        .onSuccess { result ->
            reduce { state.copy(isLoading = false, data = result) }
            postSideEffect(FeatureSideEffect.ShowSuccessMessage)
        }
        .onError { exception ->
            Timber.e("Action failed: ${exception.message}")
            reduce {
                state.copy(
                    isLoading = false,
                    errorMessage = exception.message,
                )
            }

            when (exception) {
                is DoRunException.NetworkError -> {
                    postSideEffect(FeatureSideEffect.ShowNetworkError)
                }
                is DoRunException.ValidationError -> {
                    postSideEffect(FeatureSideEffect.ShowValidationError)
                }
                else -> {
                    postSideEffect(FeatureSideEffect.ShowGenericError)
                }
            }
        }
}
```

### 9.2 에러 상태 표시

```kotlin
// State에 에러 정보 포함
data class FeatureState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) : UiState

// Screen에서 에러 표시
@Composable
internal fun FeatureScreen(
    state: FeatureState,
    onIntent: (FeatureIntent) -> Unit,
) {
    Column {
        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage,
                style = SixpackTheme.typography.c1Regular,
                color = SixpackTheme.colors.red
            )
        }
        // ... 나머지 UI
    }
}
```

---

## 10. Loading State Pattern (로딩 상태 패턴)

### 10.1 전체 화면 로딩

```kotlin
// State에 로딩 상태 포함
data class FeatureState(
    val isLoading: Boolean = false,
) : UiState

// Screen에서 전체 화면 로딩 표시
@Composable
internal fun FeatureScreen(
    state: FeatureState,
    onIntent: (FeatureIntent) -> Unit,
) {
    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        // 실제 콘텐츠
    }
}
```

### 10.2 버튼 로딩

```kotlin
// State에 버튼별 로딩 상태 포함
data class FeatureState(
    val isLoading: Boolean = false,
    val isButtonLoading: Boolean = false,
) : UiState

// Screen에서 버튼 로딩 표시
DoRunDefaultButton(
    text = if (state.isButtonLoading) {
        stringResource(R.string.loading)
    } else {
        stringResource(R.string.button_text)
    },
    onClick = { onIntent(SomeIntent.OnButtonClick) },
    enabled = !state.isButtonLoading,
)
```

---

## 11. Common Patterns (자주 사용되는 패턴)

### 11.1 Phone Number Input with SMS Verification

```kotlin
// State
data class AuthState(
    val phoneNumber: String = "",
    val verificationCode: String = "",
    val step: PhoneAuthStep = PhoneAuthStep.PHONE_INPUT,
    val remainingTimeInSeconds: Int = RETRY_TIME_IN_SECONDS,
) : UiState {
    val isPhoneNumberValid: Boolean
        get() = phoneNumber.length == 11

    val isVerificationCodeValid: Boolean
        get() = verificationCode.length == 6

    companion object {
        const val RETRY_TIME_IN_SECONDS = 180
    }
}

// Timer in ViewModel
private var timerJob: Job? = null

private fun startTimer() {
    stopTimer()
    timerJob = viewModelScope.launch {
        while (container.stateFlow.value.remainingTimeInSeconds > 0) {
            delay(1000)
            intent {
                reduce {
                    state.copy(remainingTimeInSeconds = state.remainingTimeInSeconds - 1)
                }
            }
        }
        intent {
            postSideEffect(AuthSideEffect.ShowCodeExpiredError)
        }
    }
}

private fun stopTimer() {
    timerJob?.cancel()
    timerJob = null
}

override fun onCleared() {
    super.onCleared()
    stopTimer()
}
```

### 11.2 Input Validation

```kotlin
// Computed properties in State
data class ProfileState(
    val nickname: String = "",
) : UiState {
    val isNicknameValid: Boolean
        get() = nickname.length in 2..10

    val canProceed: Boolean
        get() = isNicknameValid
}

// Intent handler
private fun handleNicknameChanged(nickname: String) = intent {
    reduce {
        state.copy(
            nickname = nickname.take(10), // Limit input
            errorMessage = null,
        )
    }
}
```

### 11.3 Throttling/Debouncing Clicks

Use `throttleClick` modifier for preventing double clicks:

```kotlin
DoRunDefaultButton(
    text = stringResource(R.string.button_text),
    onClick = throttleClick {
        onIntent(SomeIntent.OnButtonClick)
    },
)
```

---

## 12. Preview Pattern (프리뷰 패턴)

**ALWAYS** 모든 composable에 프리뷰를 제공하세요. 재사용 가능한 컴포넌트도 포함합니다.

### 12.1 Screen Previews

```kotlin
@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FeatureScreenPreview() {
    DoRunPreviewWrapper {
        FeatureScreen(
            state = FeatureState(
                data = "Sample Data",
                isLoading = false,
            ),
            onIntent = {},
        )
    }
}

@Preview(name = "Loading State")
@Composable
private fun FeatureScreenLoadingPreview() {
    DoRunPreviewWrapper {
        FeatureScreen(
            state = FeatureState(isLoading = true),
            onIntent = {},
        )
    }
}

@Preview(name = "Error State")
@Composable
private fun FeatureScreenErrorPreview() {
    DoRunPreviewWrapper {
        FeatureScreen(
            state = FeatureState(
                errorMessage = "Something went wrong",
            ),
            onIntent = {},
        )
    }
}
```

### 12.2 Component Previews

**ALWAYS** `ui/component/` 패키지의 재사용 가능한 컴포넌트에 프리뷰를 제공하세요.

**핵심 원칙**:
- 컴포넌트 프리뷰는 `private` (컴포넌트 자체는 `internal`)
- `DoRunPreviewWrapper` 사용으로 테마 지원
- 다양한 상태의 여러 프리뷰 제공
- 실제 사용을 나타내는 현실적인 목 데이터 사용
- 엣지 케이스 포함 (empty, loading, error states)

**예시 1: Simple Component**

```kotlin
@Composable
internal fun EmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    // Component implementation
}

@Preview
@Composable
private fun EmptyStatePreview() {
    DoRunPreviewWrapper {
        EmptyState(
            title = "아직 완료한 인증이 없어요...",
            description = "러닝을 완료하면 인증할 수 있어요!",
        )
    }
}
```

**예시 2: Component with Multiple States**

```kotlin
@Composable
internal fun RecordCard(
    record: RecordItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Component implementation
}

// Preview for completed state
@Preview
@Composable
private fun RecordCardCompletedPreview() {
    DoRunPreviewWrapper {
        RecordCard(
            record = RecordItem(
                id = 1,
                date = "2025.09.30 (화)",
                time = "오전 10:11",
                distanceKm = 8.02,
                certificationStatus = CertificationStatus.COMPLETED,
            ),
            onClick = {},
        )
    }
}

// Preview for available state
@Preview
@Composable
private fun RecordCardAvailablePreview() {
    DoRunPreviewWrapper {
        RecordCard(
            record = RecordItem(
                id = 2,
                date = "2025.10.01 (수)",
                time = "오후 02:30",
                distanceKm = 5.5,
                certificationStatus = CertificationStatus.AVAILABLE,
            ),
            onClick = {},
        )
    }
}
```

**예시 3: Component with Paging Data**

`LazyPagingItems`를 받는 컴포넌트의 경우 `flowOf(PagingData.from(...)).collectAsLazyPagingItems()` 사용:

```kotlin
@Preview
@Composable
private fun PostGridPreview() {
    DoRunPreviewWrapper {
        val mockGridItems = listOf(
            GridItemType.MonthLabel(year = 2025, month = 10),
            GridItemType.PostItem(Post(id = 1, imageUrl = null, createdAt = "2025-10-14T10:30:00Z")),
            GridItemType.PostItem(Post(id = 2, imageUrl = null, createdAt = "2025-10-12T15:20:00Z")),
        )
        val gridItemsPagingItems = flowOf(PagingData.from(mockGridItems)).collectAsLazyPagingItems()

        PostGrid(
            gridItemsPagingItems = gridItemsPagingItems,
        )
    }
}
```

---

## 13. 흔한 실수 방지 (Common Mistakes to Avoid)

### 13.1 ❌ DON'T: Hardcode strings

```kotlin
Text(text = "Sign In")
```

### 13.2 ✅ DO: Use string resources

```kotlin
Text(text = stringResource(R.string.sign_in_title))
```

---

### 13.3 ❌ DON'T: Call use cases directly in composables

```kotlin
@Composable
fun Screen(useCase: SomeUseCase) {
    Button(onClick = { useCase.invoke() }) { }
}
```

### 13.4 ✅ DO: Call through ViewModel via Intents

```kotlin
@Composable
fun Screen(onIntent: (Intent) -> Unit) {
    Button(onClick = { onIntent(SomeIntent.OnButtonClick) }) { }
}
```

---

### 13.5 ❌ DON'T: Mutate state directly

```kotlin
fun handleClick() {
    state = state.copy(isLoading = true) // Error!
}
```

### 13.6 ✅ DO: Use intent and reduce

```kotlin
private fun handleClick() = intent {
    reduce { state.copy(isLoading = true) }
}
```

---

### 13.7 ❌ DON'T: Use navigation in ViewModel

```kotlin
private fun handleClick() = intent {
    navController.navigate(NextRoute) // Error!
}
```

### 13.8 ✅ DO: Use SideEffects for navigation

```kotlin
private fun handleClick() = intent {
    postSideEffect(FeatureSideEffect.NavigateToNext)
}
```

---

## 14. File Naming Conventions (파일 명명 규칙)

- **Route**: `[Feature]Route.kt` (예: `SignInRoute.kt`)
- **ViewModel**: `[Feature]ViewModel.kt` (예: `SignInViewModel.kt`)
- **State**: `[Feature]State.kt` (예: `SignInState.kt`)
- **Intent**: `[Feature]Intent.kt` (예: `SignInIntent.kt`)
- **SideEffect**: `[Feature]SideEffect.kt` (예: `SignInSideEffect.kt`)
- **Screen**: `[Feature]Screen.kt` (예: `SignInScreen.kt`)
- **Navigation**: `[Feature]Navigation.kt` (예: `SignInNavigation.kt`)
- **Components**: `[Component]Component.kt` 또는 설명적인 이름

---

## 15. Testing Considerations (테스트 고려사항)

다음을 위한 광범위한 프리뷰 composables:
- Light 및 Dark mode
- 다양한 화면 크기
- 다양한 상태 (loading, error, success)
- Empty states
- Edge cases

```kotlin
@Preview(name = "Loading State")
@Composable
private fun FeatureScreenLoadingPreview() {
    DoRunPreviewWrapper {
        FeatureScreen(
            state = FeatureState(isLoading = true),
            onIntent = {},
        )
    }
}

@Preview(name = "Error State")
@Composable
private fun FeatureScreenErrorPreview() {
    DoRunPreviewWrapper {
        FeatureScreen(
            state = FeatureState(
                errorMessage = "Something went wrong",
            ),
            onIntent = {},
        )
    }
}

@Preview(name = "Empty State")
@Composable
private fun FeatureScreenEmptyPreview() {
    DoRunPreviewWrapper {
        FeatureScreen(
            state = FeatureState(data = emptyList()),
            onIntent = {},
        )
    }
}
```

---

## 16. Summary Checklist (요약 체크리스트)

새로운 프레젠테이션 코드를 만들 때 확인하세요:

**MVI Pattern:**
- [ ] Orbit를 사용한 MVI 패턴 사용 (State, Intent, SideEffect)
- [ ] ViewModel이 올바른 제네릭으로 `BaseViewModel` 상속
- [ ] `@HiltViewModel` 및 생성자 주입 사용
- [ ] State는 `UiState`를 구현하는 불변 data class
- [ ] Intent는 `UiIntent`를 구현하는 sealed interface
- [ ] SideEffect는 `SideEffect`를 구현하는 sealed interface
- [ ] 모든 상태 변경은 `intent { reduce { } }` 사용
- [ ] 일회성 이벤트는 `postSideEffect()` 사용

**Screen Structure:**
- [ ] Route composable이 state와 side effects를 수집
- [ ] Screen composable은 stateless이며 internal
- [ ] 모든 문자열에 `stringResource()` 사용
- [ ] `@Serializable`로 type-safe routes 사용한 네비게이션
- [ ] `DoRunPreviewWrapper`로 프리뷰 제공

**Error & Loading:**
- [ ] 패턴 매칭으로 적절한 에러 처리
- [ ] 로딩 상태를 적절히 처리

**Common Components:**
- [ ] `DoRunDefaultButton` 사용
- [ ] `DoRunNavigationTopBar` 사용
- [ ] `SixpackTheme` 색상, 타이포그래피, 형태 사용

**Dialog Management:**
- [ ] Dialog는 State로 관리 (SideEffect 아님)

---

## 17. 예시 (Examples)

아래는 가상의 `Profile` 화면을 생성하는 완전한 예시입니다.

### ProfileRoute.kt

```kotlin
package com.dpm.sixpack.presentation.routes.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsState
import com.dpm.sixpack.presentation.routes.profile.contract.ProfileIntent
import com.dpm.sixpack.presentation.routes.profile.contract.ProfileSideEffect
import com.dpm.sixpack.presentation.routes.profile.ui.screen.ProfileScreen

@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToBack: () -> Unit,
) {
    val state by viewModel.container.stateFlow.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.container.sideEffectFlow.collect { sideEffect ->
            when (sideEffect) {
                is ProfileSideEffect.NavigateBack -> onNavigateToBack()
            }
        }
    }

    ProfileScreen(
        state = state,
        onIntent = viewModel::onIntent
    )
}
```

### ProfileViewModel.kt

```kotlin
package com.dpm.sixpack.presentation.routes.profile

import androidx.lifecycle.SavedStateHandle
import com.dpm.sixpack.presentation.common.base.BaseViewModel
import com.dpm.sixpack.presentation.routes.profile.contract.ProfileIntent
import com.dpm.sixpack.presentation.routes.profile.contract.ProfileSideEffect
import com.dpm.sixpack.presentation.routes.profile.contract.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    // UseCase 주입
) : BaseViewModel<ProfileState, ProfileIntent, ProfileSideEffect>() {

    override val initialState: ProfileState = ProfileState()

    override val container: Container<ProfileState, ProfileSideEffect> =
        container(initialState = initialState, savedStateHandle = savedStateHandle)

    override fun onIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.OnBackButtonClick -> handleBackButtonClick()
        }
    }

    private fun handleBackButtonClick() = intent {
        postSideEffect(ProfileSideEffect.NavigateBack)
    }
}
```

### contract/ProfileState.kt

```kotlin
package com.dpm.sixpack.presentation.routes.profile.contract

import com.dpm.sixpack.presentation.common.base.UiState
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileState(
    val isLoading: Boolean = false,
) : UiState
```

### contract/ProfileIntent.kt

```kotlin
package com.dpm.sixpack.presentation.routes.profile.contract

import com.dpm.sixpack.presentation.common.base.UiIntent

sealed interface ProfileIntent : UiIntent {
    data object OnBackButtonClick : ProfileIntent
}
```

### contract/ProfileSideEffect.kt

```kotlin
package com.dpm.sixpack.presentation.routes.profile.contract

import com.dpm.sixpack.presentation.common.base.SideEffect

sealed interface ProfileSideEffect : SideEffect {
    data object NavigateBack : ProfileSideEffect
}
```

### ui/screen/ProfileScreen.kt

```kotlin
package com.dpm.sixpack.presentation.routes.profile.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.dpm.sixpack.presentation.R
import com.dpm.sixpack.presentation.common.components.topbar.DoRunNavigationTopBar
import com.dpm.sixpack.presentation.routes.profile.contract.ProfileIntent
import com.dpm.sixpack.presentation.routes.profile.contract.ProfileState
import com.dpm.sixpack.presentation.theme.DoRunPreviewWrapper
import com.dpm.sixpack.presentation.theme.SixpackTheme

@Composable
internal fun ProfileScreen(
    state: ProfileState,
    onIntent: (ProfileIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            DoRunNavigationTopBar(
                navigateToBack = { onIntent(ProfileIntent.OnBackButtonClick) },
                titleContent = {
                    Text(
                        text = stringResource(R.string.profile_title),
                        style = SixpackTheme.typography.t1Bold,
                        color = SixpackTheme.colors.gray900,
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // UI 구성 (state 기반)
            Text(text = stringResource(R.string.profile_content))
        }
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    DoRunPreviewWrapper {
        ProfileScreen(
            state = ProfileState(),
            onIntent = {},
        )
    }
}
```

### navigation/ProfileNavigation.kt

```kotlin
package com.dpm.sixpack.presentation.routes.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.dpm.sixpack.presentation.destinations.ProfileRoute
import com.dpm.sixpack.presentation.routes.profile.ProfileRoute

fun NavController.navigateProfile(navOptions: NavOptions? = null) {
    navigate(ProfileRoute, navOptions)
}

fun NavGraphBuilder.addProfileNavGraph(
    onNavigateToBack: () -> Unit,
) {
    composable<ProfileRoute> {
        ProfileRoute(
            onNavigateToBack = onNavigateToBack,
        )
    }
}
```

### destinations/ProfileRoute.kt

```kotlin
package com.dpm.sixpack.presentation.destinations

import kotlinx.serialization.Serializable

@Serializable
data object ProfileRoute : Route
```

---

## 마무리

이 가이드라인을 따라 일관되고 유지보수 가능한 프레젠테이션 레이어 코드를 작성하세요. 항상 다음을 확인하세요:

1. **MVI 패턴 준수**: State, Intent, SideEffect 분리
2. **재사용 가능한 컴포넌트 사용**: 공통 컴포넌트 최대한 활용
3. **String 리소스 사용**: 모든 UI 텍스트는 strings.xml
4. **Type-Safe Navigation**: @Serializable routes 사용
5. **Dialog는 State로 관리**: SideEffect 아님
6. **프리뷰 제공**: 모든 composable에 프리뷰 작성
7. **Theme 시스템 사용**: SixpackTheme 값 사용

질문이 있거나 명확하지 않은 부분이 있다면 이 문서를 참조하거나 기존 코드의 예시를 확인하세요.
