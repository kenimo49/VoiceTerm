/*
 * ConnectBot: simple, powerful, open-source SSH client for Android
 * Copyright 2025 Kenny Root
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.connectbot.ui.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import org.connectbot.R
import org.connectbot.service.TerminalBridge
import org.connectbot.terminal.VTermKey


/**
 * Inline text input bar displayed below the TopAppBar.
 * Non-modal: terminal remains visible and interactive below.
 * Features:
 * - Compact single-row input with send button
 * - Checkbox to toggle appending Enter on send
 * - Full IME support (Japanese input, swipe typing, voice input)
 * - Enter preference persisted in SharedPreferences
 * - Built-in microphone button for SpeechRecognizer voice input
 * - Auto-restart of voice input after auto-send
 */
@Composable
fun FloatingTextInputDialog(
	bridge: TerminalBridge,
	autoSendTimeoutMs: Long = 0L,
	sendEnter: Boolean = true,
	initialText: String = "",
	onDismiss: () -> Unit
) {
	val context = LocalContext.current

	// Text state and focus
	var text by remember { mutableStateOf(initialText) }
	val focusRequester = remember { FocusRequester() }

	// Speech recognition state
	val speechAvailable = remember { SpeechRecognizer.isRecognitionAvailable(context) }
	var isListening by remember { mutableStateOf(false) }
	var micEnabled by remember { mutableStateOf(false) }

	val speechRecognizer = remember {
		if (speechAvailable) SpeechRecognizer.createSpeechRecognizer(context) else null
	}

	val recognitionIntent = remember {
		Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
			putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
			putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
		}
	}

	fun startListening() {
		speechRecognizer?.startListening(recognitionIntent)
		isListening = true
	}

	fun stopListening() {
		speechRecognizer?.stopListening()
		isListening = false
	}

	// Set up recognition listener
	DisposableEffect(speechRecognizer) {
		speechRecognizer?.setRecognitionListener(object : RecognitionListener {
			override fun onReadyForSpeech(params: Bundle?) {}
			override fun onBeginningOfSpeech() {}
			override fun onRmsChanged(rmsdB: Float) {}
			override fun onBufferReceived(buffer: ByteArray?) {}
			override fun onEndOfSpeech() {}

			override fun onPartialResults(partialResults: Bundle?) {
				val matches = partialResults
					?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
				if (!matches.isNullOrEmpty()) {
					text = matches[0]
				}
			}

			override fun onResults(results: Bundle?) {
				val matches = results
					?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
				if (!matches.isNullOrEmpty()) {
					text = matches[0]
				}
				isListening = false
			}

			override fun onError(error: Int) {
				isListening = false
				// Restart on transient errors if mic is still enabled
				if (micEnabled && error in listOf(
						SpeechRecognizer.ERROR_NO_MATCH,
						SpeechRecognizer.ERROR_SPEECH_TIMEOUT
					)
				) {
					startListening()
				}
			}

			override fun onEvent(eventType: Int, params: Bundle?) {}
		})

		onDispose {
			speechRecognizer?.destroy()
		}
	}

	// Permission launcher
	val permissionLauncher = rememberLauncherForActivityResult(
		ActivityResultContracts.RequestPermission()
	) { granted ->
		if (granted) {
			micEnabled = true
			startListening()
		}
	}

	// Request focus when shown
	LaunchedEffect(Unit) {
		focusRequester.requestFocus()
	}

	// Send text helper function
	fun sendText() {
		if (text.isNotEmpty()) {
			bridge.injectString(text)
			if (sendEnter) {
				bridge.terminalEmulator.dispatchKey(0, VTermKey.ENTER)
			}
			text = ""
		}
	}

	// Auto-send progress animation
	val autoSendProgress = remember { Animatable(0f) }

	// Auto-send debounce timer (only active when voice input is on)
	LaunchedEffect(text, autoSendTimeoutMs, micEnabled) {
		autoSendProgress.snapTo(0f)
		if (autoSendTimeoutMs > 0L && micEnabled && text.isNotEmpty()) {
			autoSendProgress.animateTo(
				targetValue = 1f,
				animationSpec = tween(
					durationMillis = autoSendTimeoutMs.toInt(),
					easing = LinearEasing
				)
			)
			sendText()
			if (micEnabled) {
				startListening()
			}
		}
	}

	// Single row: [TextField] [Mic] [Enter checkbox] [Send] [Close]
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(MaterialTheme.colorScheme.surface)
			.padding(horizontal = 4.dp, vertical = 2.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(4.dp)
	) {
		// Text input field with optional progress bar overlay
		Box(
			modifier = Modifier
				.weight(1f)
				.height(62.dp)
		) {
			TextField(
				value = text,
				onValueChange = { text = it },
				placeholder = {
					Text(
						stringResource(R.string.terminal_text_input_dialog_label),
						style = MaterialTheme.typography.bodySmall
					)
				},
				singleLine = true,
				keyboardOptions = KeyboardOptions(
					imeAction = ImeAction.Send
				),
				keyboardActions = KeyboardActions(
					onSend = { sendText() }
				),
				colors = TextFieldDefaults.colors(
					focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
					unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
					focusedIndicatorColor = Color.Transparent,
					unfocusedIndicatorColor = Color.Transparent
				),
				textStyle = MaterialTheme.typography.bodyMedium,
				shape = RoundedCornerShape(6.dp),
				modifier = Modifier
					.matchParentSize()
					.focusRequester(focusRequester)
			)

			// Auto-send progress bar at the bottom of the TextField
			if (autoSendTimeoutMs > 0L && micEnabled && text.isNotEmpty()) {
				LinearProgressIndicator(
					progress = { autoSendProgress.value },
					modifier = Modifier
						.fillMaxWidth()
						.height(3.dp)
						.align(Alignment.BottomCenter),
					color = MaterialTheme.colorScheme.primary,
					trackColor = Color.Transparent
				)
			}
		}

		// Microphone button (only shown if speech recognition is available)
		if (speechAvailable) {
			Box(
				contentAlignment = Alignment.Center,
				modifier = Modifier
					.size(28.dp)
					.clickable {
						if (isListening) {
							stopListening()
							micEnabled = false
						} else {
							val hasPermission = ContextCompat.checkSelfPermission(
								context,
								Manifest.permission.RECORD_AUDIO
							) == PackageManager.PERMISSION_GRANTED
							if (hasPermission) {
								micEnabled = true
								startListening()
							} else {
								permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
							}
						}
					}
			) {
				Icon(
					Icons.Default.Mic,
					contentDescription = stringResource(
						if (isListening) R.string.voice_input_mic_off
						else R.string.voice_input_mic_on
					),
					tint = if (isListening) MaterialTheme.colorScheme.error
					else MaterialTheme.colorScheme.onSurfaceVariant,
					modifier = Modifier.size(20.dp)
				)
			}
		}

		// Send button (compact, no minimum touch target padding)
		Box(
			contentAlignment = Alignment.Center,
			modifier = Modifier
				.size(28.dp)
				.background(
					MaterialTheme.colorScheme.primary,
					RoundedCornerShape(6.dp)
				)
				.clickable { sendText() }
		) {
			Icon(
				Icons.AutoMirrored.Filled.Send,
				contentDescription = stringResource(R.string.button_send),
				tint = MaterialTheme.colorScheme.onPrimary,
				modifier = Modifier.size(16.dp)
			)
		}

		// Close button (compact)
		Box(
			contentAlignment = Alignment.Center,
			modifier = Modifier
				.size(24.dp)
				.clickable { onDismiss() }
		) {
			Icon(
				Icons.Default.Close,
				contentDescription = stringResource(R.string.button_close),
				tint = MaterialTheme.colorScheme.onSurfaceVariant,
				modifier = Modifier.size(16.dp)
			)
		}
	}
}
