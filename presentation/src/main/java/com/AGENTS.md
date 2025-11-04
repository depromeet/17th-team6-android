# Presentation Module - AI Agent Guidelines

This document provides guidance for AI agents (LLMs) when writing code for the presentation layer of the SixPack Android application.

## Overview

The presentation module implements the **UI layer** using Jetpack Compose and **MVI pattern** with Orbit library. It handles user interactions, displays data, and manages navigation between screens.

**Base Package**: `com.dpm.sixpack.presentation`

## Package Structure

```
presentation/
├── common/                        # Shared components and utilities
│   ├── base/                     # Base classes (BaseViewModel, UiState, etc.)
│   ├── components/               # Reusable UI components
│   │   ├── auth/                # Authentication-related components
│   │   ├── bottomsheet/         # Bottom sheets
│   │   ├── dialog/              # Dialogs
│   │   ├── image/               # Image components
│   │   ├── post/                # Post/Feed components
│   │   ├── textfield/           # Text input components
│   │   └── topbar/              # Top app bars
│   ├── model/                    # UI models
│   └── util/                     # Utilities and extensions
├── destinations/                  # Type-safe navigation routes
├── navigation/                    # Bottom navigation and tabs
├── routes/                        # Feature screens
│   └── [feature]/
│       ├── [Feature]Route.kt     # Route composable (entry point)
│       ├── [Feature]ViewModel.kt # ViewModel with Orbit MVI
│       ├── contract/             # MVI contracts
│       │   ├── [Feature]State.kt
│       │   ├── [Feature]Intent.kt
│       │   └── [Feature]SideEffect.kt
│       ├── navigation/           # Feature navigation logic
│       └── ui/                   # UI components and screens
│           ├── screen/
│           └── component/
└── theme/                         # Material theme configuration
```

## Core Patterns

### 1. MVI Pattern with Orbit

**ALWAYS** use Orbit MVI pattern for all screens. The pattern consists of:

#### BaseViewModel Structure

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

#### Contract Files

**ALWAYS** create three contract files in `contract/` package:

**1. State (`[Feature]State.kt`)**:
```kotlin
data class FeatureState(
    val isLoading: Boolean = false,
    val data: DataType? = null,
    val errorMessage: String? = null,
    // Add computed properties if needed
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

#### ViewModel Implementation

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

**Key principles**:
- Use `intent { }` block for all state mutations
- Use `reduce { }` to update state immutably
- Use `postSideEffect()` for one-time events (navigation, toasts, etc.)
- Handle all Intent types in `onIntent()` with pattern matching
- All intent handlers should be `private` functions
- Use `savedStateHandle` in container for process death survival

### 2. Screen Structure Pattern

#### Route Composable (Entry Point)

**File**: `routes/[feature]/[Feature]Route.kt`

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
                    // Show toast or snackbar
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

**Key principles**:
- Route is the entry point and handles navigation
- Use `hiltViewModel()` to inject ViewModel
- Collect state with `collectAsState()`
- Collect side effects in `LaunchedEffect(Unit)`
- Pass navigation callbacks as parameters
- Pass state and `onIntent` to Screen composable

#### Screen Composable

**File**: `routes/[feature]/ui/screen/[Feature]Screen.kt`

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
            DoRunTopBar(
                title = stringResource(R.string.feature_title),
                onBackClick = { onIntent(FeatureIntent.OnBackButtonClick) },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            // Screen content
            if (state.isLoading) {
                CircularProgressIndicator()
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

**Key principles**:
- Screen is a stateless UI component
- Use `internal` visibility for Screen composables
- Accept `state` and `onIntent` as parameters
- Use `Scaffold` for screen structure
- Provide preview with `DoRunPreviewWrapper`
- Use `stringResource()` for all text

### 3. Navigation Pattern

#### Type-Safe Navigation Routes

**File**: `destinations/[Feature]Route.kt`

```kotlin
@Serializable
data object FeatureRoute

// Or with parameters
@Serializable
data class FeatureRoute(
    val userId: Long,
    val sessionId: String,
)
```

#### Navigation Functions

**File**: `routes/[feature]/navigation/[Feature]Navigation.kt`

```kotlin
fun NavController.navigateToFeature(navOptions: NavOptions? = null) {
    navigate(FeatureRoute, navOptions)
}

