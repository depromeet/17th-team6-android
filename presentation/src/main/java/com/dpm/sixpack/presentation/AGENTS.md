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
- **세부 구현**: 자세한 내용은 섹션 3-1 참조

## 3-1. Navigation System (Type-Safe Navigation)

**IMPORTANT**: SixPack은 Jetpack Navigation의 Type-Safe Navigation을 사용합니다. 모든 Route는 `@Serializable`을 사용하여 정의해야 합니다.

### Route 정의 (destinations 패키지)

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

### Navigation 함수 정의 (navigation 패키지)

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
    navOptions: NavOptions? = null
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

### Navigation 사용 예시

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

### Navigation 체크리스트

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

## 3-2. App-Level Navigation Structure

**IMPORTANT**: 앱의 전체 네비게이션 구조는 `app` 모듈의 `main` 패키지에서 관리됩니다. 이 구조는 변경하지 말고, 새로운 화면을 추가할 때만 확장하세요.

### MainActivity

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

### MainViewModel

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

### MainScreen

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

### MainNavHost

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

### MainNavigator

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

### 체크리스트: 새로운 화면의 앱 수준 통합

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

## 4. 코드 스타일

- **Jetpack Compose**: UI는 Jetpack Compose를 사용하여 선언적으로 작성합니다.
- **Hilt**: `@HiltViewModel`을 사용하여 ViewModel의 의존성을 주입합니다.
- **공용 컴포넌트**: UI 구현 시, `com.dpm.sixpack.presentation.common.components` 패키지에 정의된 공용 컴포넌트를 최대한 재사용합니다.
- **테마**: UI 요소의 색상, 타이포그래피, 모양(Shape)은 `SixpackTheme`에서 정의된 값을 사용해야 합니다. (예: `SixpackTheme.colors.blue600`, `SixpackTheme.typography.b1Bold`, `SixpackTheme.shapes.round12`)

## 4-1. Theme System (SixpackTheme)

**IMPORTANT**: 모든 UI 요소는 반드시 `SixpackTheme`에 정의된 값을 사용해야 합니다. 하드코딩된 색상, 폰트 크기, Shape를 직접 작성하지 마세요.

### Colors (`SixpackTheme.colors`)

