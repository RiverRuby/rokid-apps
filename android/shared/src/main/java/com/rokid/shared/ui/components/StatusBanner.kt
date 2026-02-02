package com.rokid.shared.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rokid.shared.ui.theme.GlassesColors
import com.rokid.shared.ui.theme.GlassesTypography

/**
 * Connection states for network-aware apps.
 */
enum class ConnectionState {
    Connected,
    Connecting,
    Disconnected,
    Error
}

/**
 * Reusable banner for showing network/connection status.
 *
 * Uses text + simple symbols since the display is monochrome:
 * - Connected: ● (filled circle) in accent color
 * - Connecting: ○ (empty circle) in warning color
 * - Disconnected: ● in dim color
 * - Error: ● in error color
 *
 * Usage:
 * ```
 * ConnectionStatusBanner(
 *     state = connectionState,
 *     hostInfo = "192.168.1.100:8787",
 *     latencyMs = 42
 * )
 * ```
 */
@Composable
fun ConnectionStatusBanner(
    state: ConnectionState,
    hostInfo: String = "",
    latencyMs: Int? = null,
    modifier: Modifier = Modifier
) {
    // Use very dim color (~25% brightness) to minimize waveguide ghosting artifact
    val (text, color) = when (state) {
        ConnectionState.Connected ->
            "● Connected${latencyMs?.let { " ${it}ms" } ?: ""}" to GlassesColors.veryDim

        ConnectionState.Connecting ->
            "○ Connecting..." to GlassesColors.dim

        ConnectionState.Disconnected ->
            "○ Disconnected" to GlassesColors.dim

        ConnectionState.Error ->
            "● Error" to GlassesColors.error
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            style = GlassesTypography.labelLarge,
            color = color
        )
        if (hostInfo.isNotEmpty()) {
            Text(
                text = hostInfo,
                style = GlassesTypography.labelLarge,
                color = GlassesColors.dim
            )
        }
    }
}
