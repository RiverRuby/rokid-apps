# macOS Development Environment Setup

## Prerequisites

### 1. Install Homebrew (if not already installed)
```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

### 2. Install Android Platform Tools
```bash
brew install android-platform-tools
```

Verify installation:
```bash
adb version
# Expected: Android Debug Bridge version X.X.X
```

### 3. Install Java Development Kit

Android development requires JDK 17+:
```bash
brew install openjdk@17
```

**Important**: Add to your `~/.zshrc` or `~/.bash_profile`:
```bash
export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
export PATH="$JAVA_HOME/bin:$PATH"
```

Then reload your shell:
```bash
source ~/.zshrc  # or source ~/.bash_profile
```

Verify:
```bash
java -version
# Expected: openjdk version "17.x.x"
```

### 4. Install Android SDK (Command Line - No Android Studio)

If you don't want to install Android Studio, you can use command line tools:

```bash
# Install command line tools
brew install --cask android-commandlinetools

# Accept licenses (run in a proper bash shell)
/bin/bash -c 'export JAVA_HOME=/opt/homebrew/opt/openjdk@17 && yes | sdkmanager --licenses'

# Install required SDK components
/bin/bash -c 'export JAVA_HOME=/opt/homebrew/opt/openjdk@17 && sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"'
```

The SDK is installed at `/opt/homebrew/share/android-commandlinetools`.

### 4b. Install Android Studio (Alternative)

If you prefer a GUI, download Android Studio from: https://developer.android.com/studio

Android Studio provides:
- Gradle build system
- APK analysis tools
- Layout inspector
- Logcat viewer with filtering

The SDK will be at `~/Library/Android/sdk`.

### 5. Configure SDK Path for Projects

Each Android project needs a `local.properties` file pointing to the SDK:

```bash
# For command line tools installation:
echo "sdk.dir=/opt/homebrew/share/android-commandlinetools" > android/HelloHUD/local.properties

# For Android Studio installation:
echo "sdk.dir=$HOME/Library/Android/sdk" > android/HelloHUD/local.properties
```

## Hardware Requirements

### Rokid RV101 Development Cable

**CRITICAL**: The standard 3-pin charging cable does NOT support ADB.

You need the **5-pin development cable** ($39.99):
- Purchase from Rokid store or authorized retailers
- The cable has 5 pins instead of 3
- Only this cable enables USB debugging

### Cable Identification
```
3-pin (Charging only):  [  | | |  ]  ← Does NOT work for ADB
5-pin (Development):    [ | | | | | ] ← Required for development
```

## First-Time Device Setup

### 1. Install Hi Rokid App on Phone

Download "Hi Rokid" from:
- iOS: App Store
- Android: Google Play

### 2. Pair Glasses with Phone

1. Open Hi Rokid app
2. Follow pairing instructions
3. Complete initial glasses setup

### 3. Enable ADB Debugging

In Hi Rokid app:
1. Go to Settings
2. Find Developer Options
3. Enable "ADB Debugging"

### 4. Connect Glasses to Mac

1. Connect 5-pin development cable to glasses
2. Connect USB end to Mac
3. Run: `adb devices -l`

First connection may show:
```
List of devices attached
XXXXXXXX       unauthorized
```

If unauthorized:
- Check glasses display for authorization prompt
- Or toggle ADB debugging in Hi Rokid app

Successful connection shows:
```
List of devices attached
XXXXXXXX       device product:rv101 model:RV101 device:rv101
```

## Project Setup

### 1. Clone Repository
```bash
cd ~/projects  # or your preferred location
git clone <repo-url> rokid-apps
cd rokid-apps
```

### 2. Make Scripts Executable
```bash
chmod +x tools/*.sh
```

### 3. Verify Device Connection
```bash
./tools/device_info.sh
```

### 4. Test Deployment
```bash
./tools/install.sh HelloHUD
```

## Environment Variables (Optional)

Add to `~/.zshrc` or `~/.bash_profile`:

```bash
# Android SDK (if using Android Studio)
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$ANDROID_HOME/platform-tools:$PATH"
export PATH="$ANDROID_HOME/tools:$PATH"

# Java
export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
export PATH="$JAVA_HOME/bin:$PATH"
```

## Troubleshooting

### "adb: command not found"
```bash
# Reinstall platform-tools
brew reinstall android-platform-tools

# Or add to PATH manually
export PATH="$PATH:/opt/homebrew/bin"
```

### "no devices/emulators found"
1. Check cable is 5-pin development cable
2. Try different USB port
3. Restart ADB:
```bash
adb kill-server
adb start-server
adb devices
```

### "error: device unauthorized"
1. Open Hi Rokid app on phone
2. Go to Settings > Developer Options
3. Toggle ADB Debugging off, then on
4. Check glasses for authorization prompt

### Gradle build fails
```bash
# Clear Gradle cache
cd android/<AppName>
./gradlew clean

# Update Gradle wrapper
./gradlew wrapper --gradle-version=8.4
```

## Next Steps

Once setup is complete:
1. Read [DEPLOY_RV101.md](DEPLOY_RV101.md) for deployment details
2. Read [UI_GUIDELINES.md](UI_GUIDELINES.md) before building UI
3. Check [APP_IDEAS.md](APP_IDEAS.md) for app specifications
