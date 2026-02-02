package com.rokid.shared.ui.theme

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import androidx.compose.ui.text.font.FontFamily

/**
 * Font definitions for Rokid glasses apps.
 *
 * Selected fonts (tested on RV101 display 2026-02-01):
 * - Primary: JetBrains Mono - Clean monospace, excellent readability, tech aesthetic
 * - Accent: Space Grotesk - Distinctive geometric sans for headers
 *
 * IMPORTANT: Load fonts from assets/ using Typeface.createFromAsset().
 * The res/font resource approach has known issues with Jetpack Compose
 * causing crashes on some devices.
 */
object GlassesFonts {
    private const val TAG = "GlassesFonts"

    private var _primaryFont: FontFamily? = null
    private var _accentFont: FontFamily? = null
    private var initialized = false

    /**
     * Primary UI font (JetBrains Mono) - used for body text, labels, code
     * Falls back to system monospace if loading fails
     */
    val primary: FontFamily
        get() = _primaryFont ?: FontFamily.Monospace

    /**
     * Accent/header font (Space Grotesk) - used for titles, headers
     * Falls back to system default if loading fails
     */
    val accent: FontFamily
        get() = _accentFont ?: FontFamily.Default

    /**
     * Initialize fonts from assets. Call this early in your Activity's onCreate.
     * Safe to call multiple times - will only load once.
     */
    fun init(context: Context) {
        if (initialized) return

        _primaryFont = loadFont(context, "fonts/jetbrains_mono_bold.ttf")
        _accentFont = loadFont(context, "fonts/space_grotesk_bold.ttf")
        initialized = true

        Log.d(TAG, "Fonts initialized - primary: ${_primaryFont != null}, accent: ${_accentFont != null}")
    }

    private fun loadFont(context: Context, path: String): FontFamily? {
        return try {
            val typeface = Typeface.createFromAsset(context.assets, path)
            FontFamily(androidx.compose.ui.text.font.Typeface(typeface))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load font: $path", e)
            null
        }
    }
}
