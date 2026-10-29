package com.daniebeler.pfpixelix.ui.events

import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.di.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@SingleIn(AppScope::class)
@Inject
class BackToTopTrigger {
    private val _event = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val event = _event.asSharedFlow()

    fun scrollToTop() {
        Logger.d("BackToTop") {
            "emitting"
        }
        _event.tryEmit(Unit)
        Logger.d("BackToTop") {
            "emitted"
        }
    }

}