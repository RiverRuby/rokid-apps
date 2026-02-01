# References & External Links

Useful resources for Rokid RV101 development.

## Official Rokid Resources

### Apps & Downloads
- [Rokid Downloads](https://global.rokid.com/pages/downloads) - Hi Rokid companion app
- [Rokid Support Hub](https://global.rokid.com/pages/support) - Official support center

### Documentation
- [Rokid How-to Guide](https://global.rokid.com/pages/how-to-use) - User guides
- [RokidGlass GitBook Docs](https://rokidglass.github.io/glass2-docs/en/) - Developer documentation
- [Rokid Security Center](https://global.rokid.com/pages/security-center) - Security information

### Developer Resources
- [RokidGlass GitHub](https://github.com/RokidGlass) - Official GitHub organization
- [Rokid AR Platform SDK](https://ar.rokid.com/sdk?lang=en) - AR SDK documentation
- [UXR SDK Docs (Legacy)](https://github.com/RokidGlass/UXR-docs) - Legacy SDK docs

### Community
- [Rokid Forum](https://forum.rokid.com/) - Community forum
- [Rokid Developer Forum](https://developer-forum.rokid.com/) - Developer discussions
- [Rokid Subreddit](https://www.reddit.com/r/rokid_official/) - Reddit community

### Product Pages
- [Rokid Glasses Product Page](https://global.rokid.com/pages/rokid-glasses) - Product information

## Android Development

### Core Tools
- [Android Debug Bridge (ADB)](https://developer.android.com/tools/adb) - ADB documentation
- [Logcat](https://developer.android.com/studio/debug/logcat) - Logging system
- [Android Studio](https://developer.android.com/studio) - Official IDE

### Jetpack Compose
- [Compose Documentation](https://developer.android.com/jetpack/compose) - Official Compose docs
- [Compose API Reference](https://developer.android.com/reference/kotlin/androidx/compose/ui/package-summary) - API reference
- [Compose Samples](https://github.com/android/compose-samples) - Official samples
- [Compose-Kotlin Compatibility](https://developer.android.com/jetpack/androidx/releases/compose-kotlin) - Version compatibility

### Architecture
- [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Architecture component
- [StateFlow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow) - Reactive state
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) - Data persistence

### Networking
- [OkHttp](https://square.github.io/okhttp/) - HTTP client
- [OkHttp WebSocket](https://square.github.io/okhttp/4.x/okhttp/okhttp3/-web-socket/) - WebSocket support
- [Gson](https://github.com/google/gson) - JSON serialization

## Node.js & Backend

### Core
- [Node.js](https://nodejs.org/) - Runtime
- [Express](https://expressjs.com/) - Web framework
- [ws (WebSocket)](https://github.com/websockets/ws) - WebSocket library

### Tools
- [nodemon](https://nodemon.io/) - Auto-restart on changes
- [dotenv](https://github.com/motdotla/dotenv) - Environment variables

## Integration APIs

### Sunsama
- [Sunsama](https://www.sunsama.com/) - Task management app
- API documentation: Contact Sunsama for API access

### Notion
- [Notion API](https://developers.notion.com/) - Official API docs
- [Notion SDK for JavaScript](https://github.com/makenotion/notion-sdk-js) - Official SDK
- [Notion Database API](https://developers.notion.com/reference/database) - Database operations

## Design Resources

### AR/Smart Glasses UX
- [Google Glass Design Guidelines](https://developers.google.com/glass/design/principles) - Design principles (archived but relevant)
- [Meta Quest Design Guidelines](https://developer.oculus.com/design/) - VR/AR UX patterns

### Typography
- [Material Design Type Scale](https://m3.material.io/styles/typography/type-scale-tokens) - Typography system
- [Google Fonts](https://fonts.google.com/) - Free fonts

### Accessibility
- [WCAG Contrast Guidelines](https://www.w3.org/WAI/WCAG21/Understanding/contrast-minimum.html) - Contrast requirements
- [Android Accessibility](https://developer.android.com/guide/topics/ui/accessibility) - Accessibility development

## Unofficial Resources

> ⚠️ Validate information from unofficial sources before relying on it.

- [Rokid Dev Cable Guide](https://marcinmiazga.com/rokid-development-cable) - Development cable info
- YouTube tutorials - Search "Rokid RV101 development"

## Tools

### macOS Development
- [Homebrew](https://brew.sh/) - Package manager
- [Android Platform Tools](https://formulae.brew.sh/cask/android-platform-tools) - ADB and fastboot

### Code Quality
- [ktlint](https://ktlint.github.io/) - Kotlin linter
- [Detekt](https://detekt.dev/) - Static analysis for Kotlin

### Testing
- [Compose Testing](https://developer.android.com/jetpack/compose/testing) - UI testing
- [JUnit 5](https://junit.org/junit5/) - Unit testing
- [Turbine](https://github.com/cashapp/turbine) - Flow testing

## Version Compatibility

### Recommended Versions (as of 2024)

| Component | Version |
|-----------|---------|
| Kotlin | 1.9.22 |
| Compose BOM | 2024.02.00 |
| Compose Compiler | 1.5.8 |
| Gradle | 8.4 |
| AGP | 8.2.0 |
| minSdk | 31 (adjust per Phase 0) |
| targetSdk | 34 |
| JDK | 17 |

### Checking for Updates
- [Compose Releases](https://developer.android.com/jetpack/androidx/releases/compose)
- [Kotlin Releases](https://kotlinlang.org/docs/releases.html)
- [Gradle Releases](https://gradle.org/releases/)

## Quick Reference

### ADB Commands
```bash
# Device management
adb devices -l              # List connected devices
adb kill-server             # Stop ADB server
adb start-server            # Start ADB server

# App installation
adb install -r <apk>        # Install APK
adb uninstall <package>     # Uninstall app

# App management
adb shell am start -n <package>/<activity>  # Launch app
adb shell am force-stop <package>           # Force stop app
adb shell pm clear <package>                # Clear app data

# Debugging
adb logcat                  # View logs
adb logcat -c               # Clear logs
adb shell screencap -p > screen.png         # Screenshot

# Device info
adb shell getprop ro.product.model          # Model name
adb shell getprop ro.build.version.release  # Android version
adb shell wm size                           # Screen resolution
```

### Gradle Commands
```bash
./gradlew assembleDebug     # Build debug APK
./gradlew assembleRelease   # Build release APK
./gradlew clean             # Clean build
./gradlew test              # Run unit tests
./gradlew dependencies      # Show dependencies
```
