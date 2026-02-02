package com.rokid.shared.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.rokid.shared.ui.theme.GlassesTypography
import kotlinx.coroutines.delay

/**
 * Simple text-based loading indicator for glasses display.
 *
 * Shows "Loading." -> "Loading.." -> "Loading..." animation.
 * Works well on monochrome display without requiring graphics.
 */
@Composable
fun GlassesLoadingIndicator(
    modifier: Modifier = Modifier,
    text: String = "Loading"
) {
    var dots by remember { mutableIntStateOf(1) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            dots = (dots % 3) + 1
        }
    }

    Text(
        text = text + ".".repeat(dots),
        style = GlassesTypography.bodyLarge,
        modifier = modifier
    )
}
