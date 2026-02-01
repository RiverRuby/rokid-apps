# App Specifications

Detailed specifications for each app in the Rokid Apps Portfolio.

## Table of Contents

1. [HelloHUD (Phase 0)](#hellohud-phase-0)
2. [NowCard](#nowcard)
3. [ARPet](#arpet)
4. [Capture](#capture)
5. [SpeedReader](#speedreader)
6. [Meeting Overlay](#meeting-overlay)
7. [AgentHUD](#agenthud)

---

## HelloHUD (Phase 0)

### Purpose
Minimal test app to validate the development toolchain and device deployment.

### Requirements
- Single Activity, fullscreen, black background
- Large white text: "HELLO RV101"
- Log "tick" every 2 seconds to verify app is running

### Success Criteria
- App installs via `./tools/install.sh HelloHUD`
- App launches without crash
- Text is visible and readable on glasses
- "tick" appears in logcat every 2 seconds

### Implementation
```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start tick logging
        lifecycleScope.launch {
            while (true) {
                Log.d("HelloHUD", "tick")
                delay(2000)
            }
        }

        setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "HELLO RV101",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
```

---

## NowCard

### Purpose
Display current task from Sunsama/Notion with optional Pomodoro timer. Glanceable "what should I be doing right now?" display.

### Data Sources
- **Sunsama**: API to get current/next task
- **Notion**: Database query for today's focused task

### UI Design
```
┌─────────────────────────────────────┐
│           NOW                       │
├─────────────────────────────────────┤
│                                     │
│    Write implementation plan        │
│    for Rokid apps                   │
│                                     │
│         ⏱ 18:42                     │
│                                     │
├─────────────────────────────────────┤
│  [DONE]    [SKIP]    [+5 MIN]      │
└─────────────────────────────────────┘
```

### Features
- Pull current task from Sunsama or Notion
- Pomodoro timer (25 min default)
- Actions: Mark done, Skip to next, Extend time
- Auto-refresh every 60 seconds
- Show "Break time!" between pomodoros

### Technical Architecture
```kotlin
// Data layer
interface TaskSource {
    suspend fun getCurrentTask(): Task?
    suspend fun markDone(taskId: String)
    suspend fun skipTask(taskId: String)
}

class SunsamaTaskSource(apiKey: String) : TaskSource
class NotionTaskSource(apiKey: String, databaseId: String) : TaskSource

// ViewModel
class NowCardViewModel(taskSource: TaskSource) : ViewModel() {
    val currentTask: StateFlow<Task?>
    val timerState: StateFlow<TimerState>

    fun startPomodoro()
    fun markDone()
    fun skip()
    fun extendTime(minutes: Int)
}
```

### Configuration
- Store API keys in encrypted DataStore
- Settings screen to configure Sunsama vs Notion
- Pomodoro duration setting (default 25 min)

---

## ARPet

### Purpose
Pixel art virtual pet that reflects habit completion. Pet thrives when habits are done, gets sad/sick when neglected.

### Data Source
- Custom PostgreSQL database for habit tracking
- Voice commands to log habits

### Pet States
| State | Trigger | Visual |
|-------|---------|--------|
| HAPPY | All habits done | ^_^ with hearts |
| CONTENT | Most habits done | ^_^ normal |
| HUNGRY | Missing meals/water | >_< with empty stomach |
| TIRED | Missing sleep/rest | -_- droopy eyes |
| SAD | Multiple habits missed | ;_; tears |
| SICK | Extended neglect | x_x with swirls |

### UI Design
```
┌─────────────────────────────────────┐
│        🎮 PIXEL PET                 │
├─────────────────────────────────────┤
│                                     │
│          ╔═══════╗                  │
│          ║  ^_^  ║   HAPPY          │
│          ║ /|||\ ║   ♥♥♥♥♡          │
│          ╚═══════╝                  │
│                                     │
│    Habits: 4/5 done                 │
│    Streak: 12 days                  │
│                                     │
├─────────────────────────────────────┤
│  Say "Log water" or "Log workout"  │
└─────────────────────────────────────┘
```

### Features
- Simple pixel art pet (8x8 or 16x16 sprites)
- Animate between states
- Voice command recognition for logging habits
- Pull habit status from PostgreSQL database
- Show streak count
- Optional: Pet "talks" with tips/encouragement

### Voice Commands
| Command | Action |
|---------|--------|
| "Log water" | Mark water habit done |
| "Log workout" | Mark exercise habit done |
| "Log meal" | Mark meal habit done |
| "How am I doing?" | Summarize today's habits |
| "Pet status" | Show detailed pet stats |

### Technical Architecture
```kotlin
// Pixel art rendering
class PetRenderer {
    fun drawPet(canvas: Canvas, state: PetState, frame: Int)
}

// Habit integration
interface HabitTracker {
    suspend fun getHabitsForToday(): List<Habit>
    suspend fun markHabitDone(habitId: String)
}

class PostgresHabitTracker(connectionString: String) : HabitTracker

// Voice commands
class VoiceCommandHandler {
    fun startListening()
    fun onCommand(command: String): HabitAction?
}
```

---

## Capture

### Purpose
Quick camera capture for documenting food eaten and fashion items seen.

### Storage Options
- **Notion DB**: Upload photo + metadata
- **Local folder**: Save to device, sync later

### UI Design
```
┌─────────────────────────────────────┐
│        📸 CAPTURE                   │
├─────────────────────────────────────┤
│                                     │
│    ┌─────────────────────┐          │
│    │                     │          │
│    │   [Camera Preview]  │          │
│    │                     │          │
│    └─────────────────────┘          │
│                                     │
├─────────────────────────────────────┤
│  [🍕 FOOD]    [👗 FASHION]         │
└─────────────────────────────────────┘
```

### Post-Capture Screen
```
┌─────────────────────────────────────┐
│        ✓ SAVED                      │
├─────────────────────────────────────┤
│                                     │
│    [Thumbnail]                      │
│                                     │
│    Type: Food                       │
│    Time: 12:34 PM                   │
│    Location: San Francisco          │
│                                     │
│    Add note? (voice or skip)        │
│                                     │
└─────────────────────────────────────┘
```

### Features
- Quick capture with one button
- Tag as Food or Fashion
- Auto-add timestamp and location
- Optional voice note
- Upload to Notion or save locally
- Gallery view of recent captures

### Technical Architecture
```kotlin
class CaptureManager {
    fun takePhoto(): Bitmap
    fun saveToNotion(photo: Bitmap, metadata: CaptureMetadata)
    fun saveToLocal(photo: Bitmap, metadata: CaptureMetadata)
}

data class CaptureMetadata(
    val type: CaptureType, // FOOD or FASHION
    val timestamp: Long,
    val location: Location?,
    val note: String?
)

enum class CaptureType { FOOD, FASHION }
```

---

## SpeedReader

### Purpose
RSVP (Rapid Serial Visual Presentation) style speed reading for articles.

### Input Methods
1. **Share sheet**: Share from browser/app
2. **URL fetch**: Enter URL, extract article content
3. **Clipboard paste**: Copy text on phone, appears on glasses

### UI Design
```
┌─────────────────────────────────────┐
│  Speed Reader          ⏸ 350 WPM   │
├─────────────────────────────────────┤
│                                     │
│                                     │
│                                     │
│           implementing              │
│                                     │
│                                     │
│                                     │
├─────────────────────────────────────┤
│  Progress: ████████░░░░░ 67%        │
│  [◀◀]  [⏸]  [▶▶]  [⚙]              │
└─────────────────────────────────────┘
```

### Features
- RSVP display with ORP (Optimal Recognition Point) highlighting
- Adjustable speed (100-800 WPM)
- Pause/resume with DPAD
- Skip forward/backward by sentence
- Progress indicator
- Article extraction from URLs (Readability algorithm)
- Queue multiple articles

### Technical Architecture
```kotlin
class ArticleExtractor {
    suspend fun extractFromUrl(url: String): Article
    fun extractFromHtml(html: String): Article
}

class SpeedReaderEngine {
    val currentWord: StateFlow<Word>
    val progress: StateFlow<Float>

    fun setSpeed(wpm: Int)
    fun play()
    fun pause()
    fun skipForward()
    fun skipBackward()
}

data class Word(
    val text: String,
    val orpIndex: Int  // Optimal Recognition Point
)
```

---

## Meeting Overlay

### Purpose
Live transcript overlay during meetings.

### Features (Conceptual)
- Connect to meeting transcript service (Otter.ai, Fireflies, etc.)
- Show current speaker
- Highlight key points and action items
- Searchable history

### Status
**Deferred** - Higher complexity, build after core apps are stable.

---

## AgentHUD

### Purpose
Display real-time status of coding agents with quick approval/denial actions.

### Architecture Overview
```
┌─────────────┐     WebSocket      ┌──────────────┐
│  AgentHUD   │◄──────────────────►│ agent-router │
│  (Android)  │     HTTP POST      │  (Node.js)   │
└─────────────┘────────────────────►└──────────────┘
                                          │
                                          ▼
                                   ┌──────────────┐
                                   │ Real Agents  │
                                   │ (future)     │
                                   └──────────────┘
```

### Overview Screen
```
┌─────────────────────────────────────┐
│ AGENTS          ● Connected  12ms   │
├─────────────────────────────────────┤
│ ▶ Frontend      RUNNING             │
│   Implementing auth flow...         │
├─────────────────────────────────────┤
│   Backend       WAITING_APPROVAL    │
│   Delete user table?                │
├─────────────────────────────────────┤
│   Tests         ERROR               │
│   3 tests failed                    │
├─────────────────────────────────────┤
│   Deploy        PAUSED              │
│   Waiting for approval              │
└─────────────────────────────────────┘
  ↑↓ Navigate  ENTER: Details  BACK: Quit
```

### Detail Screen
```
┌─────────────────────────────────────┐
│ ← Backend                           │
├─────────────────────────────────────┤
│ Status: WAITING_APPROVAL            │
│ Updated: 2 seconds ago              │
├─────────────────────────────────────┤
│ Agent is requesting permission to   │
│ delete the users table. This will   │
│ remove all user data permanently.   │
│                        [More ▼]     │
├─────────────────────────────────────┤
│ [APPROVE]  [DENY]  [PAUSE]         │
└─────────────────────────────────────┘
```

### Agent States
| Status | Description | Actions |
|--------|-------------|---------|
| RUNNING | Actively working | pause |
| PAUSED | Paused by user | resume |
| WAITING_APPROVAL | Needs decision | approve, deny |
| ERROR | Encountered error | ack, retry |
| DONE | Completed | - |

### Agent Router Service

See [EVENT_SCHEMA.md](EVENT_SCHEMA.md) for message formats.

**Technology Stack:**
- Runtime: Node.js
- HTTP: Express
- WebSocket: ws library
- Auth: Shared secret token

**Simulator Mode:**
For testing without real agents:
- 4 simulated agents: Frontend, Backend, Tests, Deploy
- Random state updates every 3-10 seconds
- Periodically enter WAITING_APPROVAL and ERROR states

### Technical Architecture (Android)
```kotlin
class AgentWebSocketClient(
    private val host: String,
    private val token: String,
    private val onSnapshot: (List<AgentState>) -> Unit,
    private val onUpdate: (AgentState) -> Unit,
    private val onConnectionChange: (ConnectionState) -> Unit
) {
    fun connect()
    fun sendAction(agentId: String, action: String)
}

// Reconnect with exponential backoff
private fun scheduleReconnect() {
    val delay = minOf(30_000L, 1000L * (1 shl reconnectAttempts))
    reconnectAttempts++
    // Schedule reconnect
}
```

### Future Extensions
- Real agent adapters (Claude, GPT, Cursor)
- OAuth authentication
- Multi-user support
- Push notifications for WAITING_APPROVAL
- Action history logging
