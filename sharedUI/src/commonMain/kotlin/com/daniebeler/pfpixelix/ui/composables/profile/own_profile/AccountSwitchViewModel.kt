package com.daniebeler.pfpixelix.ui.composables.profile.own_profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.di.AppScope
import com.daniebeler.pfpixelix.domain.model.Credentials
import com.daniebeler.pfpixelix.domain.model.SessionStorage
import com.daniebeler.pfpixelix.domain.service.general.AuthService
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@ViewModelKey
@ContributesIntoMap(AppScope::class, binding<ViewModel>())
@Inject
class AccountSwitchViewModel(
    private val authService: AuthService
) : ViewModel() {
    var sessionStorage by mutableStateOf<SessionStorage?>(null)
    var currentCredentials by mutableStateOf<Credentials?>(null)

    fun loadData() {
        viewModelScope.launch {
            loadCurrentCredentials()
        }
        loadAccounts()
    }

    private suspend fun loadCurrentCredentials() {
        authService.activeUser
            .map { authService.getCurrentSession() }
            .collect {
                currentCredentials = it
            }
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            sessionStorage = authService.getAvailableSessions()
        }
    }

    fun switchAccount(newAccount: String, changedAccount: () -> Unit) {
        val coroutine = viewModelScope.launch {
            authService.openSessionIfExist(key = newAccount)
        }

        coroutine.invokeOnCompletion {
            changedAccount()
            loadAccounts()
        }
    }

    fun removeAccount(key: String) {
        val coroutine = viewModelScope.launch {
            authService.deleteSession(key)
        }
        coroutine.invokeOnCompletion {
            loadAccounts()
        }
    }
}
