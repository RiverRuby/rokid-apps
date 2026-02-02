package com.rokid.hellohud

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import com.rokid.shared.ui.components.ConnectionState
import com.rokid.shared.ui.components.ConnectionStatusBanner
import com.rokid.shared.ui.components.GlassesButton
import com.rokid.shared.ui.components.dpadNavigation
import com.rokid.shared.ui.theme.GlassesColors
import com.rokid.shared.ui.theme.GlassesFonts
import com.rokid.shared.ui.theme.GlassesTheme
import com.rokid.shared.ui.theme.GlassesTypography
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "HelloHUD"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate - HelloHUD starting")

        // Initialize fonts before setContent
        GlassesFonts.init(this)

        setContent {
            GlassesTheme {
                HelloHUDScreen()
            }
        }
    }
}

@Composable
fun HelloHUDScreen() {
    var counter by remember { mutableIntStateOf(0) }
    var buttonFocused by remember { mutableIntStateOf(0) }
    val focusRequester = remember { FocusRequester() }
    val activity = LocalContext.current as? Activity

    // Request focus on start
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // Tick counter every 2 seconds
    LaunchedEffect(Unit) {
        while (true) {
            Log.d("HelloHUD", "tick - counter: $counter")
            delay(2000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GlassesColors.background)
            .focusRequester(focusRequester)
            .focusable()
            .dpadNavigation(
                onLeft = {
                    buttonFocused = (buttonFocused - 1 + 3) % 3
                    Log.d("HelloHUD", "Focus: $buttonFocused")
                },
                onRight = {
                    buttonFocused = (buttonFocused + 1) % 3
                    Log.d("HelloHUD", "Focus: $buttonFocused")
                },
                onSelect = {
                    when (buttonFocused) {
                        0 -> {
                            counter++
                            Log.d("HelloHUD", "Incremented counter: $counter")
                        }
                        1 -> {
                            counter = 0
                            Log.d("HelloHUD", "Reset counter")
                        }
                        2 -> {
                            Log.d("HelloHUD", "Exit pressed")
                            activity?.finish()
                        }
                    }
                },
                onBack = {
                    Log.d("HelloHUD", "Back pressed - exiting")
                    activity?.finish()
                }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header - no padding, using dim color to avoid ghosting
            ConnectionStatusBanner(
                state = ConnectionState.Connected,
                hostInfo = "HelloHUD v1.0"
            )

            // Main content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "HELLO RV101",
                    style = GlassesTypography.displayLarge
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Counter: $counter",
                    style = GlassesTypography.bodyLarge
                )
            }

            // Action buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GlassesButton(
                        text = "+1",
                        onClick = { counter++ },
                        focused = buttonFocused == 0
                    )
                    GlassesButton(
                        text = "RESET",
                        onClick = { counter = 0 },
                        focused = buttonFocused == 1
                    )
                    // EXIT button with outline style (green border, no fill, green text)
                    Box(
                        modifier = Modifier
                            .border(
                                width = 2.dp,
                                color = if (buttonFocused == 2) GlassesColors.foreground else GlassesColors.accent
                            )
                            .background(
                                if (buttonFocused == 2) GlassesColors.accent else GlassesColors.background
                            )
                            .clickable { activity?.finish() }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "EXIT",
                            style = GlassesTypography.labelLarge,
                            color = if (buttonFocused == 2) GlassesColors.background else GlassesColors.accent
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "← → Navigate  TAP: Select  BACK: Exit",
                    style = GlassesTypography.labelSmall
                )
            }
        }
    }
}
