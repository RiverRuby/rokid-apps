# UI Guidelines for Rokid RV101

Design patterns and best practices for the RV101's green monochrome waveguide display.

## Display Characteristics

### Hardware Constraints
- **Display Type**: Green monochrome waveguide
- **Color Range**: Grayscale only (no RGB)
- **Viewing Distance**: ~1-2 meters perceived depth
- **Field of View**: Limited peripheral coverage
- **Ambient Light**: Display competes with environment

### What This Means
- No color differentiation - use **size, position, brightness** instead
- High contrast required for visibility
- Large text for readability at arm's length perception
- Minimal information density

## Typography Scale

| Use Case | Size | Weight | Example |
|----------|------|--------|---------|
| Screen Title | 36sp | Bold | "AGENTS", "NOW" |
| Section Header | 28sp | SemiBold | "Status:", "Today's Tasks" |
| Body Text | 22-24sp | Normal | Task descriptions, summaries |
| Labels | 18sp | Medium | Button text, timestamps |
| Minimum | 16sp | - | Only for non-critical info |

### Font Selection (Tested 2026-02-01)

Fonts tested on actual RV101 display. Selected based on readability and HUD aesthetic:

| Use Case | Font | Rationale |
|----------|------|-----------|
| **Primary UI** | JetBrains Mono | Clean monospace, excellent readability, tech/HUD aesthetic |
| **Headers/Accent** | Space Grotesk | Distinctive geometric sans, good contrast with mono |

**Rejected alternatives:**
- Roboto (default) - too generic, doesn't fit HUD aesthetic
- Inter - too similar to Roboto on small display
- Space Mono - less readable than JetBrains Mono

### Font Loading

**Important:** Use `assets/fonts/` folder with `Typeface.createFromAsset()` instead of `res/font` resources. The `res/font` approach has known issues with Jetpack Compose causing crashes.

```kotlin
// ✅ Correct: Load from assets
val typeface = Typeface.createFromAsset(context.assets, "fonts/jetbrains_mono_bold.ttf")
val fontFamily = FontFamily(androidx.compose.ui.text.font.Typeface(typeface))

// ❌ Avoid: res/font resources in Compose (can cause crashes)
// FontFamily(Font(R.font.jetbrains_mono_bold))
```

### Code Example
```kotlin
object GlassesTypography {
    // Load fonts from assets in your Activity/Application
    lateinit var primaryFont: FontFamily  // JetBrains Mono
    lateinit var accentFont: FontFamily   // Space Grotesk

    val screenTitle = TextStyle(fontSize = 36.sp, fontWeight = Bold)
    val sectionHeader = TextStyle(fontSize = 28.sp, fontWeight = SemiBold)
    val body = TextStyle(fontSize = 22.sp, fontWeight = Normal)
    val label = TextStyle(fontSize = 18.sp, fontWeight = Medium)
}
```

## Color Palette

Since the display is monochrome green, we use brightness levels:

| Element | Color Value | Brightness |
|---------|-------------|------------|
| Background | `#000000` (Black) | 0% |
| Primary Text | `#FFFFFF` (White) | 100% |
| Secondary Text | `#CCCCCC` | 80% |
| Dimmed Text | `#666666` | 40% |
| Accent/Highlight | `#00FF00` (Green) | Full |
| Error Indication | `#FF6B6B` | High |
| Warning | `#FFEB3B` | High |

### Usage Rules
1. **Always use black background** - maximizes contrast
2. **Primary content in white** - highest visibility
3. **Use green sparingly** - for emphasis only
4. **Error states** - use brightness/size, not just color

## Layout Principles

### Information Density
- **Maximum 4-6 items visible** at once
- **One primary action per screen** when possible
- **Progressive disclosure** - show more on demand

### Spacing
```kotlin
object GlassesSpacing {
    val screenPadding = 24.dp
    val sectionGap = 20.dp
    val itemGap = 12.dp
    val inlineGap = 8.dp
}
```

### Screen Structure
```
┌─────────────────────────────────────┐
│ [Header: Title + Status]  24dp pad │
├─────────────────────────────────────┤
│                                     │
│ [Content Area]                      │
│  - Max 4-6 visible items            │
│  - Scroll indicator if more         │
│                                     │
├─────────────────────────────────────┤
│ [Footer: Actions/Navigation hints]  │
└─────────────────────────────────────┘
```

## DPAD Navigation

### Input Model
The RV101 uses a touchpad on the temple that translates to DPAD events:
- **Swipe Up/Down**: `Key.DirectionUp` / `Key.DirectionDown`
- **Swipe Left/Right**: `Key.DirectionLeft` / `Key.DirectionRight`
- **Tap**: `Key.Enter` or `Key.DirectionCenter`
- **Long Press/Back**: `Key.Back`

### Focus States

Focus must be **highly visible** - no subtle hover effects.

```kotlin
@Composable
fun FocusableItem(
    focused: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .border(
                width = if (focused) 3.dp else 0.dp,
                color = if (focused) Color.White else Color.Transparent
            )
            .background(
                if (focused) Color.White.copy(alpha = 0.1f) else Color.Transparent
            )
            .padding(12.dp)
    ) {
        content()
    }
}
```

### Focus Indicators
| State | Visual Treatment |
|-------|-----------------|
| Unfocused | No border, transparent background |
| Focused | 3dp white border + 10% white fill |
| Selected/Active | Inverted (white background, black text) |
| Disabled | 40% opacity |

### Navigation Hints
Always show navigation instructions at screen bottom:
```
↑↓ Navigate  ENTER: Select  BACK: Return
```

