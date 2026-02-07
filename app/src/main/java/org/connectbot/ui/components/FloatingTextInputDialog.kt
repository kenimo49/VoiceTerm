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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import org.connectbot.R
import org.connectbot.service.TerminalBridge
import org.connectbot.terminal.VTermKey

private const val PREF_SEND_ENTER = "floating_input_send_enter"

/**
 * Inline text input bar displayed below the TopAppBar.
 * Non-modal: terminal remains visible and interactive below.
 * Features:
 * - Compact single-row input with send button
 * - Checkbox to toggle appending Enter on send
 * - Full IME support (Japanese input, swipe typing, voice input)
 * - Enter preference persisted in SharedPreferences
 */
@Composable
fun FloatingTextInputDialog(
	bridge: TerminalBridge,
	initialText: String = "",
	onDismiss: () -> Unit
) {
	val context = LocalContext.current
	val prefs = remember { PreferenceManager.getDefaultSharedPreferences(context) }

	// Text state and focus
	var text by remember { mutableStateOf(initialText) }
	val focusRequester = remember { FocusRequester() }

	// Enter checkbox state
	var sendEnter by remember { mutableStateOf(prefs.getBoolean(PREF_SEND_ENTER, true)) }

	// Request focus when shown
	LaunchedEffect(Unit) {
		focusRequester.requestFocus()
	}

	// Save preferences when removed
	DisposableEffect(Unit) {
		onDispose {
			prefs.edit {
				putBoolean(PREF_SEND_ENTER, sendEnter)
			}
		}
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

	// Single row: [TextField] [Enter checkbox] [Send] [Close]
	Row(
		modifier = Modifier
			.fillMaxWidth()
			.background(MaterialTheme.colorScheme.surface)
			.padding(horizontal = 4.dp, vertical = 2.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(4.dp)
	) {
		// Text input field
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
				.weight(1f)
				.height(62.dp)
				.focusRequester(focusRequester)
		)

		// Enter checkbox
		Checkbox(
			checked = sendEnter,
			onCheckedChange = { sendEnter = it },
			modifier = Modifier.size(20.dp),
			colors = CheckboxDefaults.colors(
				checkedColor = MaterialTheme.colorScheme.primary,
				uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
			)
		)
		Text(
			text = stringResource(R.string.terminal_text_input_send_enter),
			style = MaterialTheme.typography.labelSmall,
			color = MaterialTheme.colorScheme.onSurface
		)

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
