package com.daniebeler.pfpixelix.ui.events

import com.daniebeler.pfpixelix.di.AppSingleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.tatarka.inject.annotations.Inject

@Inject
@AppSingleton
class NotificationBadgeState {
    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count.asStateFlow()

    fun update(newCount: Int) { _count.value = newCount }
    fun clear() { _count.value = 0 }
}