## Component Patterns

### List Items
```
┌─────────────────────────────────────┐
│ [Icon/Status]  Title                │
│                Description line     │
└─────────────────────────────────────┘
```

```kotlin
@Composable
fun GlassesListItem(
    title: String,
    subtitle: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    focused: Boolean = false
) {
    FocusableItem(focused = focused) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.invoke()
            if (leadingIcon != null) Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = GlassesTypography.body,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = GlassesTypography.label,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
```

### Buttons
```kotlin
@Composable
fun GlassesButton(
    text: String,
    onClick: () -> Unit,
    focused: Boolean = false,
    enabled: Boolean = true
) {
    Box(
        modifier = Modifier
            .background(
                when {
                    !enabled -> Color.Gray.copy(alpha = 0.3f)
                    focused -> Color.White
                    else -> Color.White.copy(alpha = 0.2f)
                }
            )
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        Text(
            text = text,
            style = GlassesTypography.label,
            color = if (focused) Color.Black else Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}
```

### Status Indicators
Use text + simple symbols, not color:

| Status | Display |
|--------|---------|
| Connected | `● Connected` (filled circle) |
| Disconnected | `○ Disconnected` (empty circle) |
| Loading | `◐ Loading...` (half circle or dots) |
| Error | `✕ Error` (X mark) |
| Success | `✓ Done` (checkmark) |

### Progress Indicators

**Progress Bar**:
```
████████░░░░░ 67%
```

**Loading State**:
```kotlin
@Composable
fun GlassesLoadingIndicator() {
    val dots = remember { mutableStateOf(1) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            dots.value = (dots.value % 3) + 1
        }
    }

    Text(
        text = "Loading" + ".".repeat(dots.value),
        style = GlassesTypography.body
    )
}
```

## Display Artifacts

### Waveguide Ghosting (Discovered 2026-02-01)
All elements show a faint "ghost" duplicate - a flipped, offset reflection caused by waveguide optics. This is a known hardware limitation of waveguide AR displays. **Ghost visibility scales with source brightness.**

| Brightness | Ghost Visibility | Use Case |
|------------|------------------|----------|
| 100% (white) | Very noticeable | Primary content only, accept ghosting |
| 80% (secondary) | Noticeable | Body text |
| 40% (dim) | Faint but visible | Labels, timestamps |
| **25% (veryDim)** | **Imperceptible** | **Status indicators, headers** |

**Guidelines:**
1. Use `GlassesColors.veryDim` (~25%) for status banners and peripheral info
2. Accept some ghosting for primary content that needs to be highly readable
3. Ghost follows the element (not position-fixed), so position doesn't help

## Anti-Patterns (Avoid These)

### Don't Do This
- ❌ Bright text for secondary info - causes visible ghosting (use veryDim)
- ❌ Small icons (< 24dp) - hard to see
- ❌ Color-dependent information - display is monochrome
- ❌ Dense layouts (> 6 items) - overwhelming
- ❌ Subtle focus states - must be obvious
- ❌ Fine details or thin lines - may not render
- ❌ Animations requiring color - stick to movement/opacity
- ❌ Complex gradients - may appear as solid blocks

### Do This Instead
- ✅ Large icons (32dp+) with high contrast
- ✅ Size and position for hierarchy
- ✅ Simple layouts with clear focus
- ✅ Bold borders for focus indication
- ✅ Simple shapes with thick strokes
- ✅ Fade/scale animations only
- ✅ Solid fills or simple patterns

## Testing Checklist

Before deploying UI changes:

- [ ] Text readable at arm's length (22sp+ for body)
- [ ] Focus state clearly visible (3dp+ border)
- [ ] Navigation works with DPAD only
- [ ] Information fits in 4-6 visible items
- [ ] Loading/error states show clear feedback
- [ ] No color-only information
- [ ] Tested on actual glasses (not just emulator)

## Screen Templates

### Simple Info Display
```
┌─────────────────────────────────────┐
│           SCREEN TITLE              │
├─────────────────────────────────────┤
│                                     │
│         Primary Info                │
│         Goes Here                   │
│                                     │
│         Secondary detail            │
│                                     │
├─────────────────────────────────────┤
│  [ACTION 1]    [ACTION 2]          │
└─────────────────────────────────────┘
```

### List View
```
┌─────────────────────────────────────┐
│ LIST TITLE              ● Status    │
├─────────────────────────────────────┤
│ ▶ Item 1                           │
│   Description                       │
├─────────────────────────────────────┤
│   Item 2                           │
│   Description                       │
├─────────────────────────────────────┤
│   Item 3                           │
│   Description                       │
├─────────────────────────────────────┤
│ ↑↓ Navigate  ENTER: Select         │
└─────────────────────────────────────┘
```

### Detail View
```
┌─────────────────────────────────────┐
│ ← ITEM TITLE                        │
├─────────────────────────────────────┤
│ Property: Value                     │
│ Property: Value                     │
├─────────────────────────────────────┤
│ Extended description text that      │
│ can span multiple lines and show    │
│ more detail about the item.         │
│                        [More ▼]     │
├─────────────────────────────────────┤
│ [PRIMARY ACTION]  [SECONDARY]      │
└─────────────────────────────────────┘
```

### Error State
```
┌─────────────────────────────────────┐
│           SCREEN TITLE              │
├─────────────────────────────────────┤
│                                     │
│         ✕ Error                     │
│                                     │
│    Connection failed. Check         │
│    network settings.                │
│                                     │
├─────────────────────────────────────┤
│          [RETRY]                    │
└─────────────────────────────────────┘
```
