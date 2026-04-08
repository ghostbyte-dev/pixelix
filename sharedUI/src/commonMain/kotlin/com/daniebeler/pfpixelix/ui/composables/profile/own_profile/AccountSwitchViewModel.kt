package com.daniebeler.pfpixelix.ui.composables.profile.own_profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.session.AuthService
import com.daniebeler.pfpixelix.domain.service.session.Credentials
import com.daniebeler.pfpixelix.domain.service.session.SessionStorage
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class AccountSwitchViewModel @Inject constructor(
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

    fun switchAccount(newAccount: Credentials, changedAccount: () -> Unit) {
        val coroutine = viewModelScope.launch {
            authService.openSessionIfExist(userId = newAccount.accountId)
        }

        coroutine.invokeOnCompletion {
            changedAccount()
            loadAccounts()
        }
    }

    fun removeAccount(accountId: String) {
        val coroutine = viewModelScope.launch {
            authService.deleteSession(accountId)
        }
        coroutine.invokeOnCompletion {
            loadAccounts()
        }
    }
}
