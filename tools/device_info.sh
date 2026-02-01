#!/bin/bash
# Collect Rokid RV101 device information via ADB
# Usage: ./tools/device_info.sh

set -e

echo "=== Rokid RV101 Device Info ==="
echo "Date: $(date)"
echo "macOS: $(sw_vers -productVersion)"
echo ""

# Check ADB is available
if ! command -v adb &> /dev/null; then
    echo "ERROR: adb not found. Install with: brew install android-platform-tools"
    exit 1
fi

# Check device connection
DEVICES=$(adb devices -l 2>&1)
if echo "$DEVICES" | grep -q "no devices"; then
    echo "ERROR: No device connected"
    echo "1. Ensure you're using the 5-pin development cable (not 3-pin charging cable)"
    echo "2. Enable ADB debugging in Hi Rokid app"
    echo "3. Try: adb kill-server && adb start-server"
    exit 1
fi

if echo "$DEVICES" | grep -q "unauthorized"; then
    echo "ERROR: Device unauthorized"
    echo "1. Check glasses for authorization prompt"
    echo "2. Or toggle ADB debugging in Hi Rokid app"
    exit 1
fi

echo "Device connected:"
echo "$DEVICES"
echo ""

echo "--- System Information ---"
echo "Model:        $(adb shell getprop ro.product.model 2>/dev/null || echo 'N/A')"
echo "Manufacturer: $(adb shell getprop ro.product.manufacturer 2>/dev/null || echo 'N/A')"
echo "Device:       $(adb shell getprop ro.product.device 2>/dev/null || echo 'N/A')"
echo "Brand:        $(adb shell getprop ro.product.brand 2>/dev/null || echo 'N/A')"
echo ""

echo "--- Android Version ---"
echo "Android:      $(adb shell getprop ro.build.version.release 2>/dev/null || echo 'N/A')"
echo "SDK Level:    $(adb shell getprop ro.build.version.sdk 2>/dev/null || echo 'N/A')"
echo "Build ID:     $(adb shell getprop ro.build.id 2>/dev/null || echo 'N/A')"
echo "Build Type:   $(adb shell getprop ro.build.type 2>/dev/null || echo 'N/A')"
echo ""

echo "--- Hardware ---"
echo "CPU ABI:      $(adb shell getprop ro.product.cpu.abi 2>/dev/null || echo 'N/A')"
echo "CPU ABIs:     $(adb shell getprop ro.product.cpu.abilist 2>/dev/null || echo 'N/A')"
echo "Hardware:     $(adb shell getprop ro.hardware 2>/dev/null || echo 'N/A')"
echo ""

echo "--- Display ---"
echo "Screen Size:  $(adb shell wm size 2>/dev/null | grep -oE '[0-9]+x[0-9]+' || echo 'N/A')"
echo "Density:      $(adb shell wm density 2>/dev/null | grep -oE '[0-9]+' || echo 'N/A') dpi"
echo ""

echo "--- Storage ---"
adb shell df -h /data 2>/dev/null | head -2 || echo "N/A"
echo ""

echo "--- Network ---"
echo "Wi-Fi IP:     $(adb shell ip addr show wlan0 2>/dev/null | grep -oE 'inet [0-9.]+' | cut -d' ' -f2 || echo 'N/A')"
echo ""

echo "--- Installed Rokid Apps ---"
adb shell pm list packages 2>/dev/null | grep -i rokid || echo "None found"
echo ""

echo "=== Collection Complete ==="
