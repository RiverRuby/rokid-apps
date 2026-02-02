# Architecture & Shared Patterns

## Overview

All apps share common patterns for UI, navigation, and networking. This document defines those patterns to ensure consistency across the portfolio.

## Shared Components Location

```
android/shared/
├── build.gradle.kts
└── src/main/java/com/rokid/shared/
    ├── ui/
    │   ├── theme/
    │   │   ├── GlassesTheme.kt
    │   │   ├── Color.kt
    │   │   └── Typography.kt
    │   └── components/
    │       ├── DpadNavigation.kt
    │       └── StatusBanner.kt
    └── util/
        └── VoiceCommandService.kt
```

## GlassesTheme

Standard theme for all apps targeting the RV101's green monochrome display.

### Font Assets

Copy these font files to each app's `assets/fonts/` folder:
- `jetbrains_mono_bold.ttf` - Primary UI font (monospace, tech aesthetic)
- `space_grotesk_bold.ttf` - Headers/accent font (geometric sans)

Source: `android/HelloHUD/app/src/main/assets/fonts/`

**Important:** Use `Typeface.createFromAsset()` to load fonts. The `res/font` resource approach has known issues with Jetpack Compose causing crashes on some devices.

```kotlin
object GlassesColors {
    val background = Color.Black
    val foreground = Color.White
    val accent = Color(0xFF00FF00)  // Green for emphasis
    val error = Color(0xFFFF6B6B)   // Soft red for errors
    val warning = Color(0xFFFFEB3B) // Yellow for warnings
    val dim = Color(0xFF666666)     // Dimmed text
}

object GlassesFonts {
    private var _primaryFont: FontFamily? = null
    private var _accentFont: FontFamily? = null

    val primary: FontFamily get() = _primaryFont ?: FontFamily.Monospace
    val accent: FontFamily get() = _accentFont ?: FontFamily.Default

    fun init(context: Context) {
        _primaryFont = loadFont(context, "fonts/jetbrains_mono_bold.ttf")
        _accentFont = loadFont(context, "fonts/space_grotesk_bold.ttf")
    }

    private fun loadFont(context: Context, path: String): FontFamily? {
        return try {
            val typeface = Typeface.createFromAsset(context.assets, path)
            FontFamily(androidx.compose.ui.text.font.Typeface(typeface))
        } catch (e: Exception) {
            Log.e("GlassesFonts", "Failed to load font: $path", e)
            null
        }
    }
}

object GlassesTypography {
    val displayLarge: TextStyle get() = TextStyle(
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = GlassesFonts.accent,  // Space Grotesk for headers
        color = GlassesColors.foreground
    )

    val headlineLarge: TextStyle get() = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.SemiBold,
        fontFamily = GlassesFonts.accent,
        color = GlassesColors.foreground
    )

    val bodyLarge: TextStyle get() = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Normal,
        fontFamily = GlassesFonts.primary,  // JetBrains Mono for body
        color = GlassesColors.foreground
    )

    val labelLarge: TextStyle get() = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        fontFamily = GlassesFonts.primary,
        color = GlassesColors.foreground
    )
}

@Composable
fun GlassesTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = GlassesColors.background,
            surface = GlassesColors.background,
            onBackground = GlassesColors.foreground,
            onSurface = GlassesColors.foreground,
            primary = GlassesColors.accent,
            error = GlassesColors.error
        ),
        typography = Typography(
            displayLarge = GlassesTypography.displayLarge,
            headlineLarge = GlassesTypography.headlineLarge,
            bodyLarge = GlassesTypography.bodyLarge,
            labelLarge = GlassesTypography.labelLarge
        ),
        content = content
    )
}
```

## DPAD Navigation

Modifier for handling touchpad/DPAD input on glasses.

