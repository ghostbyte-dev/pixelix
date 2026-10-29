package com.daniebeler.pfpixelix.ui.composables.settings.blocked_accounts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.di.AppScope
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.service.general.AccountService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@ViewModelKey
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
@Inject
class BlockedAccountsViewModel(
    private val accountService: AccountService
) : ViewModel() {

    var blockedAccountsState by mutableStateOf(BlockedAccountsState())

    var unblockAccountAlert: String by mutableStateOf("")

    init {
        getBlockedAccounts()
    }

    fun getBlockedAccounts(refreshing: Boolean = false) {
        accountService.getBlockedAccounts().onEach { result ->
            blockedAccountsState = when (result) {
                is Resource.Success -> {
                    BlockedAccountsState(blockedAccounts = result.data ?: emptyList())
                }

                is Resource.Error -> {
                    BlockedAccountsState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    BlockedAccountsState(
                        isLoading = true,
                        isRefreshing = refreshing,
                        blockedAccounts = blockedAccountsState.blockedAccounts
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun unblockAccount(accountId: String, username: String) {
        accountService.unblockAccount(accountId, username).onEach { result ->
            blockedAccountsState = when (result) {
                is Resource.Success -> {
                    val newBlockedAccounts =
                        blockedAccountsState.blockedAccounts.filter { account: Account -> account.id != accountId }
                    BlockedAccountsState(blockedAccounts = newBlockedAccounts)
                }

                is Resource.Error -> {
                    blockedAccountsState.copy(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    blockedAccountsState.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
}