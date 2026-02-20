package com.daniebeler.pfpixelix.ui.composables.textfield_mentions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getTextAfterSelection
import androidx.compose.ui.text.input.getTextBeforeSelection
import androidx.compose.ui.unit.dp
import com.daniebeler.pfpixelix.di.injectViewModel
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.character_count

@Composable
fun TextFieldMentionsComposable(
    submit: (text: String) -> Unit,
    text: TextFieldValue,
    changeText: (newText: TextFieldValue) -> Unit,
    labelStringId: StringResource,
    submitButton: (@Composable (enabled: Boolean) -> Unit)?,
    modifier: Modifier?,
    imeAction: ImeAction,
    suggestionsBoxColor: Color,
    viewModel: TextFieldMentionsViewModel = injectViewModel("textFieldMentionsViewModel") { textFieldMentionsViewModel },
    maxLength: Int? = null,
    colors: TextFieldColors = TextFieldDefaults.colors(
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
    ),
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    Column {
        Row(verticalAlignment = Alignment.Bottom) {
            TextField(
                value = text,
                singleLine = false,
                onValueChange = {
                    viewModel.changeText(it)
                    changeText(it)
                },
                placeholder = { Text(stringResource(labelStringId)) },
                modifier = Modifier.weight(1f).heightIn(max = 200.dp),
                shape = RoundedCornerShape(16.dp),
                colors = colors,
                keyboardOptions = KeyboardOptions(imeAction = imeAction),
                keyboardActions = KeyboardActions(onDone = {
                    if ((maxLength ?: Int.MAX_VALUE) <= text.text.length && text.text.isNotEmpty()) {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        submit(text.text)
                        viewModel.mentionsDropdownOpen = false
                    }
                })
            )


            if (submitButton != null) {
                Spacer(modifier = Modifier.width(12.dp))
                submitButton((maxLength ?: Int.MAX_VALUE) <= text.text.length && text.text.isNotEmpty())
            }
        }
        if (viewModel.mentionsDropdownOpen) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clip(shape = RoundedCornerShape(12.dp))
                    .background(suggestionsBoxColor)
            ) {
                if (viewModel.mentionSuggestions.suggestions.isNotEmpty()) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        viewModel.mentionSuggestions.suggestions.forEach {
                            TextButton(onClick = {
                                viewModel.mentionsDropdownOpen = false
                                val textBeforeSelection = text.getTextBeforeSelection(9999).toString()

                                val match = viewModel.regex.find(textBeforeSelection)

                                if (match != null) {
                                    val startIndex = match.range.first
                                    val newTextBeforeSelection = textBeforeSelection.substring(0, startIndex) + it

                                    changeText(
                                        text.copy(
                                            text = newTextBeforeSelection + text.getTextAfterSelection(9999).toString(),
                                            selection = TextRange(newTextBeforeSelection.length)
                                        )
                                    )
                                }
                            }) {
                                Text(
                                    text = it,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
            }
        }
        if (maxLength != null) {
            if (text.text.length > maxLength - 30
            ) {
                Text(
                    text = stringResource(
                        Res.string.character_count,
                        text.text.length,
                        maxLength
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (text.text.length > maxLength)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(
                        top = 4.dp,
                        start = 4.dp,
                        bottom = 4.dp
                    )
                )
            }
        }
    }
}