# Rokid Apps Portfolio

A collection of lightweight, focused apps for Rokid RV101 smart glasses. Each app is designed for the glasses' green monochrome display with large typography and minimal information density.

## Quick Start

### First-Time Setup (No Android Studio Required)

```bash
# 1. Install tools
brew install android-platform-tools
brew install openjdk@17
brew install --cask android-commandlinetools

# 2. Add to ~/.zshrc (then restart terminal or run: source ~/.zshrc)
export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
export PATH="$JAVA_HOME/bin:$PATH"

# 3. Install Android SDK components
/bin/bash -c 'export JAVA_HOME=/opt/homebrew/opt/openjdk@17 && yes | sdkmanager --licenses'
/bin/bash -c 'export JAVA_HOME=/opt/homebrew/opt/openjdk@17 && sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"'

# 4. Create local.properties in each app (points to SDK location)
echo "sdk.dir=/opt/homebrew/share/android-commandlinetools" > android/HelloHUD/local.properties
```

### Build and Deploy

```bash
# Connect RV101 via 5-pin dev cable and verify
adb devices -l

# Build and install an app
cd android/HelloHUD
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.rokid.hellohud/.MainActivity
```

See [docs/SETUP_MACOS.md](docs/SETUP_MACOS.md) for detailed setup instructions.

## App Portfolio

| App | Status | Description |
|-----|--------|-------------|
| **HelloHUD** | ✅ Complete | Device validation test app |
| **NowCard** | 📋 Planned | Current task from Sunsama/Notion + Pomodoro timer |
| **ARPet** | 📋 Planned | Pixel art Tamagotchi connected to habit tracker |
| **Capture** | 📋 Planned | Quick camera capture for food/fashion logging |
| **SpeedReader** | 📋 Planned | RSVP-style article reading |
| **AgentHUD** | 📋 Planned | Coding agents status + approval actions |

## Next Steps

### ~~Phase 0: Device Validation~~ ✅ Complete
- HelloHUD deployed and running on RV101
- Device specs: Android 12, SDK 32, 480x640, 240dpi

### Phase 1: Shared Components (Current)
1. Create `GlassesTheme` with typography and colors
2. Create `DpadNavigation` modifier for touchpad input
3. Create `VoiceCommandService` interface

### Phase 2: First App (NowCard)
1. Set up Sunsama or Notion API integration
2. Build task display UI
3. Add Pomodoro timer
4. Test on device

### Future Phases
- ARPet with Postgres habit tracker
- Capture app with camera integration
- SpeedReader with article extraction
- AgentHUD with WebSocket connection to agent-router

See [CLAUDE.md](CLAUDE.md) for detailed status and roadmap.

## Prerequisites

- macOS with Homebrew
- Android Platform Tools (`brew install android-platform-tools`)
- Rokid RV101 glasses with **5-pin development cable** (not 3-pin charging cable)
- Hi Rokid companion app on phone (to enable ADB debugging)

## Repository Structure

```
rokid-apps/
├── docs/              # Detailed documentation
├── tools/             # Shell scripts for device operations
├── android/           # Android app projects
│   ├── shared/        # Shared UI components
│   ├── HelloHUD/      # Phase 0 test app
│   ├── NowCard/       # Task display app
│   ├── ARPet/         # Virtual pet app
│   ├── Capture/       # Camera capture app
│   ├── SpeedReader/   # Speed reading app
│   └── AgentHUD/      # Agent monitoring HUD
└── services/          # Backend services
    └── agent-router/  # WebSocket server for AgentHUD
```

## Documentation

- [Setup Guide](docs/SETUP_MACOS.md) - macOS development environment
- [Deployment Guide](docs/DEPLOY_RV101.md) - Installing apps on RV101
- [App Ideas](docs/APP_IDEAS.md) - Detailed app specifications
- [Architecture](docs/ARCHITECTURE.md) - System design and patterns
- [UI Guidelines](docs/UI_GUIDELINES.md) - Glasses-specific UI patterns
- [Event Schema](docs/EVENT_SCHEMA.md) - WebSocket message formats
- [Troubleshooting](docs/TROUBLESHOOTING.md) - Debug playbook
- [References](docs/REFERENCES.md) - External links and resources

## Device Specs (RV101)

- **Processor**: Qualcomm Snapdragon AR1
- **OS**: Android 12 (SDK 32)
- **Display**: Green monochrome waveguide, 480x640, 240dpi
- **Camera**: 12MP Sony IMX681, 109° FOV
- **CPU**: arm64-v8a
- **Weight**: 49 grams

## License

Private project - not for distribution.
