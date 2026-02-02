package com.rokid.shared.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A list that handles DPAD navigation automatically.
 *
 * Up/Down changes selection, Enter triggers onSelect callback.
 *
 * Usage:
 * ```
 * var selectedIndex by remember { mutableIntStateOf(0) }
 *
 * DpadFocusableList(
 *     items = agents,
 *     selectedIndex = selectedIndex,
 *     onIndexChange = { selectedIndex = it },
 *     onSelect = { index -> navigateToDetail(agents[index]) }
 * ) { item, focused ->
 *     GlassesListItem(
 *         title = item.name,
 *         subtitle = item.status,
 *         focused = focused
 *     )
 * }
 * ```
 */
@Composable
fun <T> DpadFocusableList(
    items: List<T>,
    selectedIndex: Int,
    onIndexChange: (Int) -> Unit,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    itemContent: @Composable (item: T, focused: Boolean) -> Unit
) {
    Column(
        modifier = modifier
            .dpadNavigation(
                onUp = {
                    if (selectedIndex > 0) onIndexChange(selectedIndex - 1)
                },
                onDown = {
                    if (selectedIndex < items.lastIndex) onIndexChange(selectedIndex + 1)
                },
                onSelect = { onSelect(selectedIndex) },
                onBack = onBack
            )
    ) {
        items.forEachIndexed { index, item ->
            itemContent(item, index == selectedIndex)
        }
    }
}