// Or with parameters
fun NavController.navigateToFeature(
    userId: Long,
    sessionId: String,
    navOptions: NavOptions? = null,
) {
    navigate(FeatureRoute(userId = userId, sessionId = sessionId), navOptions)
}
```

#### NavHost Integration

```kotlin
NavHost(navController = navController, startDestination = StartRoute) {
    composable<FeatureRoute> {
        FeatureRoute(
            onNavigateBack = { navController.navigateUp() },
            onNavigateToNext = { navController.navigateToNextScreen() },
        )
    }
}
```

### 4. Common UI Components

#### DoRunDefaultButton

```kotlin
DoRunDefaultButton(
    text = stringResource(R.string.button_text),
    onClick = { onIntent(SomeIntent.OnButtonClick) },
    enabled = true,
    modifier = Modifier.fillMaxWidth(),
)
```

#### DoRunDialog

```kotlin
if (showDialog) {
    DoRunDialog(
        title = stringResource(R.string.dialog_title),
        message = stringResource(R.string.dialog_message),
        confirmText = stringResource(R.string.dialog_confirm),
        dismissText = stringResource(R.string.dialog_dismiss),
        onConfirm = { onIntent(SomeIntent.OnConfirm) },
        onDismiss = { onIntent(SomeIntent.OnDismiss) },
    )
}
```

#### DoRunTitleTopBar

```kotlin
DoRunTitleTopBar(
    title = stringResource(R.string.screen_title),
    onBackClick = { onIntent(SomeIntent.OnBackButtonClick) },
)
```

### 5. String Resources

**ALWAYS** use string resources for all user-facing text. **NEVER** hardcode strings.

**File**: `presentation/src/main/res/values/strings.xml`

```xml
<resources>
    <!-- Screen titles -->
    <string name="feature_screen_title">Screen Title</string>

    <!-- Buttons -->
    <string name="feature_button_confirm">확인</string>
    <string name="feature_button_cancel">취소</string>

    <!-- Messages -->
    <string name="feature_message_success">Success message</string>
    <string name="feature_message_error">Error message</string>

    <!-- Placeholders -->
    <string name="feature_input_placeholder">Enter text here</string>
</resources>
```

**Usage**:
```kotlin
Text(text = stringResource(R.string.feature_screen_title))
```

### 6. Error Handling Pattern

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

### 7. Loading State Pattern

```kotlin
// In State
data class FeatureState(
    val isLoading: Boolean = false,
    val isButtonLoading: Boolean = false,
)

// In Screen
if (state.isLoading) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
} else {
    // Content
}

// Or for button loading
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

### 8. Preview Pattern

**ALWAYS** provide previews for composables, including reusable components.

#### Screen Previews

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
```

#### Component Previews

**ALWAYS** provide previews for reusable components in the `ui/component/` package:

**Key principles**:
- Component previews should be `private` (components themselves are `internal`)
- Use `DoRunPreviewWrapper` for theme support
- Provide multiple previews for components with different states
- Use realistic mock data that represents actual usage
- Include edge cases (empty, loading, error states)

**Example 1: Simple Component**

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

**Example 2: Component with Multiple States**

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

**Example 3: Component with Paging Data**

For components that accept `LazyPagingItems`, use `flowOf(PagingData.from(...)).collectAsLazyPagingItems()`:

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

**Example 4: Interactive Component States**

```kotlin
@Composable
internal fun MyPageTabText(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    // Component implementation
}

// Preview for selected state
@Preview
@Composable
private fun MyPageTabTextSelectedPreview() {
    DoRunPreviewWrapper {
        MyPageTabText(text = "인증", isSelected = true)
    }
}

