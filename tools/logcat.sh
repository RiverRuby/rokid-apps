#!/bin/bash
# View logs for an app on Rokid RV101
# Usage: ./tools/logcat.sh <AppName>
# Example: ./tools/logcat.sh HelloHUD

APP_NAME="${1:-}"

if [ -z "$APP_NAME" ]; then
    echo "Usage: ./tools/logcat.sh <AppName>"
    echo "Example: ./tools/logcat.sh HelloHUD"
    echo ""
    echo "This will filter logcat for messages containing the app name."
    echo "Press Ctrl+C to stop."
    exit 1
fi

# Check ADB connection
if ! adb devices -l 2>&1 | grep -q "device "; then
    echo "ERROR: No device connected or device unauthorized"
    exit 1
fi

# Convert to lowercase for package matching
LOWER_NAME=$(echo "$APP_NAME" | tr '[:upper:]' '[:lower:]')

echo "=== Logcat for $APP_NAME ==="
echo "Filtering for: $APP_NAME, $LOWER_NAME, com.rokid.$LOWER_NAME"
echo "Press Ctrl+C to stop"
echo ""

# Clear existing logs and start fresh
adb logcat -c 2>/dev/null

# Filter for app-related logs
# Include: app name, package name, common tags like AndroidRuntime, Compose
adb logcat -v time | grep -iE "($APP_NAME|com\.rokid\.$LOWER_NAME|AndroidRuntime|FATAL|Exception)"
