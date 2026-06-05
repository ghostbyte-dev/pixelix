package com.daniebeler.pfpixelix.domain.service.suggestions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.daniebeler.pfpixelix.domain.service.general.BackendType
import com.daniebeler.pfpixelix.domain.service.general.FediseaService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.ui.composables.post.SuggestionsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject

class ServersSuggestionsManager @Inject constructor(
    private val fediseaService: FediseaService,
) {
    var suggestionsOpen by mutableStateOf(false)
    private val _suggestionsState = MutableStateFlow(SuggestionsState())
    val suggestionsState: StateFlow<SuggestionsState> = _suggestionsState.asStateFlow()
    private var searchJob: Job? = null

    fun changeText(newText: TextFieldValue, platform: BackendType?, scope: CoroutineScope) {
        searchJob?.cancel()
        if (newText.text.isBlank() || platform == null) {
            suggestionsOpen = false
            return
        }
        search(newText.text, platform, scope)
    }

    private fun search(instanceSearch: String, platform: BackendType, scope: CoroutineScope) {
        searchJob =
            fediseaService.getOpenServers(instanceSearch, platform, limit = 10).onEach { result ->
                _suggestionsState.update { currentState ->
                    when (result) {
                        is Resource.Success -> {
                            suggestionsOpen = true
                            SuggestionsState(
                                suggestions = result.data.data.map { it.domain })
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

    fun selectSuggestion(suggestion: String): TextFieldValue {
        suggestionsOpen = false
        _suggestionsState.update { SuggestionsState() }
        return TextFieldValue(suggestion, TextRange(suggestion.length))
    }

    fun onFocusChanged(isFocused: Boolean) {
        if (!isFocused) {
            suggestionsOpen = false
        }
    }
}