package com.daniebeler.pfpixelix.domain.service.utils

import com.daniebeler.pfpixelix.di.AppSingleton
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import me.tatarka.inject.annotations.Inject

sealed interface GlobalNavigationEvent {
    data object NavigateToLogin : GlobalNavigationEvent
}

interface GlobalNavigator {
    val navigationEvents: SharedFlow<GlobalNavigationEvent>
    suspend fun emit(event: GlobalNavigationEvent)
}

@AppSingleton
@Inject
class GlobalNavigatorImpl : GlobalNavigator {
    private val _navigationEvents = MutableSharedFlow<GlobalNavigationEvent>(extraBufferCapacity = 1)
    override val navigationEvents: SharedFlow<GlobalNavigationEvent> = _navigationEvents.asSharedFlow()

    override suspend fun emit(event: GlobalNavigationEvent) {
        _navigationEvents.emit(event)
    }
}