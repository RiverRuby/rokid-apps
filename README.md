# Rokid Apps Portfolio

A collection of lightweight, focused apps for Rokid RV101 smart glasses. Each app is designed for the glasses' green monochrome display with large typography and minimal information density.

## Quick Start

```bash
# Prerequisites
brew install android-platform-tools

# Connect RV101 via 5-pin dev cable and verify
adb devices -l

# Install and launch an app
./tools/install.sh HelloHUD
```

## App Portfolio

| App | Status | Description |
|-----|--------|-------------|
| **HelloHUD** | 🔨 Phase 0 | Device validation test app |
| **NowCard** | 📋 Planned | Current task from Sunsama/Notion + Pomodoro timer |
| **ARPet** | 📋 Planned | Pixel art Tamagotchi connected to habit tracker |
| **Capture** | 📋 Planned | Quick camera capture for food/fashion logging |
| **SpeedReader** | 📋 Planned | RSVP-style article reading |
| **AgentHUD** | 📋 Planned | Coding agents status + approval actions |

## Next Steps

### Phase 0: Device Validation
1. Connect RV101 via 5-pin development cable
2. Run `./tools/device_info.sh` to collect device specs
3. Create HelloHUD Android project in `android/HelloHUD/`
4. Deploy with `./tools/install.sh HelloHUD`
5. Verify text is readable on glasses display
6. Update `docs/DEPLOY_RV101.md` with actual device values (Android version, SDK, resolution)

### Phase 1: Shared Components
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
- **Display**: Green monochrome waveguide
- **Camera**: 12MP Sony IMX681, 109° FOV
- **Weight**: 49 grams
- **OS**: YodaOS (Android variant)

## License

Private project - not for distribution.
