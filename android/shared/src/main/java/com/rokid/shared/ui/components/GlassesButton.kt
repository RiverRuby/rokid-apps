package com.rokid.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rokid.shared.ui.theme.GlassesTypography

/**
 * Button styled for glasses display with clear focus/selected states.
 *
 * States:
 * - Normal: 20% white background, white text
 * - Focused: White background, black text (inverted)
 * - Disabled: 30% gray background, reduced opacity
 *
 * Usage:
 * ```
 * GlassesButton(
 *     text = "APPROVE",
 *     onClick = { viewModel.approve() },
 *     focused = index == selectedIndex
 * )
 * ```
 */
@Composable
fun GlassesButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    focused: Boolean = false,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .background(
                when {
                    !enabled -> Color.Gray.copy(alpha = 0.3f)
                    focused -> Color.White
                    else -> Color.White.copy(alpha = 0.2f)
                }
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = text,
            style = GlassesTypography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = when {
                    !enabled -> Color.Gray
                    focused -> Color.Black
                    else -> Color.White
                }
            )
        )
    }
}
