#!/bin/bash
# Build, install, and launch an app on Rokid RV101
# Usage: ./tools/install.sh <AppName>
# Example: ./tools/install.sh HelloHUD

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"

APP_NAME="${1:-}"

if [ -z "$APP_NAME" ]; then
    echo "Usage: ./tools/install.sh <AppName>"
    echo ""
    echo "Available apps:"
    ls -d "$ROOT_DIR/android"/*/ 2>/dev/null | xargs -n1 basename | grep -v shared || echo "  No apps found in android/"
    exit 1
fi

APP_DIR="$ROOT_DIR/android/$APP_NAME"

if [ ! -d "$APP_DIR" ]; then
    echo "ERROR: App directory not found: $APP_DIR"
    echo ""
    echo "Available apps:"
    ls -d "$ROOT_DIR/android"/*/ 2>/dev/null | xargs -n1 basename | grep -v shared || echo "  No apps found in android/"
    exit 1
fi

# Check ADB connection
if ! adb devices -l 2>&1 | grep -q "device "; then
    echo "ERROR: No device connected or device unauthorized"
    echo "Run ./tools/device_info.sh for diagnostic info"
    exit 1
fi

echo "=== Building $APP_NAME ==="
cd "$APP_DIR"

# Check for gradlew
if [ ! -f "gradlew" ]; then
    echo "ERROR: gradlew not found in $APP_DIR"
    echo "This app may not be set up yet. Create Android project first."
    exit 1
fi

chmod +x gradlew
./gradlew assembleDebug

APK_PATH="app/build/outputs/apk/debug/app-debug.apk"

if [ ! -f "$APK_PATH" ]; then
    echo "ERROR: APK not found at $APK_PATH"
    echo "Build may have failed. Check Gradle output above."
    exit 1
fi

echo ""
echo "=== Installing $APP_NAME ==="
adb install -r "$APK_PATH"

# Extract package and activity name
if command -v aapt &> /dev/null; then
    PACKAGE=$(aapt dump badging "$APK_PATH" 2>/dev/null | grep "package:" | sed "s/.*name='\([^']*\)'.*/\1/")
    ACTIVITY=$(aapt dump badging "$APK_PATH" 2>/dev/null | grep "launchable-activity:" | sed "s/.*name='\([^']*\)'.*/\1/")
else
    # Fallback: assume standard naming
    LOWER_NAME=$(echo "$APP_NAME" | tr '[:upper:]' '[:lower:]')
    PACKAGE="com.rokid.$LOWER_NAME"
    ACTIVITY="$PACKAGE.MainActivity"
fi

echo ""
echo "=== Launching $APP_NAME ==="
echo "Package:  $PACKAGE"
echo "Activity: $ACTIVITY"

if [ -n "$PACKAGE" ] && [ -n "$ACTIVITY" ]; then
    adb shell am start -n "$PACKAGE/$ACTIVITY"
    echo ""
    echo "=== Done ==="
    echo "App launched. Run './tools/logcat.sh $APP_NAME' to view logs."
else
    echo "WARNING: Could not determine package/activity. Launch manually or check APK."
fi
