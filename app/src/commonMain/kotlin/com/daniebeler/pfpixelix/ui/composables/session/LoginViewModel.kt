package com.daniebeler.pfpixelix.ui.composables.session

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.model.Server
import com.daniebeler.pfpixelix.domain.service.instance.InstanceService
import com.daniebeler.pfpixelix.domain.service.platform.Platform
import com.daniebeler.pfpixelix.domain.service.session.AuthService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class LoginViewModel(
    private val authService: AuthService,
    private val instanceService: InstanceService,
    private val platform: Platform
) : ViewModel() {

    var serverHost by mutableStateOf(TextFieldValue())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isValidHost by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var openServers by mutableStateOf<List<Server>>(emptyList())
        private set

    init {
       getOpenServers()
    }

    private fun getOpenServers() {
        instanceService.getOpenServers().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    openServers = result.data
                }

                is Resource.Error -> {
                }

                is Resource.Loading -> {
                }
            }
        }.launchIn(viewModelScope)
    }

    fun updateServerHost(host: TextFieldValue) {
        serverHost = host
        isValidHost = authService.isValidHost(serverHost.text)
    }

    fun auth() {
        viewModelScope.launch {
            try {
                isLoading = true
                error = null
                authService.auth(serverHost.text)
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun showAvailableServers() {
        platform.openUrl("https://pixelfed.org/servers")
    }
}