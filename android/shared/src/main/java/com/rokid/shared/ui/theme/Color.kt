package com.rokid.shared.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Color palette for Rokid RV101 green monochrome display.
 * Since the display is monochrome green, we use brightness levels.
 */
object GlassesColors {
    val background = Color.Black
    val foreground = Color.White
    val accent = Color(0xFF00FF00)      // Green for emphasis
    val error = Color(0xFFFF6B6B)       // Soft red for errors
    val warning = Color(0xFFFFEB3B)     // Yellow for warnings
    val dim = Color(0xFF666666)         // Dimmed text (40% brightness)
    val veryDim = Color(0xFF404040)     // Very dim text (25% brightness) - for ghosting-sensitive areas
    val secondary = Color(0xFFCCCCCC)   // Secondary text (80% brightness)
}
