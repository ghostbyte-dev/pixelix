package com.daniebeler.pfpixelix.ui.events

import com.daniebeler.pfpixelix.di.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@SingleIn(AppScope::class)
@Inject
class SearchFieldFocus {
    private val eventsFlow = MutableSharedFlow<Boolean>()
    val events = eventsFlow.asSharedFlow()

    fun focus() {
        GlobalScope.launch { eventsFlow.emit(true) }
    }
}