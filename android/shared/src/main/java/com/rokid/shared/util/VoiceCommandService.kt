package com.rokid.shared.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for voice command recognition across apps.
 *
 * Implement this to add voice control to your glasses app.
 * Commands are emitted as lowercase strings for easy matching.
 *
 * Usage:
 * ```
 * @Composable
 * fun MyScreen(voiceService: VoiceCommandService) {
 *     val isListening by voiceService.isListening.collectAsState()
 *
 *     LaunchedEffect(Unit) {
 *         voiceService.commands.collect { command ->
 *             when {
 *                 command.contains("done") -> markTaskDone()
 *                 command.contains("skip") -> skipTask()
 *                 command.contains("next") -> nextItem()
 *             }
 *         }
 *     }
 *
 *     // Show listening indicator
 *     if (isListening) {
 *         Text("🎤 Listening...")
 *     }
 * }
 * ```
 */
interface VoiceCommandService {
    /**
     * Whether voice recognition is currently active.
     */
    val isListening: StateFlow<Boolean>

    /**
     * Flow of recognized voice commands (lowercase strings).
     */
    val commands: Flow<String>

    /**
     * Start listening for voice commands.
     * No-op if already listening.
     */
    fun startListening()

    /**
     * Stop listening for voice commands.
     * Releases audio resources.
     */
    fun stopListening()
}
