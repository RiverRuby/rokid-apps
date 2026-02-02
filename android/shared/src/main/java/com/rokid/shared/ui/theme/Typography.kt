package com.rokid.shared.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Typography scale for Rokid RV101 glasses.
 *
 * Display constraints:
 * - Green monochrome waveguide display
 * - 480x640 resolution at 240dpi
 * - Viewing distance ~1-2 meters perceived depth
 * - Minimum readable: 16sp (non-critical info only)
 * - Recommended body: 22-24sp
 * - Headers: 28-36sp
 */
object GlassesTypography {

    /**
     * Large display text (36sp) - Screen titles like "AGENTS", "NOW"
     * Uses accent font (Space Grotesk)
     */
    val displayLarge: TextStyle
        get() = TextStyle(
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = GlassesFonts.accent,
            color = GlassesColors.foreground
        )

    /**
     * Headline text (28sp) - Section headers like "Status:", "Today's Tasks"
     * Uses accent font (Space Grotesk)
     */
    val headlineLarge: TextStyle
        get() = TextStyle(
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = GlassesFonts.accent,
            color = GlassesColors.foreground
        )

    /**
     * Body text (22sp) - Main content, task descriptions, summaries
     * Uses primary font (JetBrains Mono)
     */
    val bodyLarge: TextStyle
        get() = TextStyle(
            fontSize = 22.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = GlassesFonts.primary,
            color = GlassesColors.foreground
        )

    /**
     * Label text (18sp) - Button text, timestamps, secondary info
     * Uses primary font (JetBrains Mono)
     */
    val labelLarge: TextStyle
        get() = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = GlassesFonts.primary,
            color = GlassesColors.foreground
        )

    /**
     * Small text (16sp) - Non-critical info only
     * Uses primary font (JetBrains Mono)
     */
    val labelSmall: TextStyle
        get() = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            fontFamily = GlassesFonts.primary,
            color = GlassesColors.dim
        )
}
