package com.rokid.shared.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type

/**
 * Modifier for handling touchpad/DPAD input on Rokid glasses.
 *
 * The RV101 touchpad on the temple translates to DPAD events:
 * - Swipe Up/Down: Key.DirectionUp / Key.DirectionDown
 * - Swipe Left/Right: Key.DirectionLeft / Key.DirectionRight
 * - Tap: Key.Enter or Key.DirectionCenter
 * - Long Press/Back gesture: Key.Back
 *
 * Usage:
 * ```
 * Box(
 *     modifier = Modifier
 *         .focusable()
 *         .dpadNavigation(
 *             onUp = { selectedIndex-- },
 *             onDown = { selectedIndex++ },
 *             onSelect = { performAction() },
 *             onBack = { finish() }
 *         )
 * ) { ... }
 * ```
 */
fun Modifier.dpadNavigation(
    onUp: () -> Unit = {},
    onDown: () -> Unit = {},
    onLeft: () -> Unit = {},
    onRight: () -> Unit = {},
    onSelect: () -> Unit = {},
    onBack: () -> Unit = {}
): Modifier = this.onKeyEvent { event ->
    if (event.type != KeyEventType.KeyDown) return@onKeyEvent false

    when (event.key) {
        Key.DirectionUp -> {
            onUp()
            true
        }
        Key.DirectionDown -> {
            onDown()
            true
        }
        Key.DirectionLeft -> {
            onLeft()
            true
        }
        Key.DirectionRight -> {
            onRight()
            true
        }
        Key.Enter, Key.DirectionCenter -> {
            onSelect()
            true
        }
        Key.Back, Key.Escape -> {
            onBack()
            true
        }
        else -> false
    }
}
