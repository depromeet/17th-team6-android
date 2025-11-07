# App Module - AI Agent Guidelines

This document provides guidance for AI agents (LLMs) when working with the app module of the SixPack Android application.

## Overview

The app module serves as the **application entry point** and orchestrates the overall application structure. It contains the Application class, MainActivity, main navigation setup, and Hilt integration.

**Base Package**: `com.dpm.sixpack`

## Package Structure

```
app/
├── src/main/
│   ├── java/com/dpm/sixpack/
│   │   ├── SixPackApp.kt          # Application class with Hilt
│   │   ├── SixPackAppState.kt     # App-level state management
│   │   └── main/
│   │       ├── MainActivity.kt     # Single activity
│   │       ├── MainScreen.kt       # Main screen composable
│   │       ├── MainViewModel.kt    # App-level ViewModel
│   │       └── navigation/
│   │           ├── MainNavHost.kt  # Root navigation graph
│   │           └── MainNavigator.kt # Navigation coordinator
│   ├── res/                        # Resources
│   └── AndroidManifest.xml
└── build.gradle.kts
```

## Core Responsibilities

### 1. Application Class (`SixPackApp.kt`)

The Application class is annotated with `@HiltAndroidApp` and handles:

- **Hilt initialization** for dependency injection
- **Timber initialization** for logging (DEBUG builds only)
- **Coil ImageLoader** singleton setup
- **App-level initializers** (Naver Map SDK, etc.)
- **Dark mode configuration**

```kotlin
@HiltAndroidApp
class SixPackApp :
    Application(),
    SingletonImageLoader.Factory {

    @Inject
    lateinit var initializers: AppInitializer

    @Inject
    lateinit var imageLoader: dagger.Lazy<ImageLoader>

    override fun onCreate() {
        super.onCreate()

        initializers.onAppCreate(this)

        setDarkMode()
        initTimber()
    }

    private fun setDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader = imageLoader.get()
}
```

**Key principles**:
- Use `@HiltAndroidApp` annotation (required for Hilt)
- Inject initializers with `@Inject lateinit var`
- Initialize third-party SDKs in `onCreate()`
- Use `BuildConfig.DEBUG` for debug-only features
- Implement `SingletonImageLoader.Factory` for Coil

### 2. MainActivity (`MainActivity.kt`)

The app uses a **single activity architecture** with Jetpack Compose.

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            SixPackTheme {
                MainScreen()
            }
        }
    }
}
```

**Key principles**:
- Use `@AndroidEntryPoint` annotation (required for Hilt)
- Extend `ComponentActivity` for Compose
- Use `enableEdgeToEdge()` for edge-to-edge UI
- Set content with `SixPackTheme` wrapper
- Keep MainActivity minimal - all logic in ViewModels

### 3. Main Navigation (`MainNavHost.kt`)

The root navigation graph manages all top-level destinations.

```kotlin
@Composable
fun MainNavHost(
    navController: NavHostController,
    startDestination: Any,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        // Onboarding flow
        composable<OnboardingRoute> {
            OnboardingRoute(
                onNavigateToSignIn = { navController.navigateToSignIn() },
                onNavigateToSignUp = { navController.navigateToSignUp() },
            )
        }

        // Auth flow
        composable<SignInRoute> {
            SignInRoute(
                onNavigateBack = { navController.navigateUp() },
                onNavigateToHome = {
                    navController.navigate(MainRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }

        // Main tabs
        composable<MainRoute> {
            MainRoute()
        }

        // Other destinations...
    }
}
```

**Key principles**:
- Use type-safe navigation with `@Serializable` routes
- Define all top-level destinations
- Pass navigation lambdas to child composables
- Use `popUpTo()` for clearing back stack when needed
- Keep navigation logic in NavHost, not in destinations

### 4. Main ViewModel (`MainViewModel.kt`)

Manages app-level state (e.g., login status, initial route determination).

```kotlin
@HiltViewModel
class MainViewModel @Inject constructor(
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase,
) : ViewModel() {

    private val _startDestination = MutableStateFlow<Any?>(null)
    val startDestination: StateFlow<Any?> = _startDestination.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            isUserLoggedInUseCase()
                .onSuccess { isLoggedIn ->
                    _startDestination.value = if (isLoggedIn) {
                        MainRoute
                    } else {
                        OnboardingRoute
                    }
                }
                .onError {
                    _startDestination.value = OnboardingRoute
                }
        }
    }
}
```

**Key principles**:
- Use `@HiltViewModel` for Hilt injection
- Manage app-level state (not screen-specific)
- Determine initial navigation destination
- Use StateFlow for reactive state
- Call use cases in `init` or functions

## Common Patterns

### 1. Adding a New Top-Level Destination

**Step 1**: Define route in `presentation/destinations/`

```kotlin
@Serializable
data object NewFeatureRoute
```

**Step 2**: Add to `MainNavHost.kt`

```kotlin
composable<NewFeatureRoute> {
    NewFeatureRoute(
        onNavigateBack = { navController.navigateUp() },
    )
}
```

**Step 3**: Create navigation extension in feature module

```kotlin
fun NavController.navigateToNewFeature(navOptions: NavOptions? = null) {
    navigate(NewFeatureRoute, navOptions)
}
```

### 2. Edge-to-Edge UI Setup

The app uses edge-to-edge display. When creating screens:

```kotlin
Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = { TopAppBar(...) },
) { paddingValues ->
    Content(
        modifier = Modifier.padding(paddingValues) // Important!
    )
}
```

**Always** apply `paddingValues` to avoid content being drawn behind system bars.

### 3. App Initializer Pattern

For initializing SDKs that require application context:

```kotlin
// In presentation or core module
class SdkInitializer @Inject constructor() : AppInitializer {
    override fun onAppCreate(application: Application) {
        SomeSDK.init(application, "API_KEY")
    }
}

