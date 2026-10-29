package com.daniebeler.pfpixelix.ui.composables.settings.muted_accounts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.di.AppScope
import com.daniebeler.pfpixelix.domain.model.request.UserMuteRequest
import com.daniebeler.pfpixelix.domain.service.general.AccountService
import com.daniebeler.pfpixelix.domain.service.general.Session
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
class MutedAccountsViewModel(
    private val accountService: AccountService, session: Session
) : ViewModel() {
    val capabilities = session.capabilities

    var mutedAccountsState by mutableStateOf(MutedAccountsState())

    var unmuteAccountAlert: String by mutableStateOf("")

    init {
        getMutedAccounts()
    }

    fun getMutedAccounts(refreshing: Boolean = false) {
        accountService.getMutedAccounts().onEach { result ->
            mutedAccountsState = when (result) {
                is Resource.Success -> {
                    MutedAccountsState(mutedAccounts = result.data)
                }

                is Resource.Error -> {
                    MutedAccountsState(error = result.message)
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
                        error = result.message
                    )
                }

                is Resource.Loading -> {
                    mutedAccountsState.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
}