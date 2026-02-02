# Deploying Apps to Rokid RV101

## Prerequisites

- macOS with ADB installed (`brew install android-platform-tools`)
- 5-pin development cable (NOT the 3-pin charging cable)
- ADB debugging enabled via Hi Rokid app

## Device Specifications

| Property | Value |
|----------|-------|
| Model | RG-glasses (Rokid RV101) |
| Processor | Qualcomm Snapdragon AR1 (4x 1.9GHz Kryo) |
| RAM | Up to 2GB LPDDR4x |
| OS | YodaOS (Android 12) |
| Android Version | 12 |
| SDK Level | 32 |
| Display | Green monochrome waveguide |
| Resolution | 480x640 |
| Density | 240 dpi |
| CPU ABI | arm64-v8a |
| Camera | 12MP Sony IMX681, 109° FOV |
| Storage | ~18GB available |

## Quick Deploy

```bash
# Connect glasses via 5-pin cable
# Verify connection
adb devices -l

# Install app
./tools/install.sh <AppName>

# Example
./tools/install.sh HelloHUD
```

## Manual Deployment Steps

### 1. Build the APK

```bash
cd android/<AppName>
./gradlew assembleDebug
```

APK location: `app/build/outputs/apk/debug/app-debug.apk`

### 2. Install the APK

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Flags:
- `-r`: Replace existing installation
- `-g`: Grant all runtime permissions

### 3. Launch the App

```bash
# Get package and activity name
aapt dump badging app/build/outputs/apk/debug/app-debug.apk | grep -E "package|launchable"

# Launch
adb shell am start -n com.rokid.<appname>/.MainActivity
```

### 4. View Logs

```bash
# All logs from app
adb logcat | grep -i "<AppName>"

# Or use the helper script
./tools/logcat.sh <AppName>
```

## Deployment Script Details

### tools/install.sh

```bash
#!/bin/bash
# Usage: ./tools/install.sh <AppName>
# Example: ./tools/install.sh HelloHUD

# 1. Builds debug APK
# 2. Installs to connected device
# 3. Extracts package/activity from APK
# 4. Launches the app
```

### tools/launch.sh

```bash
#!/bin/bash
# Usage: ./tools/launch.sh <AppName>
# Launches an already-installed app
```

### tools/logcat.sh

```bash
#!/bin/bash
# Usage: ./tools/logcat.sh <AppName>
# Filters logcat for app-specific output
```

## Network Services (AgentHUD)

For apps that connect to network services:

### Option 1: Same Wi-Fi Network

1. Ensure glasses and dev machine on same network
2. Get dev machine's LAN IP: `ipconfig getifaddr en0`
3. Configure app to connect to `ws://<LAN_IP>:8787/ws`

### Option 2: ADB Port Forwarding

If Wi-Fi connectivity is unreliable:

```bash
# Forward port from glasses to host
adb reverse tcp:8787 tcp:8787

# Configure app to use localhost
# ws://localhost:8787/ws
```

## Uninstalling Apps

```bash
# Uninstall by package name
adb uninstall com.rokid.<appname>

# Example
adb uninstall com.rokid.hellohud
```

## Debugging

### Check App Installation

```bash
# List all installed packages
adb shell pm list packages | grep rokid

# Get app info
adb shell dumpsys package com.rokid.<appname>
```

### Force Stop App

```bash
adb shell am force-stop com.rokid.<appname>
```

### Clear App Data

```bash
adb shell pm clear com.rokid.<appname>
```

### Screenshot

```bash
adb exec-out screencap -p > screenshot.png
```

### Screen Recording

```bash
# Start recording (max 180 seconds)
adb shell screenrecord /sdcard/recording.mp4

# Pull recording
adb pull /sdcard/recording.mp4
```

## Build Variants

### Debug Build (Default)
- Debuggable
- No minification
- Full logging

```bash
./gradlew assembleDebug
```

### Release Build
- Minified
- Optimized
- Requires signing

```bash
./gradlew assembleRelease
```

## Common Issues

### "INSTALL_FAILED_UPDATE_INCOMPATIBLE"

Previous installation has different signature:
```bash
adb uninstall com.rokid.<appname>
adb install app/build/outputs/apk/debug/app-debug.apk
```

### "INSTALL_FAILED_INSUFFICIENT_STORAGE"

Device storage full:
```bash
# Check storage
adb shell df -h

# Clear app caches
adb shell pm clear com.rokid.<appname>
```

### App Crashes on Launch

Check logcat for stack trace:
```bash
./tools/logcat.sh <AppName> | grep -A 20 "FATAL EXCEPTION"
```

Common causes:
- Missing permissions in AndroidManifest.xml
- Incompatible SDK version
- Missing dependencies

## Phase 0 Validation Checklist

After connecting device for the first time, run:

```bash
./tools/device_info.sh
```

Device specs (collected 2026-02-01):

- [x] Android version: 12
- [x] SDK level: 32
- [x] Screen resolution: 480x640
- [x] Screen density: 240 dpi
- [x] ABI (CPU architecture): arm64-v8a
- [x] Available storage: ~18GB

Then deploy HelloHUD:

- [x] `./tools/install.sh HelloHUD` succeeds
- [x] App launches without crash
- [x] "HELLO RV101" text is visible
- [x] Tick logs appear in logcat every 2 seconds

**Phase 0 completed: 2026-02-01**
