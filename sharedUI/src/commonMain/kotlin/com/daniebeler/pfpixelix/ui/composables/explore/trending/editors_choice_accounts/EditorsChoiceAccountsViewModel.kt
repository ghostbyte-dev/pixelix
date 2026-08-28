package com.daniebeler.pfpixelix.ui.composables.explore.trending.editors_choice_accounts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.service.capabilities.Capabilities
import com.daniebeler.pfpixelix.domain.service.general.ExploreService
import com.daniebeler.pfpixelix.domain.service.general.Session
import com.daniebeler.pfpixelix.domain.service.preferences.UserPreferences
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

data class AccountsState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val accounts: List<Account> = emptyList(),
    val error: String = "",
    val nextId: String? = null,
    val endReached: Boolean = false
)

class EditorsChoiceAccountsViewModel @Inject constructor(
    private val exploreService: ExploreService,
    session: Session,
    private val userPreferences: UserPreferences
) : ViewModel() {
    val capabilities: StateFlow<Capabilities> = session.capabilities
    var accountsState by mutableStateOf(AccountsState())
    var showHelp by mutableStateOf(false)

    init {
        getAccountsState()
        viewModelScope.launch {
            userPreferences.showEditorsChoiceAccountsHelpFlow.collect {
                showHelp = it
            }
        }
    }

    fun getAccountsState(refreshing: Boolean = false) {
        if (!refreshing && accountsState.accounts.isNotEmpty()) return
        fetchAccounts(nextId = null, isRefreshing = refreshing)
    }

    fun getAccountsPaginated() {
        if (accountsState.isLoading || accountsState.endReached || accountsState.nextId == null || accountsState.accounts.isEmpty()) {
            return
        }

        fetchAccounts(nextId = accountsState.nextId, isRefreshing = false)
    }

    private fun fetchAccounts(nextId: String?, isRefreshing: Boolean) {
        exploreService.getEditorsChoiceAccounts(nextId).onEach { result ->
            accountsState = when (result) {
                is Resource.Success -> {
                    val newAccounts = result.data.data
                    val updatedAccounts =
                        if (nextId == null) newAccounts else accountsState.accounts + newAccounts

                    accountsState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        accounts = updatedAccounts,
                        nextId = result.data.next,
                        endReached = newAccounts.isEmpty() || result.data.next == null,
                        error = ""
                    )
                }

                is Resource.Error -> {
                    accountsState.copy(
                        isLoading = false, isRefreshing = false, error = result.message
                    )
                }

                is Resource.Loading -> {
                    accountsState.copy(
                        isLoading = true, isRefreshing = isRefreshing
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun discardHelp() {
        viewModelScope.launch {
            userPreferences.showEditorsChoiceAccountsHelp = false
        }
    }
}