**Primary Colors:**
- `blue600` - 주요 액션 버튼, 강조 색상 (#3E4FFF)
- `blue900`, `blue800`, `blue700`, `blue500`, `blue400`, `blue300`, `blue200`, `blue100` - 블루 계열 색상
- `lime600` - 보조 강조 색상 (#D2FF3E)

**Grayscale Colors:**
- `gray900` - 주요 텍스트 (#232529)
- `gray800` - 부제목 텍스트 (#3B3E43)
- `gray700`, `gray600`, `gray500` - 중간톤 텍스트
- `gray400`, `gray300`, `gray200`, `gray100` - 비활성화, 테두리
- `gray50`, `gray10` - 배경색
- `gray0` - 흰색 배경 (#FFFFFF)

**Semantic Colors:**
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

### Typography (`SixpackTheme.typography`)

**모든 타이포그래피는 Pretendard 폰트를 사용하며, -0.2sp letterSpacing이 적용되어 있습니다.**

**Headline (h):**
- `h1Bold` - 28sp, Bold, 38sp lineHeight
- `h1Medium` - 28sp, Medium, 38sp lineHeight
- `h2Bold` - 24sp, Bold, 34sp lineHeight (화면 타이틀용)
- `h2Medium` - 24sp, Medium, 34sp lineHeight
- `h2Regular` - 24sp, Regular, 34sp lineHeight
- `h3Bold` - 32sp, Bold, 42sp lineHeight
- `h4Bold` - 40sp, Bold, 50sp lineHeight

**Title (t):**
- `t1Bold` - 20sp, Bold, 28sp lineHeight
- `t1Medium` - 20sp, Medium, 28sp lineHeight
- `t1Regular` - 20sp, Regular, 28sp lineHeight
- `t2Bold` - 18sp, Bold, 26sp lineHeight
- `t2Medium` - 18sp, Medium, 26sp lineHeight
- `t2Regular` - 18sp, Regular, 26sp lineHeight

**Body (b):**
- `b1Bold` - 16sp, Bold, 24sp lineHeight (버튼 텍스트용)
- `b1Medium` - 16sp, Medium, 24sp lineHeight
- `b1Regular` - 16sp, Regular, 24sp lineHeight (본문용)
- `b2Bold` - 14sp, Bold, 21sp lineHeight
- `b2Medium` - 14sp, Medium, 21sp lineHeight
- `b2Regular` - 14sp, Regular, 21sp lineHeight

**Caption (c):**
- `c1Bold` - 12sp, Bold, 18sp lineHeight
- `c1Medium` - 12sp, Medium, 18sp lineHeight
- `c1Regular` - 12sp, Regular, 18sp lineHeight (작은 텍스트, 에러 메시지용)

**사용 예시:**
```kotlin
Text(
    text = "환영합니다!",
    style = SixpackTheme.typography.h2Bold  // 화면 타이틀
)

Text(
    text = "본문 텍스트입니다.",
    style = SixpackTheme.typography.b1Regular
)

Text(
    text = "에러 메시지",
    style = SixpackTheme.typography.c1Regular,
    color = SixpackTheme.colors.red
)
```

### Shapes (`SixpackTheme.shapes`)

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

### Dimensions (`SixPackDimen`)

- `SixPackDimen.defaultSideMargin` - 20.dp (기본 좌우 여백)

**사용 예시:**
```kotlin
Column(
    modifier = Modifier.padding(horizontal = SixPackDimen.defaultSideMargin)
)
```

## 4-2. Common Components (재사용 컴포넌트)

**IMPORTANT**: 새로운 화면을 만들 때 항상 이 컴포넌트들을 먼저 확인하고 재사용하세요. 동일한 기능을 중복 구현하지 마세요.

### Buttons

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
    text = "다음",
    onClick = { /* ... */ },
    enabled = isValid,
    modifier = Modifier.fillMaxWidth()
)

// 커스텀 색상
DoRunDefaultButton(
    text = "삭제",
    onClick = { /* ... */ },
    containerColor = SixpackTheme.colors.red,
    modifier = Modifier.fillMaxWidth()
)
```

### Top Bars

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
    navigateToBack = onNavigateBack
)

// 타이틀 포함
DoRunNavigationTopBar(
    navigateToBack = onNavigateBack,
    titleContent = {
        Text(
            text = "회원가입",
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
        IconButton(onClick = onBack) {
            Icon(...)
        }
    },
    content = {
        Text("타이틀")
    },
    trailingContent = {
        IconButton(onClick = onSettings) {
            Icon(...)
        }
    }
)
```

### Preview Wrapper

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

### Permission Handling

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

## 4-3. Utility Functions

### Format Extensions

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

### Modifier Extensions

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
            // 클릭 처리
        }
)
```

## 4-4. 재사용 체크리스트

새로운 화면을 만들기 전에 아래 체크리스트를 확인하세요:

**Theme:**
- [ ] 모든 색상은 `SixpackTheme.colors`에서 사용
- [ ] 모든 텍스트 스타일은 `SixpackTheme.typography`에서 사용
- [ ] 모든 Shape는 `SixpackTheme.shapes`에서 사용
- [ ] 좌우 여백은 `SixPackDimen.defaultSideMargin` 사용

**Components:**
- [ ] 버튼이 필요하면 `DoRunDefaultButton` 사용
- [ ] 뒤로가기가 필요하면 `DoRunNavigationTopBar` 사용
- [ ] 복잡한 TopBar가 필요하면 `DoRunTopBarSlot` 사용
- [ ] @Preview에서는 `DoRunPreviewWrapper` 사용
- [ ] 권한이 필요하면 `PermissionHandler` 사용

**Utilities:**
- [ ] 시간 포맷이 필요하면 `formatSecondsToTime` 사용
- [ ] 페이스 포맷이 필요하면 `formatPace` 사용
- [ ] 리플 없는 클릭이 필요하면 `noRippleClickable` 사용

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
