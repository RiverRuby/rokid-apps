# Troubleshooting Guide

Common issues and solutions for Rokid RV101 development.

## ADB Connection Issues

### "adb: device not found" or "no devices/emulators found"

**Possible Causes:**
1. Using 3-pin charging cable instead of 5-pin dev cable
2. USB port issue
3. ADB server state

**Solutions:**

```bash
# 1. Verify you have the 5-pin development cable
# 3-pin: [  | | |  ] - Charging only, NO ADB
# 5-pin: [ | | | | | ] - Development cable with ADB

# 2. Restart ADB server
adb kill-server
adb start-server
adb devices -l

# 3. Try different USB port (prefer direct ports, not hubs)

# 4. Check USB cable connection is secure
```

### "device unauthorized"

**Cause:** Device hasn't authorized this computer for debugging.

**Solutions:**

```bash
# Method 1: Toggle ADB in Hi Rokid app
# 1. Open Hi Rokid app on phone
# 2. Go to Settings > Developer Options
# 3. Turn OFF "ADB Debugging"
# 4. Turn ON "ADB Debugging"
# 5. Check glasses for authorization prompt
# 6. Run: adb devices -l

# Method 2: Remove and re-add device
adb kill-server
# Disconnect USB cable
# Wait 5 seconds
# Reconnect USB cable
adb start-server
adb devices -l
# Accept prompt on glasses
```

### Device shows "offline"

**Cause:** ADB connection partially established but not working.

**Solutions:**

```bash
# Restart ADB
adb kill-server
adb start-server

# If persists, restart the glasses
# (hold power button until restart)
```

## App Installation Issues

### "INSTALL_FAILED_UPDATE_INCOMPATIBLE"

**Cause:** Previous installation has different signing key.

**Solution:**
```bash
# Uninstall existing version
adb uninstall com.rokid.<appname>

# Install fresh
adb install app/build/outputs/apk/debug/app-debug.apk
```

### "INSTALL_FAILED_INSUFFICIENT_STORAGE"

**Cause:** Device storage is full.

**Solutions:**
```bash
# Check available storage
adb shell df -h

# Clear app caches
adb shell pm clear com.rokid.<appname>

# Uninstall unused apps
adb shell pm list packages | grep rokid
adb uninstall com.rokid.<unused_app>
```

### "INSTALL_FAILED_OLDER_SDK"

**Cause:** App's minSdk is higher than device's SDK version.

**Solution:**
```bash
# Check device SDK version
adb shell getprop ro.build.version.sdk

# Update build.gradle.kts to match
android {
    defaultConfig {
        minSdk = <device_sdk_version>
    }
}
```

### "Error: Activity class does not exist"

**Cause:** Wrong package name or activity name in launch command.

**Solution:**
```bash
# Get correct names from APK
aapt dump badging app/build/outputs/apk/debug/app-debug.apk | grep -E "package|launchable"

# Verify AndroidManifest.xml has correct intent-filter
# <activity android:name=".MainActivity">
#     <intent-filter>
#         <action android:name="android.intent.action.MAIN" />
#         <category android:name="android.intent.category.LAUNCHER" />
#     </intent-filter>
# </activity>
```

## App Runtime Issues

### App crashes immediately on launch

**Diagnosis:**
```bash
# Check logcat for crash info
adb logcat -v time | grep -E "(FATAL|AndroidRuntime|Exception)" -A 20
```

**Common Causes:**

1. **Missing permissions**
```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

2. **Null pointer in onCreate**
```kotlin
// Check for null safety in initialization
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ensure setContent is called
        setContent {
            MyApp()
        }
    }
}
```

3. **Missing dependencies**
```bash
# Check for ClassNotFoundException in logcat
# Add missing dependency to build.gradle.kts
```

### App launches but shows blank/black screen

**Possible Causes:**
1. UI not rendering properly
2. Theme/color issue
3. Exception during composition

**Solutions:**

```kotlin
// 1. Add debug logging
@Composable
fun MyApp() {
    Log.d("MyApp", "Composing MyApp")
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Log.d("MyApp", "Rendering content")
        Text(
            text = "Hello",
            color = Color.White,  // Must be visible against background
            fontSize = 36.sp      // Large enough to see
        )
    }
}

// 2. Check logcat for composition errors
// adb logcat | grep -E "(Compose|MyApp)"
```

### UI is unreadable on glasses

**Solutions:**

```kotlin
// 1. Increase font size (minimum 22sp for body text)
Text(
    text = "Content",
    fontSize = 24.sp,  // NOT 14sp
    fontWeight = FontWeight.Bold
)

// 2. Use high contrast colors
Box(modifier = Modifier.background(Color.Black)) {
    Text(
        text = "Visible text",
        color = Color.White  // NOT Color.Gray
    )
}

