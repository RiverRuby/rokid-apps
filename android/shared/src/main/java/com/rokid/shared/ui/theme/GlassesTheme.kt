package com.rokid.shared.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * Standard theme wrapper for all Rokid glasses apps.
 *
 * Sets up Material3 with appropriate colors and typography for
 * the RV101's green monochrome display.
 *
 * IMPORTANT: Call GlassesFonts.init(context) in your Activity's onCreate
 * BEFORE setContent {} to ensure fonts are loaded.
 *
 * Usage:
 * ```
 * class MainActivity : ComponentActivity() {
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *         GlassesFonts.init(this)  // Initialize fonts first!
 *         setContent {
 *             GlassesTheme {
 *                 // Your composables here
 *             }
 *         }
 *     }
 * }
 * ```
 */
@Composable
fun GlassesTheme(content: @Composable () -> Unit) {
    val colorScheme = darkColorScheme(
        background = GlassesColors.background,
        surface = GlassesColors.background,
        onBackground = GlassesColors.foreground,
        onSurface = GlassesColors.foreground,
        primary = GlassesColors.accent,
        onPrimary = GlassesColors.background,
        secondary = GlassesColors.secondary,
        onSecondary = GlassesColors.background,
        error = GlassesColors.error,
        onError = GlassesColors.background
    )

    val typography = Typography(
        displayLarge = GlassesTypography.displayLarge,
        headlineLarge = GlassesTypography.headlineLarge,
        bodyLarge = GlassesTypography.bodyLarge,
        labelLarge = GlassesTypography.labelLarge,
        labelSmall = GlassesTypography.labelSmall
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
