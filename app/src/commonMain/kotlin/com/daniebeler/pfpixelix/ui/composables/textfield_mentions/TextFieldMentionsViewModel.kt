package com.daniebeler.pfpixelix.ui.composables.textfield_mentions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.getTextBeforeSelection
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.hashtag.SearchService
import com.daniebeler.pfpixelix.ui.composables.post.SuggestionsState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class TextFieldMentionsViewModel @Inject constructor(
    private val searchService: SearchService
) : ViewModel() {
    var text by mutableStateOf(TextFieldValue(""))
    var mentionsDropdownOpen by mutableStateOf(false)
    var mentionSuggestions by mutableStateOf(SuggestionsState())
    val regex = Regex("\\B((#\\w*)|(@\\w+(@[\\w.-]*)?))$")

    fun changeText(newText: TextFieldValue) {
        text = newText

        val textBeforeSelection = newText.getTextBeforeSelection(9999).toString()
        val result = regex.find(textBeforeSelection)

        if (result == null) {
            mentionsDropdownOpen = false
            return
        }
        mentionsDropdownOpen = result.range.first != result.range.last
        search(result.value)
    }

    private fun search(searchString: String?) {
        if (searchString == null) {
            return
        }
        val type = if (searchString.toCharArray().first() == '@') {"accounts"} else {"tags"}
        val searchShortened = searchString.substring(1)
        searchService.search(searchShortened).onEach { result ->
            mentionSuggestions = when (result) {
                is Resource.Success -> {
                    SuggestionsState(suggestions = if (type == "accounts") {result.data.accounts.map { "@" + it.acct }} else {result.data.tags.map { "#" + it.name }})
                }

                is Resource.Error -> {
                    SuggestionsState(
                        error = result.message
                    )
                }

                is Resource.Loading -> {
                    mentionSuggestions.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
}