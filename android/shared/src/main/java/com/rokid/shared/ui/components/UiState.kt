package com.rokid.shared.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rokid.shared.ui.theme.GlassesColors
import com.rokid.shared.ui.theme.GlassesTypography

/**
 * Standard UI state wrapper for async operations.
 */
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val retry: (() -> Unit)? = null) : UiState<Nothing>()
}

/**
 * Container that handles Loading/Error/Success states with consistent UI.
 *
 * Usage:
 * ```
 * val state by viewModel.state.collectAsState()
 *
 * UiStateContainer(
 *     state = state,
 *     onRetry = { viewModel.refresh() }
 * ) { data ->
 *     // Render success content
 *     TaskCard(task = data)
 * }
 * ```
 */
@Composable
fun <T> UiStateContainer(
    state: UiState<T>,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {},
    content: @Composable (T) -> Unit
) {
    when (state) {
        is UiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                GlassesLoadingIndicator()
            }
        }

        is UiState.Error -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "✕ Error",
                    style = GlassesTypography.headlineLarge,
                    color = GlassesColors.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = state.message,
                    style = GlassesTypography.bodyLarge,
                    color = GlassesColors.foreground,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                GlassesButton(
                    text = "RETRY",
                    onClick = { state.retry?.invoke() ?: onRetry() },
                    focused = true
                )
            }
        }

        is UiState.Success -> {
            content(state.data)
        }
    }
}
