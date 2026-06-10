package com.daniebeler.pfpixelix.domain.service.session

import com.daniebeler.pfpixelix.di.AppSingleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.tatarka.inject.annotations.Inject

@Inject
@AppSingleton
class Session {
    private val credentialsState = MutableStateFlow<Credentials?>(null)
    val credentials: StateFlow<Credentials?> = credentialsState.asStateFlow()

    fun setCredentials(credentials: Credentials?) {
        credentialsState.value = credentials
    }
}