// In app module (injected automatically by Hilt)
@HiltAndroidApp
class SixPackApp : Application() {
    @Inject
    lateinit var initializers: AppInitializer

    override fun onCreate() {
        super.onCreate()
        initializers.onAppCreate(this)
    }
}
```

### 4. Handling Deep Links

Add intent filters in `AndroidManifest.xml`:

```xml
<activity android:name=".main.MainActivity">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="sixpack" android:host="feature" />
    </intent-filter>
</activity>
```

Handle in `MainActivity`:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val deepLinkUri = intent?.data
    // Pass to MainScreen or ViewModel
}
```

## Build Configuration

### AndroidManifest.xml

```xml
<manifest>
    <application
        android:name=".SixPackApp"
        android:allowBackup="false"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/Theme.SixPack">

        <activity
            android:name=".main.MainActivity"
            android:exported="true"
            android:theme="@style/Theme.SixPack">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!-- Services -->
        <service
            android:name="com.dpm.sixpack.background.RunningService"
            android:foregroundServiceType="location"
            android:exported="false" />

    </application>

    <!-- Permissions -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION" />

</manifest>
```

### build.gradle.kts

```kotlin
plugins {
    alias(libs.plugins.sixpack.android.application)
    alias(libs.plugins.sixpack.hilt)
}

android {
    namespace = "com.dpm.sixpack"

    defaultConfig {
        applicationId = "com.dpm.sixpack"
        versionCode = 1
        versionName = "1.0.0"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(projects.presentation)
    implementation(projects.domain)
    implementation(projects.data)
    implementation(projects.core)
    implementation(projects.background)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Timber
    implementation(libs.timber)

    // Coil
    implementation(libs.coil.compose)
}
```

**Key principles**:
- Use convention plugins (`sixpack.android.application`, `sixpack.hilt`)
- Depend on all feature modules
- Use version catalog for dependencies
- Enable Compose and BuildConfig

## Testing Considerations

The app module typically has minimal logic, but you should test:

- **MainViewModel** navigation logic
- **Deep link handling**
- **App initialization** (if complex)

Example:

```kotlin
@Test
fun `when user is logged in then start destination is MainRoute`() = runTest {
    // Given
    val mockUseCase = mock<IsUserLoggedInUseCase>()
    whenever(mockUseCase.invoke()).thenReturn(DoRunResult.Success(true))

    // When
    val viewModel = MainViewModel(mockUseCase)

    // Then
    advanceUntilIdle()
    assertEquals(MainRoute, viewModel.startDestination.value)
}
```

## Common Mistakes to Avoid

### ❌ DON'T: Put business logic in MainActivity or Application

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bad: Business logic in Activity
        val user = database.getUser()
        if (user.isLoggedIn) {
            startMainScreen()
        }
    }
}
```

### ✅ DO: Use ViewModels for logic

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            // ViewModel handles logic
        }
    }
}
```

### ❌ DON'T: Create multiple activities

```kotlin
// Bad: Multiple activities
class SignInActivity : ComponentActivity()
class MainTabActivity : ComponentActivity()
```

### ✅ DO: Use single activity with Compose Navigation

```kotlin
// Good: Single activity with navigation
class MainActivity : ComponentActivity() {
    // NavHost handles all screens
}
```

### ❌ DON'T: Initialize SDKs directly in MainActivity

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NaverMapSDK.init(this, "KEY") // Bad
    }
}
```

### ✅ DO: Use Application class with initializers

```kotlin
@HiltAndroidApp
class SixPackApp : Application() {
    @Inject lateinit var initializers: AppInitializer

    override fun onCreate() {
        super.onCreate()
        initializers.onAppCreate(this)
    }
}
```

## Summary Checklist

When working with the app module, verify:

- [ ] Application class has `@HiltAndroidApp` annotation
- [ ] MainActivity has `@AndroidEntryPoint` annotation
- [ ] MainActivity extends `ComponentActivity`
- [ ] Single activity architecture is maintained
- [ ] All screens use Compose Navigation
- [ ] Type-safe navigation routes with `@Serializable`
- [ ] Edge-to-edge UI with proper padding
- [ ] SDK initialization in Application, not Activity
- [ ] Timber initialized for DEBUG builds only
- [ ] All modules properly included in dependencies
- [ ] Convention plugins used in build.gradle.kts
- [ ] Required permissions declared in AndroidManifest.xml
