package com.daniebeler.pfpixelix.ui.composables.settings.about_instance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.domain.service.instance.InstanceService
import com.daniebeler.pfpixelix.domain.service.platform.Platform
import com.daniebeler.pfpixelix.domain.service.session.AuthService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class AboutInstanceViewModel @Inject constructor(
    private val instanceService: InstanceService,
    private val authService: AuthService,
    private val platform: Platform
) : ViewModel() {

    var instanceState by mutableStateOf(InstanceState())

    var ownInstanceDomain by mutableStateOf("")

    init {
        getInstance()
        ownInstanceDomain = authService.getCurrentSession()?.serverUrl.orEmpty()
    }

    private fun getInstance() {
        instanceService.getInstance().onEach { result ->
            instanceState = when (result) {
                is Resource.Success -> {
                    InstanceState(instance = result.data)
                }

                is Resource.Error -> {
                    InstanceState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    InstanceState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun openUrl(url: String) {
        platform.openUrl(url)
    }

}