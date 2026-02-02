# Rokid Apps Portfolio - Agent Context

## Product Overview

This is a portfolio of lightweight apps for Rokid RV101 smart glasses. The glasses have a green monochrome display, limited screen real estate, and DPAD-style navigation via temple touchpad. Apps must use large typography (24-32sp minimum), high contrast (white on black), and minimal information density. The goal is to build useful "glanceable" utilities that leverage the always-available nature of smart glasses.

## Repository Map

```
rokid-apps/
├── README.md              # Portfolio overview, quick start
├── CLAUDE.md              # This file - agent context
├── AGENTS.md              # Operating instructions for agents
├── docs/
│   ├── SETUP_MACOS.md     # Dev environment setup
│   ├── DEPLOY_RV101.md    # Device deployment guide
│   ├── APP_IDEAS.md       # Full app specifications
│   ├── ARCHITECTURE.md    # System design, shared patterns
│   ├── EVENT_SCHEMA.md    # WebSocket/HTTP message formats
│   ├── UI_GUIDELINES.md   # Glasses UI best practices
│   ├── TROUBLESHOOTING.md # Debug playbook
│   └── REFERENCES.md      # External links
├── tools/
│   ├── device_info.sh     # Collect device specs via ADB
│   ├── install.sh         # Build and install app to glasses
│   ├── launch.sh          # Launch installed app
│   ├── logcat.sh          # View app logs
│   └── reverse_ports.sh   # Port forwarding for network issues
├── android/
│   ├── shared/            # Shared UI components (GlassesTheme, DpadNavigation)
│   ├── HelloHUD/          # Phase 0 test app
│   ├── NowCard/           # Current task + Pomodoro timer
│   ├── ARPet/             # Pixel art pet + habit tracking
│   ├── Capture/           # Food/fashion camera capture
│   ├── SpeedReader/       # RSVP article reader
│   └── AgentHUD/          # Coding agent status HUD
└── services/
    └── agent-router/      # Node.js WebSocket server for AgentHUD
```

## Current Status

### Completed
- [x] Research Rokid RV101 specs and development requirements
- [x] Define app portfolio and priorities
- [x] Create repository structure
- [x] Write all documentation files
- [x] Create tool script stubs
- [x] Phase 0: Connect RV101 via ADB, verify toolchain
- [x] Phase 0: Document actual device specs (Android 12, SDK 32, 480x640)
- [x] Phase 0: Deploy HelloHUD test app (2026-02-01)
- [x] Font testing on device - selected JetBrains Mono & Space Grotesk (2026-02-01)
- [x] Phase 1: Build shared UI components (2026-02-01)
  - GlassesTheme, GlassesColors, GlassesFonts, GlassesTypography
  - DpadNavigation modifier
  - UI components: FocusableItem, GlassesButton, GlassesListItem, ConnectionStatusBanner
  - UiState pattern with UiStateContainer
  - VoiceCommandService interface
  - HelloHUD updated to use shared module

### Pending
- [ ] NowCard MVP
- [ ] ARPet MVP
- [ ] Capture MVP
- [ ] SpeedReader MVP
- [ ] AgentHUD + agent-router service

## How to Run Apps

```bash
# Check device connection
adb devices -l

# Collect device info
./tools/device_info.sh

# Install and launch an app
./tools/install.sh <AppName>    # e.g., ./tools/install.sh HelloHUD

# Launch an already-installed app
./tools/launch.sh <AppName>

# View logs
./tools/logcat.sh <AppName>
```

## How to Deploy to Glasses

1. Connect RV101 with 5-pin development cable
2. Enable ADB debugging in Hi Rokid app on phone
3. Run `adb devices -l` to verify connection
4. Run `./tools/install.sh <AppName>`

See [docs/DEPLOY_RV101.md](docs/DEPLOY_RV101.md) for detailed instructions.

## Key Device Facts

| Property | Value |
|----------|-------|
| Model | RG-glasses (Rokid RV101) |
| Processor | Qualcomm Snapdragon AR1 |
| RAM | Up to 2GB LPDDR4x |
| OS | YodaOS (Android 12) |
| Android SDK | 32 |
| Display | Green monochrome waveguide |
| Resolution | 480x640 (portrait) |
| Density | 240 dpi |
| CPU ABI | arm64-v8a |
| Camera | 12MP Sony IMX681, 109° FOV |
| Weight | 49 grams |
| Storage | ~18GB available |
| Dev Cable | 5-pin (required) - 3-pin charging cable does NOT support ADB |

## Design Constraints for Glasses UI

- **Green monochrome display** - no color, only intensity/brightness
- **Large typography**: 24-32sp minimum for body text, 36sp+ for headers
- **High contrast**: White/bright on black background only
- **Minimal density**: Max 4-6 items visible at once
- **DPAD navigation**: Up/down/left/right/select/back via touchpad
- **Focus states must be obvious**: Large highlight, no subtle hover effects
- **Avoid**: Small icons, color-dependent UI, dense layouts, fine details

