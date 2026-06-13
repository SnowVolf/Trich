package com.example.ui.thread.composition

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

import androidx.compose.ui.res.stringResource
import com.example.R

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.ui.graphics.Brush
import androidx.compose.runtime.LaunchedEffect

@Composable
fun PostingBar(
    draftText: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(draftText)) }

    LaunchedEffect(draftText) {
        if (textFieldValue.text != draftText) {
            textFieldValue = TextFieldValue(draftText)
        }
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background.copy(alpha = 0.7f), MaterialTheme.colorScheme.background.copy(alpha = 0.95f))
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(gradient)
            .padding(WindowInsets.navigationBars.asPaddingValues())
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f))
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                BBCodeButton(text = "B", onClick = {
                    insertBBCode(textFieldValue, "[b]", "[/b]") { newTfv ->
                        textFieldValue = newTfv
                        onTextChange(newTfv.text)
                    }
                })
                BBCodeButton(text = "I", onClick = {
                    insertBBCode(textFieldValue, "[i]", "[/i]") { newTfv ->
                        textFieldValue = newTfv
                        onTextChange(newTfv.text)
                    }
                })
                BBCodeButton(text = "S", onClick = {
                    insertBBCode(textFieldValue, "[s]", "[/s]") { newTfv ->
                        textFieldValue = newTfv
                        onTextChange(newTfv.text)
                    }
                })
                BBCodeButton(text = "SPOILER", onClick = {
                    insertBBCode(textFieldValue, "[spoiler]", "[/spoiler]") { newTfv ->
                        textFieldValue = newTfv
                        onTextChange(newTfv.text)
                    }
                })
            }

            Row(verticalAlignment = Alignment.Bottom) {
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = {
                        textFieldValue = it
                        onTextChange(it.text)
                    },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(stringResource(R.string.message_placeholder)) },
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )

                IconButton(
                    onClick = onSendClick,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = stringResource(R.string.send_button),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun BBCodeButton(text: String, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(text)
    }
}

fun insertBBCode(
    textFieldValue: TextFieldValue,
    tagStart: String,
    tagEnd: String,
    onValueChange: (TextFieldValue) -> Unit
) {
    val text = textFieldValue.text
    val selection = textFieldValue.selection
    val newText = text.substring(0, selection.start) +
            tagStart +
            text.substring(selection.start, selection.end) +
            tagEnd +
            text.substring(selection.end)
    
    val newCursorPosition = selection.end + tagStart.length + tagEnd.length
    onValueChange(
        TextFieldValue(text = newText, selection = androidx.compose.ui.text.TextRange(newCursorPosition))
    )
}
