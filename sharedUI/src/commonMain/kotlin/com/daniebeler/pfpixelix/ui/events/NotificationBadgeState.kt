package com.daniebeler.pfpixelix.ui.events

import com.daniebeler.pfpixelix.di.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Inject
@SingleIn(AppScope::class)
class NotificationBadgeState {
    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()

    fun update(newCount: Int) { _count.value = newCount }
    fun clear() { _count.value = 0 }
}