// 3. Check actual resolution
// adb shell wm size
// adb shell wm density
```

### DPAD navigation not working

**Solutions:**

```kotlin
// 1. Ensure app has focus
LaunchedEffect(Unit) {
    // Request focus on launch
    focusRequester.requestFocus()
}

// 2. Add key event handling
Modifier.onKeyEvent { event ->
    when (event.key) {
        Key.DirectionUp -> { /* handle */ true }
        Key.DirectionDown -> { /* handle */ true }
        Key.Enter -> { /* handle */ true }
        else -> false
    }
}

// 3. Check input events in logcat
// adb logcat | grep -i "keyevent"
```

## Network Issues

### WebSocket connection fails

**Diagnosis:**
```bash
# Check if router is running
curl http://<host>:8787/health

# Check network on glasses
adb shell ping -c 3 <host_ip>
```

**Solutions:**

1. **Same network check**
```bash
# Get glasses IP
adb shell ip addr show wlan0

# Get dev machine IP
ipconfig getifaddr en0

# Both should be on same subnet (e.g., 192.168.1.x)
```

2. **Firewall issue**
```bash
# macOS: Check firewall settings
# System Preferences > Security & Privacy > Firewall

# Allow incoming connections for Node.js
```

3. **Router binding issue**
```javascript
// In agent-router, bind to all interfaces
server.listen(8787, '0.0.0.0', () => {
    // NOT '127.0.0.1' or 'localhost'
});
```

4. **Use ADB port forwarding as fallback**
```bash
./tools/reverse_ports.sh
# Then configure app to use ws://localhost:8787/ws
```

### WebSocket disconnects frequently

**Solutions:**

```kotlin
// 1. Implement reconnection with backoff
private fun scheduleReconnect() {
    val delay = minOf(30_000L, 1000L * (1 shl reconnectAttempts))
    reconnectAttempts++
    handler.postDelayed({ connect() }, delay)
}

// 2. Add ping/pong keep-alive
val client = OkHttpClient.Builder()
    .pingInterval(15, TimeUnit.SECONDS)
    .build()

// 3. Show connection status in UI
if (connectionState == Disconnected) {
    Text("Reconnecting...", color = Color.Yellow)
}
```

### HTTP requests timeout

**Solutions:**

```kotlin
// 1. Set appropriate timeouts
val client = OkHttpClient.Builder()
    .connectTimeout(10, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

// 2. Check URL is correct
Log.d("Network", "Requesting: $url")

// 3. Verify network permission
// <uses-permission android:name="android.permission.INTERNET" />
```

## Build Issues

### Gradle build fails

**Solutions:**

```bash
# Clean and rebuild
cd android/<AppName>
./gradlew clean
./gradlew assembleDebug

# Clear Gradle cache
rm -rf ~/.gradle/caches/
./gradlew assembleDebug

# Check Java version (requires 17+)
java -version

# Update Gradle wrapper if needed
./gradlew wrapper --gradle-version=8.4
```

### Compose compiler version mismatch

**Error:** "Compose Compiler requires Kotlin X.Y but you're using X.Z"

**Solution:**
```kotlin
// build.gradle.kts
android {
    composeOptions {
        // Match to your Kotlin version
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

// Check compatibility:
// https://developer.android.com/jetpack/androidx/releases/compose-kotlin
```

### "Could not find shared module"

**Solution:**
```kotlin
// settings.gradle.kts
include(":app")
include(":shared")
project(":shared").projectDir = File("../shared")

// app/build.gradle.kts
dependencies {
    implementation(project(":shared"))
}
```

## Debugging Tips

### View all logs from app
```bash
./tools/logcat.sh <AppName>
# Or manually:
adb logcat -v time | grep -iE "(<AppName>|Exception|Error)"
```

### Take screenshot
```bash
adb exec-out screencap -p > screenshot.png
```

### Record screen
```bash
# Start recording (max 180 seconds)
adb shell screenrecord /sdcard/recording.mp4

# Ctrl+C to stop, then pull
adb pull /sdcard/recording.mp4
```

### Force stop app
```bash
adb shell am force-stop com.rokid.<appname>
```

### Clear app data
```bash
adb shell pm clear com.rokid.<appname>
```

### Check app state
```bash
# Is app running?
adb shell pidof com.rokid.<appname>

# App info
adb shell dumpsys package com.rokid.<appname>
```

## Getting Help

If issues persist:

1. **Collect diagnostic info:**
```bash
./tools/device_info.sh > device_info.txt
adb logcat -d > logcat.txt
```

2. **Check Rokid resources:**
- [Rokid Forum](https://forum.rokid.com/)
- [Rokid Developer Forum](https://developer-forum.rokid.com/)
- [RokidGlass GitHub](https://github.com/RokidGlass)

3. **Document the issue:**
- Device model and specs
- App name and version
- Steps to reproduce
- Relevant logcat output
- Screenshots if UI-related
