package com.daniebeler.pfpixelix.ui.composables.textfield_mentions

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
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

    fun changeText(newText: TextFieldValue) {
        text = newText
        val regex = Regex("\\B([@#])\\w*$")

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
                        error = result.message ?: "An unexpected error occurred"
                    )
                }

                is Resource.Loading -> {
                    mentionSuggestions.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun clickMention(acct: String) {
        val index = text.text.lastIndexOf("@") + 1
        val newText = text.text.substring(0, index) + acct
        text = text.copy(text = newText, selection = TextRange(newText.length))

    }
}