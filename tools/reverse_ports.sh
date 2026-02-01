#!/bin/bash
# Set up ADB reverse port forwarding for network services
# Usage: ./tools/reverse_ports.sh [port]
# Example: ./tools/reverse_ports.sh 8787

set -e

PORT="${1:-8787}"

# Check ADB connection
if ! adb devices -l 2>&1 | grep -q "device "; then
    echo "ERROR: No device connected or device unauthorized"
    exit 1
fi

echo "=== ADB Reverse Port Forwarding ==="
echo ""
echo "Setting up reverse port $PORT..."
adb reverse tcp:$PORT tcp:$PORT

echo ""
echo "Port $PORT forwarded successfully."
echo ""
echo "What this means:"
echo "  - Device can now reach localhost:$PORT"
echo "  - Traffic to device's localhost:$PORT goes to host's localhost:$PORT"
echo ""
echo "Configure your app to connect to:"
echo "  ws://localhost:$PORT/ws  (for WebSocket)"
echo "  http://localhost:$PORT   (for HTTP)"
echo ""
echo "To remove forwarding:"
echo "  adb reverse --remove tcp:$PORT"
echo ""
echo "To list all forwards:"
echo "  adb reverse --list"