## Design Decisions

### Typography
Tested fonts on actual RV101 display (2026-02-01). Selected fonts for GlassesTheme:

| Use Case | Font | Notes |
|----------|------|-------|
| **Primary UI** | JetBrains Mono | Clean monospace, excellent readability, tech aesthetic |
| **Headers/Accent** | Space Grotesk | Distinctive geometric sans, good contrast with mono |

Font files located in `android/shared/src/main/assets/fonts/`. Load via `Typeface.createFromAsset()` - more reliable than `res/font` resources in Compose. Call `GlassesFonts.init(context)` in Activity onCreate before setContent.

**Rejected alternatives:**
- Roboto (default) - too generic, doesn't fit HUD aesthetic
- Inter - too similar to Roboto on small display
- Space Mono - less readable than JetBrains Mono

## Event Schema Summary (AgentHUD)

Full schema in [docs/EVENT_SCHEMA.md](docs/EVENT_SCHEMA.md).

### Message Types
- `snapshot` - Full state of all agents (sent on WebSocket connect)
- `agent_update` - Single agent state change
- `agent_action` - Action command from HUD to router

### Agent Statuses
- `RUNNING` - Agent actively working (actions: pause)
- `PAUSED` - Agent paused by user (actions: resume)
- `WAITING_APPROVAL` - Needs user decision (actions: approve, deny)
- `ERROR` - Encountered error (actions: ack, retry)
- `DONE` - Task completed (no actions)

## Development Environment Setup

**Required tools** (install via Homebrew):
```bash
brew install android-platform-tools   # ADB
brew install openjdk@17               # Java 17 (required for Android)
brew install --cask android-commandlinetools  # Android SDK
```

**Environment variables** (add to `~/.zshrc`):
```bash
export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
export PATH="$JAVA_HOME/bin:$PATH"
```

**SDK setup** (one-time):
```bash
/bin/bash -c 'export JAVA_HOME=/opt/homebrew/opt/openjdk@17 && yes | sdkmanager --licenses'
/bin/bash -c 'export JAVA_HOME=/opt/homebrew/opt/openjdk@17 && sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"'
```

**Per-project**: Create `local.properties` with SDK path:
```
sdk.dir=/opt/homebrew/share/android-commandlinetools
```

## Known Issues and Gotchas

1. **3-pin vs 5-pin cable**: The standard charging cable (3-pin) does NOT support ADB. You must use the development cable (5-pin, $39.99).

2. **ADB authorization**: First connection requires accepting prompt on glasses OR enabling ADB in Hi Rokid phone app.

3. **Wi-Fi connectivity**: Glasses may not be on same Wi-Fi as dev machine. Use `tools/reverse_ports.sh` as fallback for network services.

4. **Green display limitations**: Cannot use color for information. Use size, position, and animation instead.

5. **YodaOS compatibility**: ~90% Android app compatible, but some APIs may behave differently. Test on device early.

6. **Java version**: Must use Java 17+. The system Java 8 won't work. Always set `JAVA_HOME` before running Gradle.

7. **SDK location**: Gradle needs `local.properties` with `sdk.dir` pointing to the Android SDK. Without Android Studio, SDK is at `/opt/homebrew/share/android-commandlinetools`.

8. **Waveguide ghosting**: All UI elements show a ghost/reflection artifact (flipped, offset duplicate). This is a hardware limitation. Ghost visibility scales with brightness - use `GlassesColors.veryDim` (~25%) for status info to make ghosts imperceptible. Accept some ghosting for primary content.

## Next Milestone Roadmap

### ~~Phase 0~~ ✅ Complete (2026-02-01)
- Device connected, specs collected
- HelloHUD deployed and verified

### ~~Phase 1~~ ✅ Complete (2026-02-01)
- Shared module at `android/shared/`
- GlassesTheme with colors, fonts, typography
- DpadNavigation modifier for touchpad input
- UI components: FocusableItem, GlassesButton, GlassesListItem, etc.
- VoiceCommandService interface
- HelloHUD updated and validated on device

### Phase 2: NowCard (Current)
1. Sunsama/Notion API integration
2. Pomodoro timer
3. Task actions (done, skip, extend)

### Phase 3: ARPet
1. Postgres habit DB connection
2. Pixel art rendering
3. Voice command recognition

## Integration Points

### Sunsama
- Authentication: OAuth or API key
- Endpoints: GET current task, POST mark complete/skip

### Notion
- Authentication: Integration token
- Endpoints: Query database, create page, update page

### Habit Tracker (Custom PostgreSQL)
- Connection details: TBD
- Schema: TBD
- Operations: Read habits, write completions

## App Priority Order

1. **NowCard** - Simplest, highest daily utility
2. **ARPet** - Fun + habit accountability
3. **Capture** - Camera utility
4. **SpeedReader** - Content consumption
5. **Meeting Overlay** - High complexity, defer
6. **AgentHUD** - Original vision, build last with learnings
