package com.rokid.shared.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * A container that shows clear focus state for glasses UI.
 *
 * Focus must be highly visible on glasses - no subtle hover effects.
 *
 * Focus states:
 * - Unfocused: No border, transparent background
 * - Focused: 3dp white border + 10% white fill
 *
 * For selected/active state, use GlassesButton or custom inverted styling.
 *
 * Usage:
 * ```
 * items.forEachIndexed { index, item ->
 *     FocusableItem(focused = index == selectedIndex) {
 *         Text(item.name)
 *     }
 * }
 * ```
 */
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
