package com.rokid.shared.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rokid.shared.ui.theme.GlassesTypography

/**
 * Standard list item for glasses apps.
 *
 * Layout:
 * ```
 * ┌─────────────────────────────────────┐
 * │ [Icon/Status]  Title                │
 * │                Description line     │
 * └─────────────────────────────────────┘
 * ```
 *
 * Usage:
 * ```
 * items.forEachIndexed { index, item ->
 *     GlassesListItem(
 *         title = item.name,
 *         subtitle = item.description,
 *         focused = index == selectedIndex,
 *         leadingIcon = { StatusDot(item.status) }
 *     )
 * }
 * ```
 */
@Composable
fun GlassesListItem(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    focused: Boolean = false
) {
    FocusableItem(
        focused = focused,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.invoke()
            if (leadingIcon != null) {
                Spacer(Modifier.width(12.dp))
            }
            Column {
                Text(
                    text = title,
                    style = GlassesTypography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = GlassesTypography.labelLarge.copy(
                        color = Color.White.copy(alpha = 0.7f)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