// Preview for unselected state
@Preview
@Composable
private fun MyPageTabTextUnselectedPreview() {
    DoRunPreviewWrapper {
        MyPageTabText(text = "기록", isSelected = false)
    }
}
```

## Common Patterns

### 1. Phone Number Input with SMS Verification

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

### 2. Input Validation

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

### 3. Throttling/Debouncing Clicks

Use `throttleClick` modifier for preventing double clicks:

```kotlin
DoRunDefaultButton(
    text = stringResource(R.string.button_text),
    onClick = throttleClick {
        onIntent(SomeIntent.OnButtonClick)
    },
)
```

## Theme and Styling

### Theme Usage

```kotlin
// Typography
Text(
    text = "Title",
    style = MaterialTheme.typography.headlineMedium,
)

// Colors
Box(
    modifier = Modifier.background(MaterialTheme.colorScheme.primary)
)

// Dimensions (use SixPackDimen for custom values)
Spacer(modifier = Modifier.height(SixPackDimen.spacing16))
```

### Custom Dimensions

Access dimensions from `SixPackDimen`:

```kotlin
object SixPackDimen {
    val spacing4 = 4.dp
    val spacing8 = 8.dp
    val spacing16 = 16.dp
    val spacing24 = 24.dp
    // etc.
}
```

## Testing Considerations

Preview composables extensively for:
- Light and dark mode
- Different screen sizes
- Different states (loading, error, success)
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
```

## Common Mistakes to Avoid

### ❌ DON'T: Hardcode strings

```kotlin
Text(text = "Sign In")
```

### ✅ DO: Use string resources

```kotlin
Text(text = stringResource(R.string.sign_in_title))
```

### ❌ DON'T: Call use cases directly in composables

```kotlin
@Composable
fun Screen(useCase: SomeUseCase) {
    Button(onClick = { useCase.invoke() }) { }
}
```

### ✅ DO: Call through ViewModel via Intents

```kotlin
@Composable
fun Screen(onIntent: (Intent) -> Unit) {
    Button(onClick = { onIntent(SomeIntent.OnButtonClick) }) { }
}
```

### ❌ DON'T: Mutate state directly

```kotlin
fun handleClick() {
    state = state.copy(isLoading = true) // Error!
}
```

### ✅ DO: Use intent and reduce

```kotlin
private fun handleClick() = intent {
    reduce { state.copy(isLoading = true) }
}
```

### ❌ DON'T: Use navigation in ViewModel

```kotlin
private fun handleClick() = intent {
    navController.navigate(NextRoute) // Error!
}
```

### ✅ DO: Use SideEffects for navigation

```kotlin
private fun handleClick() = intent {
    postSideEffect(FeatureSideEffect.NavigateToNext)
}
```

## File Naming Conventions

- **Route**: `[Feature]Route.kt` (e.g., `SignInRoute.kt`)
- **ViewModel**: `[Feature]ViewModel.kt` (e.g., `SignInViewModel.kt`)
- **State**: `[Feature]State.kt` (e.g., `SignInState.kt`)
- **Intent**: `[Feature]Intent.kt` (e.g., `SignInIntent.kt`)
- **SideEffect**: `[Feature]SideEffect.kt` (e.g., `SignInSideEffect.kt`)
- **Screen**: `[Feature]Screen.kt` (e.g., `SignInScreen.kt`)
- **Navigation**: `[Feature]Navigation.kt` (e.g., `SignInNavigation.kt`)
- **Components**: `[Component]Component.kt` or descriptive names

## Summary Checklist

When creating new presentation code, verify:

- [ ] Uses MVI pattern with Orbit (State, Intent, SideEffect)
- [ ] ViewModel extends `BaseViewModel` with correct generics
- [ ] Uses `@HiltViewModel` and constructor injection
- [ ] State is immutable data class implementing `UiState`
- [ ] Intent is sealed interface implementing `UiIntent`
- [ ] SideEffect is sealed interface implementing `SideEffect`
- [ ] All state mutations use `intent { reduce { } }`
- [ ] One-time events use `postSideEffect()`
- [ ] Route composable collects state and side effects
- [ ] Screen composable is stateless and internal
- [ ] All strings use `stringResource()`
- [ ] Navigation uses type-safe routes with `@Serializable`
- [ ] Previews provided with `DoRunPreviewWrapper`
- [ ] Proper error handling with pattern matching
- [ ] Loading states handled appropriately
