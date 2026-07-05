package com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.prefs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.domain.service.suggestions.HashtagMentionsSuggestionsManager
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.basic.SettingPref
import com.daniebeler.pfpixelix.ui.composables.widgets.SuggestionsBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.are_you_sure_you_want_to_log_out
import pixelix.app.generated.resources.cancel
import pixelix.app.generated.resources.caption_template
import pixelix.app.generated.resources.edit
import pixelix.app.generated.resources.logout
import pixelix.app.generated.resources.logout_questionmark
import pixelix.app.generated.resources.reply
import pixelix.app.generated.resources.save
import pixelix.app.generated.resources.task_edit

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CaptionTemplate(suggestionsManager: HashtagMentionsSuggestionsManager) {
    val showAlert = remember { mutableStateOf(false) }

    val prefs = LocalAppComponent.current.preferences
    val state = remember { mutableStateOf(TextFieldValue(prefs.captionTemplate)) }
    val scope = rememberCoroutineScope()

    if (showAlert.value) {
        CaptionDialog(state.value, {
            state.value = it
            suggestionsManager.changeText(it, scope)
        }, {
            state.value = it
            prefs.captionTemplate = it.text
            showAlert.value = false
        }, { showAlert.value = false }, suggestionsManager)
    }

    SettingPref(
        icon = Res.drawable.task_edit,
        title = stringResource(Res.string.caption_template),
        trailingContent = null,
        onClick = { showAlert.value = true },
        shapes = ListItemDefaults.segmentedShapes(index = 0, count = 2),
    )
}

@Composable
fun CaptionDialog(
    captionTemplate: TextFieldValue,
    onChange: (TextFieldValue) -> Unit,
    onSave: (TextFieldValue) -> Unit,
    onDismiss: () -> Unit,
    suggestionsManager: HashtagMentionsSuggestionsManager
) {
    val suggestionsState by suggestionsManager.suggestionsState.collectAsStateWithLifecycle()

    Dialog(
        onDismissRequest = onDismiss, properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .imePadding()
        ) {
            Surface(
                modifier = Modifier.align(Alignment.Center).padding(24.dp)
                    .widthIn(max = 400.dp),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(Res.string.caption_template),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = captionTemplate,
                        singleLine = false,
                        onValueChange = onChange,
                        shape = MaterialTheme.shapes.medium,
                        placeholder = { Text(stringResource(Res.string.caption_template)) },
                        modifier = Modifier.fillMaxWidth().onFocusChanged { focusState ->
                            suggestionsManager.onFocusChanged(focusState.isFocused)
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) { Text(stringResource(Res.string.cancel)) }
                        TextButton(
                            onClick = {
                                onSave(captionTemplate)
                            }
                        ) { Text(stringResource(Res.string.save)) }
                    }
                }
            }

            if (suggestionsManager.suggestionsOpen) {
                Box(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                ) {
                    SuggestionsBar(
                        state = suggestionsState,
                        bottomBarPadding = false,
                        onSelected = { selected ->
                            onChange(
                                suggestionsManager.selectSuggestion(
                                    selected, captionTemplate
                                )
                            )
                        })
                }
            }
        }
    }
}