```kotlin
fun Modifier.dpadNavigation(
    onUp: () -> Unit = {},
    onDown: () -> Unit = {},
    onLeft: () -> Unit = {},
    onRight: () -> Unit = {},
    onSelect: () -> Unit = {},
    onBack: () -> Unit = {}
): Modifier = this.onKeyEvent { event ->
    if (event.type != KeyEventType.KeyDown) return@onKeyEvent false

    when (event.key) {
        Key.DirectionUp -> { onUp(); true }
        Key.DirectionDown -> { onDown(); true }
        Key.DirectionLeft -> { onLeft(); true }
        Key.DirectionRight -> { onRight(); true }
        Key.Enter, Key.DirectionCenter -> { onSelect(); true }
        Key.Back, Key.Escape -> { onBack(); true }
        else -> false
    }
}
```

### Focus Management

```kotlin
@Composable
fun DpadFocusableList(
    items: List<Any>,
    selectedIndex: Int,
    onIndexChange: (Int) -> Unit,
    onSelect: (Int) -> Unit,
    itemContent: @Composable (item: Any, focused: Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .dpadNavigation(
                onUp = {
                    if (selectedIndex > 0) onIndexChange(selectedIndex - 1)
                },
                onDown = {
                    if (selectedIndex < items.lastIndex) onIndexChange(selectedIndex + 1)
                },
                onSelect = { onSelect(selectedIndex) }
            )
    ) {
        items.forEachIndexed { index, item ->
            itemContent(item, index == selectedIndex)
        }
    }
}
```

## Connection Status Banner

Reusable component for showing network status.

```kotlin
enum class ConnectionState {
    Connected,
    Connecting,
    Disconnected,
    Error
}

@Composable
fun ConnectionStatusBanner(
    state: ConnectionState,
    hostInfo: String = "",
    latencyMs: Int? = null
) {
    val (text, color) = when (state) {
        ConnectionState.Connected ->
            "● Connected${latencyMs?.let { " ${it}ms" } ?: ""}" to GlassesColors.accent
        ConnectionState.Connecting ->
            "○ Connecting..." to GlassesColors.warning
        ConnectionState.Disconnected ->
            "● Disconnected" to GlassesColors.dim
        ConnectionState.Error ->
            "● Error" to GlassesColors.error
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            style = GlassesTypography.labelLarge,
            color = color
        )
        if (hostInfo.isNotEmpty()) {
            Text(
                text = hostInfo,
                style = GlassesTypography.labelLarge,
                color = GlassesColors.dim
            )
        }
    }
}
```

## Voice Command Service

Interface for voice recognition across apps.

```kotlin
interface VoiceCommandService {
    val isListening: StateFlow<Boolean>
    val commands: Flow<String>

    fun startListening()
    fun stopListening()
}

class DefaultVoiceCommandService(
    private val context: Context
) : VoiceCommandService {

    private val _isListening = MutableStateFlow(false)
    override val isListening = _isListening.asStateFlow()

    private val _commands = MutableSharedFlow<String>()
    override val commands = _commands.asSharedFlow()

    private var speechRecognizer: SpeechRecognizer? = null

    override fun startListening() {
        if (_isListening.value) return

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onResults(results: Bundle?) {
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()
                        ?.let { command ->
                            _commands.tryEmit(command.lowercase())
                        }
                }
                // Implement other listener methods...
            })
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                     RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        }

        speechRecognizer?.startListening(intent)
        _isListening.value = true
    }

    override fun stopListening() {
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
        _isListening.value = false
    }
}
```

## WebSocket Client Pattern

Standard pattern for WebSocket connections with reconnection logic.

