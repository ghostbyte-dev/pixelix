package com.daniebeler.pfpixelix.domain.service.timeline

import com.daniebeler.pfpixelix.di.AppSingleton
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@AppSingleton
@Inject
class BackToTopTrigger {
    private val _event = MutableSharedFlow<Unit>()
    val event = _event.asSharedFlow()

    fun scrollToTop() {
        GlobalScope.launch { _event.emit(Unit) }    }
}