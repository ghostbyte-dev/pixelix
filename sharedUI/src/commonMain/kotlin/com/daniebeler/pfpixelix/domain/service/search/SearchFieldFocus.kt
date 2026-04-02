package com.daniebeler.pfpixelix.domain.service.search

import com.daniebeler.pfpixelix.di.AppSingleton
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@AppSingleton
@Inject
class SearchFieldFocus {
    private val eventsFlow = MutableSharedFlow<Boolean>()
    val events = eventsFlow.asSharedFlow()

    fun focus() {
        GlobalScope.launch { eventsFlow.emit(true) }
    }
}