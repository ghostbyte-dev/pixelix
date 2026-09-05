package com.daniebeler.pfpixelix.ui.composables.session

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.general.AuthService
import com.daniebeler.pfpixelix.domain.service.general.BackendType
import com.daniebeler.pfpixelix.domain.service.general.Session
import com.daniebeler.pfpixelix.domain.service.platform.Platform
import com.daniebeler.pfpixelix.domain.service.suggestions.ServersSuggestionsManager
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

enum class LoginStep {
    PLATFORM_SELECTION, SERVER_INPUT
}

@Inject
class LoginViewModel(
    private val authService: AuthService,
    val serversSuggestionsManager: ServersSuggestionsManager,
    private val platform: Platform,
    private val session: Session,
) : ViewModel() {

    var currentStep by mutableStateOf(LoginStep.PLATFORM_SELECTION)
        private set

    var selectedPlatform by mutableStateOf<BackendType?>(null)
        private set

    var originalPlatform: BackendType = session.backendType.value

    var serverHost by mutableStateOf(TextFieldValue())
        private set

    var isLoading by mutableStateOf(false)

    var isValidHost by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun onClose() {
        session.setBackendType(originalPlatform)
    }

    fun selectPlatform(type: BackendType) {
        selectedPlatform = type
        currentStep = LoginStep.SERVER_INPUT
    }

    fun goBackToPlatformSelection() {
        currentStep = LoginStep.PLATFORM_SELECTION

        serverHost = TextFieldValue()
        isValidHost = false

        error = null
    }

    fun updateServerHost(host: TextFieldValue) {
        serverHost = host
        isValidHost = authService.isValidHost(serverHost.text)
        serversSuggestionsManager.changeText(host, selectedPlatform, viewModelScope)
    }

    fun selectSuggestion(newHost: TextFieldValue) {
        serverHost = newHost
        isValidHost = true
    }

    fun auth() {
        // This must happen synchronously in the click handler for mobile popup policies.
        val backendType = selectedPlatform ?: BackendType.PIXELFED
        if (!platform.prepareAuthBrowser(serverHost.text, backendType)) {
            error = "The browser blocked the login window. Allow pop-ups and try again."
            return
        }
        session.setBackendType(backendType)
        viewModelScope.launch {
            try {
                isLoading = true
                error = null
                authService.auth(serverHost.text)
            } catch (e: Throwable) {
                platform.dismissBrowser()
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun showAvailableServers() {
        val url = when (selectedPlatform) {
            BackendType.PIXELFED -> "https://pixelfed.org/servers"
            BackendType.VERNISSAGE -> "https://joinvernissage.org/servers"
            null -> "https://pixelfed.org/servers"
        }
        platform.openUrl(url)
    }
}