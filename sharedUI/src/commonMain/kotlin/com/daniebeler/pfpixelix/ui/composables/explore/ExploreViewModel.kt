package com.daniebeler.pfpixelix.ui.composables.explore

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.model.SavedSearchItem
import com.daniebeler.pfpixelix.domain.service.capabilities.Capabilities
import com.daniebeler.pfpixelix.domain.service.general.AuthService
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.general.Session
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.domain.service.search.SavedSearchesService
import com.daniebeler.pfpixelix.ui.composables.profile.ViewEnum
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import kotlin.time.Duration.Companion.milliseconds

class ExploreViewModel @Inject constructor(
    private val exploreService: ExploreService,
    private val savedSearchesService: SavedSearchesService,
    private val authService: AuthService,
    private val prefs: UserPreferences,
    session: Session
) : ViewModel() {
    val capabilities: StateFlow<Capabilities> = session.capabilities
    var searchState by mutableStateOf(SearchState())
    var savedSearches by mutableStateOf<List<SavedSearchItem>>(emptyList())
        private set

    var isSwipeEnabled by mutableStateOf(true)
    private var resultsJob: Job? = null
    var view by mutableStateOf(ViewEnum.Grid)

    init {
        viewModelScope.launch {
            // 1. Observe the active user flow
            authService.activeUser.collect { accountId ->
                if (accountId != null) {
                    savedSearchesService.getSavedSearches(accountId).collect { list ->
                        savedSearches = list
                    }
                } else {
                    savedSearches = emptyList()
                }
            }
        }
        viewModelScope.launch {
            prefs.enableSwipeBetweenTabsFlow.collect { isSwipeEnabled = it }
        }

        viewModelScope.launch {
            prefs.showUserGridTimelineFlow.collect { res ->
                view = ViewEnum.getView(res)
            }
        }
    }

    fun clear() {
        searchState = SearchState()
        searchJob?.cancel()
        resultsJob?.cancel()
    }

    fun changeView(newView: ViewEnum) {
        view = newView
        prefs.showUserGridTimeline = newView.ordinal
    }

    fun saveAccount(accountUsername: String, account: Account) {
        viewModelScope.launch {
            val currentUserId = authService.getCurrentSession()?.accountId ?: return@launch
            savedSearchesService.addAccount(accountUsername, account, currentUserId)
        }
    }

    fun saveHashtag(accountId: String) {
        viewModelScope.launch {
            val currentUserId = authService.getCurrentSession()?.accountId ?: return@launch

            savedSearchesService.addHashtag(accountId, currentUserId)
        }
    }

    fun saveSearch(text: String) {
        if (text.isNotBlank()) {

            /*val savedSearchesBefore =
                savedSearches.pastSearches.filter { it.savedSearchType == SavedSearchType.Search }
            if (savedSearchesBefore.find { it.value == text } != null) {
                return
            }*/

            viewModelScope.launch {
                val currentUserId = authService.getCurrentSession()?.accountId ?: return@launch

                savedSearchesService.addSearch(text, currentUserId)
            }
        }
    }

    fun deleteSavedSearch(item: SavedSearchItem) {
        viewModelScope.launch {
            val currentUserId = authService.getCurrentSession()?.accountId ?: return@launch

            savedSearchesService.deleteElement(item, currentUserId)
        }
    }

    fun onSearch(text: String, isRefreshing: Boolean) {
        searchJob?.cancel()
        if (text.isNotBlank()) {
            getSearchResults(text, 20, true, isRefreshing = isRefreshing)
        }
    }

    fun textInputChange(text: String) {
        searchDebounced(text)
    }

    private var searchJob: Job? = null

    private fun searchDebounced(searchText: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500.milliseconds)
            if (searchText.isNotBlank()) {
                getSearchResults(searchText, 5, false)
            }
        }
    }

    private fun getSearchResults(text: String, limit: Int, includePosts: Boolean, isRefreshing: Boolean = false) {
        resultsJob?.cancel()
        resultsJob = exploreService.search(text, limit = limit, includePosts = includePosts).onEach { result ->
            searchState = when (result) {
                is Resource.Success -> {
                    SearchState(searchResult = result.data)
                }

                is Resource.Error -> {
                    SearchState(error = result.message)
                }

                is Resource.Loading -> {
                    SearchState(isLoading = true, isRefreshing = isRefreshing)
                }
            }
        }.launchIn(viewModelScope)
    }
}