```kotlin
abstract class ReconnectingWebSocketClient(
    private val url: String,
    private val token: String
) {
    private val client = OkHttpClient.Builder()
        .pingInterval(15, TimeUnit.SECONDS)
        .build()

    private var webSocket: WebSocket? = null
    private var reconnectAttempts = 0
    private val maxBackoff = 30_000L

    protected val _connectionState = MutableStateFlow(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    fun connect() {
        _connectionState.value = ConnectionState.Connecting

        val request = Request.Builder()
            .url(url)
            .addHeader("X-Auth-Token", token)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                reconnectAttempts = 0
                _connectionState.value = ConnectionState.Connected
                onConnected()
            }

            override fun onMessage(ws: WebSocket, text: String) {
                onMessage(text)
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                _connectionState.value = ConnectionState.Disconnected
                scheduleReconnect()
            }

            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                _connectionState.value = ConnectionState.Disconnected
                if (code != 1000) scheduleReconnect()
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, "User disconnect")
        webSocket = null
    }

    protected fun send(message: String) {
        webSocket?.send(message)
    }

    private fun scheduleReconnect() {
        val delay = minOf(maxBackoff, 1000L * (1 shl reconnectAttempts))
        reconnectAttempts++

        // Use coroutine or handler to delay reconnect
        // After delay: connect()
    }

    protected abstract fun onConnected()
    protected abstract fun onMessage(text: String)
}
```

## Data Storage Pattern

Use DataStore for configuration and credentials.

```kotlin
class AppSettings(private val context: Context) {

    private val dataStore = context.dataStore

    companion object {
        private val Context.dataStore by preferencesDataStore(name = "settings")

        private val API_KEY = stringPreferencesKey("api_key")
        private val REFRESH_INTERVAL = intPreferencesKey("refresh_interval")
    }

    val apiKey: Flow<String?> = dataStore.data.map { it[API_KEY] }
    val refreshInterval: Flow<Int> = dataStore.data.map { it[REFRESH_INTERVAL] ?: 60 }

    suspend fun setApiKey(key: String) {
        dataStore.edit { it[API_KEY] = key }
    }

    suspend fun setRefreshInterval(seconds: Int) {
        dataStore.edit { it[REFRESH_INTERVAL] = seconds }
    }
}
```

## Error Handling Pattern

Standard approach for handling and displaying errors.

```kotlin
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val retry: (() -> Unit)? = null) : UiState<Nothing>()
}

@Composable
fun <T> UiStateContainer(
    state: UiState<T>,
    onRetry: () -> Unit = {},
    content: @Composable (T) -> Unit
) {
    when (state) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Loading...",
                    style = GlassesTypography.bodyLarge
                )
            }
        }

        is UiState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = state.message,
                    style = GlassesTypography.bodyLarge,
                    color = GlassesColors.error,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }

        is UiState.Success -> {
            content(state.data)
        }
    }
}
```

## Build Configuration

Standard build.gradle.kts setup for apps.

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.rokid.<appname>"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.rokid.<appname>"
        minSdk = 31  // Adjust based on Phase 0 findings
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

dependencies {
    // Shared module
    implementation(project(":shared"))

    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // Activity & Lifecycle
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Networking (if needed)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")
}
```

## Testing Patterns

### Unit Tests

```kotlin
class NowCardViewModelTest {

    @Test
    fun `initial state is loading`() = runTest {
        val viewModel = NowCardViewModel(FakeTaskSource())
        assertEquals(UiState.Loading, viewModel.state.value)
    }

    @Test
    fun `displays task after fetch`() = runTest {
        val task = Task("1", "Test task", "Description")
        val viewModel = NowCardViewModel(FakeTaskSource(task))

        advanceUntilIdle()

        assertEquals(UiState.Success(task), viewModel.state.value)
    }
}
```

### UI Tests

```kotlin
@Test
fun dpad_navigation_changes_focus() {
    composeTestRule.setContent {
        var selected by remember { mutableStateOf(0) }
        DpadFocusableList(
            items = listOf("A", "B", "C"),
            selectedIndex = selected,
            onIndexChange = { selected = it },
            onSelect = {},
            itemContent = { item, focused ->
                Text(item.toString(), color = if (focused) Color.Green else Color.White)
            }
        )
    }

    // Simulate down press
    composeTestRule.onRoot().performKeyInput { keyDown(Key.DirectionDown) }

    // Verify focus moved
    // ...
}
```
