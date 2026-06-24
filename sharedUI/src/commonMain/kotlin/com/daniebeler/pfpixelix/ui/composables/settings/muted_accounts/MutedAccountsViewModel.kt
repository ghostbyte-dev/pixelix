package com.daniebeler.pfpixelix.ui.composables.settings.muted_accounts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.model.MutedAccount
import com.daniebeler.pfpixelix.domain.model.request.UserMuteRequest
import com.daniebeler.pfpixelix.domain.service.general.AccountService
import com.daniebeler.pfpixelix.domain.service.general.Session
import com.daniebeler.pfpixelix.ui.composables.profile.RelationshipState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class MutedAccountsViewModel @Inject constructor(
    private val accountService: AccountService, session: Session
) : ViewModel() {
    val capabilities = session.capabilities.value

    var mutedAccountsState by mutableStateOf(MutedAccountsState())

    var unmuteAccountAlert: String by mutableStateOf("")

    init {
        getMutedAccounts()
    }

    fun getMutedAccounts(refreshing: Boolean = false) {
        accountService.getMutedAccounts().onEach { result ->
            mutedAccountsState = when (result) {
                is Resource.Success -> {
                    MutedAccountsState(mutedAccounts = result.data ?: emptyList())
                }

                is Resource.Error -> {
                    MutedAccountsState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    MutedAccountsState(
                        isLoading = true,
                        isRefreshing = refreshing,
                        mutedAccounts = mutedAccountsState.mutedAccounts
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun muteAccount(userId: String, username: String, userMuteRequest: UserMuteRequest) {
        val isMuting =
            userMuteRequest.mute == true || userMuteRequest.muteStatuses == true || userMuteRequest.muteReblogs == true || userMuteRequest.muteNotifications == true || userMuteRequest.removeStatusesFromTimeline == true || userMuteRequest.removeReblogsFromTimeline == true

        accountService.muteAccount(userId, username, userMuteRequest).onEach { result ->
            mutedAccountsState = when (result) {
                is Resource.Success -> {
                    if (isMuting) {
                        mutedAccountsState
                    } else {
                        val newMutedAccounts =
                            mutedAccountsState.mutedAccounts.filter { it.account.id != userId }
                        MutedAccountsState(mutedAccounts = newMutedAccounts)
                    }
                }

                is Resource.Error -> {
                    mutedAccountsState.copy(
                        error = result.message ?: "An unexpected error occurred"
                    )
                }

                is Resource.Loading -> {
                    mutedAccountsState.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
}