#!/bin/bash
# Launch an installed app on Rokid RV101
# Usage: ./tools/launch.sh <AppName>
# Example: ./tools/launch.sh HelloHUD

set -e

APP_NAME="${1:-}"

if [ -z "$APP_NAME" ]; then
    echo "Usage: ./tools/launch.sh <AppName>"
    echo "Example: ./tools/launch.sh HelloHUD"
    exit 1
fi

# Check ADB connection
if ! adb devices -l 2>&1 | grep -q "device "; then
    echo "ERROR: No device connected or device unauthorized"
    exit 1
fi

# Convert to lowercase for package name
LOWER_NAME=$(echo "$APP_NAME" | tr '[:upper:]' '[:lower:]')
PACKAGE="com.rokid.$LOWER_NAME"
ACTIVITY="$PACKAGE.MainActivity"

echo "Launching $APP_NAME..."
echo "Package:  $PACKAGE"
echo "Activity: $ACTIVITY"

# Try to launch
if adb shell am start -n "$PACKAGE/$ACTIVITY" 2>&1 | grep -q "Error"; then
    echo ""
    echo "ERROR: Failed to launch. The app may not be installed."
    echo ""
    echo "Installed Rokid apps:"
    adb shell pm list packages | grep -i rokid || echo "  None found"
    echo ""
    echo "Try installing first: ./tools/install.sh $APP_NAME"
    exit 1
fi

echo ""
echo "App launched. Run './tools/logcat.sh $APP_NAME' to view logs."
