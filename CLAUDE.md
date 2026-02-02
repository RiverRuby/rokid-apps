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

### Pending
- [ ] Build shared UI components (GlassesTheme, DpadNavigation)
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

## Known Issues and Gotchas

1. **3-pin vs 5-pin cable**: The standard charging cable (3-pin) does NOT support ADB. You must use the development cable (5-pin, $39.99).

2. **ADB authorization**: First connection requires accepting prompt on glasses OR enabling ADB in Hi Rokid phone app.

3. **Wi-Fi connectivity**: Glasses may not be on same Wi-Fi as dev machine. Use `tools/reverse_ports.sh` as fallback for network services.

4. **Green display limitations**: Cannot use color for information. Use size, position, and animation instead.

5. **YodaOS compatibility**: ~90% Android app compatible, but some APIs may behave differently. Test on device early.

## Next Milestone Roadmap

### Phase 0 (Next Session)
1. Connect RV101 via ADB
2. Run `tools/device_info.sh` to collect specs
3. Build and deploy HelloHUD
4. Update DEPLOY_RV101.md with real findings

### Phase 1: Shared Components
1. Create GlassesTheme (colors, typography)
2. Create DpadNavigation modifier
3. Create VoiceCommandService interface

### Phase 2: NowCard
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
