package com.daniebeler.pfpixelix.ui.composables.widgets

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.character_count

@Composable
fun MaxLengthTextField(
    submit: (text: String) -> Unit,
    value: TextFieldValue,
    onValueChange: (newText: TextFieldValue) -> Unit,
    label: StringResource,
    modifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Default,
    maxLength: Int? = null,
    minLines: Int = 1,
    shape: CornerBasedShape = MaterialTheme.shapes.medium,
    colors: TextFieldColors = TextFieldDefaults.colors(
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        focusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
    ),
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    Row(verticalAlignment = Alignment.Bottom, modifier = modifier) {
        TextField(
            value = value,
            singleLine = false,
            onValueChange = {
                onValueChange(it)
            },
            shape = shape,
            label = { Text(stringResource(label)) },
            placeholder = { Text(stringResource(label)) },
            modifier = textFieldModifier.heightIn(max = 200.dp),
            colors = colors,
            minLines = minLines,
            keyboardOptions = KeyboardOptions(imeAction = imeAction),
            keyboardActions = KeyboardActions(onDone = {
                if ((maxLength ?: Int.MAX_VALUE) <= value.text.length && value.text.isNotEmpty()) {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    submit(value.text)
                }
            })
        )
    }
    if (maxLength != null) {
        if (value.text.length > maxLength - 30
        ) {
            Text(
                text = stringResource(
                    Res.string.character_count,
                    value.text.length,
                    maxLength
                ),
                style = MaterialTheme.typography.labelSmall,
                color = if (value.text.length > maxLength)
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
