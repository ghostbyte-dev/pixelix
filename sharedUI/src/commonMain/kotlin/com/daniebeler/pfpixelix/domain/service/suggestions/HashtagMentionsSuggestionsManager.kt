package com.daniebeler.pfpixelix.domain.service.suggestions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getTextAfterSelection
import androidx.compose.ui.text.input.getTextBeforeSelection
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.ui.composables.post.SuggestionsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject

class HashtagMentionsSuggestionsManager @Inject constructor(
    private val exploreService: ExploreService
) {
    var suggestionsOpen by mutableStateOf(false)
    private val _suggestionsState = MutableStateFlow(SuggestionsState())
    val suggestionsState: StateFlow<SuggestionsState> = _suggestionsState.asStateFlow()
    val regex = Regex("\\B((#\\w*)|(@\\w+(@[\\w.-]*)?))$")
    private var searchJob: kotlinx.coroutines.Job? = null

    fun changeText(newText: TextFieldValue, scope: CoroutineScope) {
        val textBeforeSelection = newText.getTextBeforeSelection(9999).toString()
        val result = regex.find(textBeforeSelection)

        if (result == null) {
            suggestionsOpen = false
            return
        }
        suggestionsOpen = result.range.first != result.range.last
        search(result.value, scope)
    }

    private fun search(searchString: String?, scope: CoroutineScope) {
        searchJob?.cancel()
        if (searchString == null) {
            return
        }
        val type = if (searchString.toCharArray().first() == '@') {
            "accounts"
        } else {
            "tags"
        }
        val searchShortened = searchString.substring(1)
        searchJob = exploreService.search(searchShortened, limit = 10).onEach { result ->
            _suggestionsState.update { currentState ->
                when (result) {
                    is Resource.Success -> {
                        SuggestionsState(
                            suggestions = if (type == "accounts") {
                                result.data.accounts.map { "@" + it.acct }
                            } else {
                                result.data.tags.map { "#" + it.name }
                            }
                        )
                    }

                    is Resource.Error -> {
                        SuggestionsState(
                            error = result.message
                        )
                    }

                    is Resource.Loading -> {
                        currentState.copy(isLoading = true)
                    }
                }
            }
        }.launchIn(scope)
    }

    fun selectSuggestion(suggestion: String, textFieldValue: TextFieldValue): TextFieldValue {
        _suggestionsState.update { SuggestionsState() }
        val textBeforeSelection = textFieldValue.getTextBeforeSelection(9999).toString()

        val match = regex.find(textBeforeSelection)

        if (match != null) {
            val startIndex = match.range.first
            val newTextBeforeSelection = textBeforeSelection.substring(0, startIndex) + suggestion

            return textFieldValue.copy(
                text = newTextBeforeSelection + textFieldValue.getTextAfterSelection(9999).toString(),
                selection = TextRange(newTextBeforeSelection.length)
            )
        }
        return textFieldValue
    }

    fun onFocusChanged(isFocused: Boolean) {
        if (!isFocused) {
            suggestionsOpen = false
        }
    }
}