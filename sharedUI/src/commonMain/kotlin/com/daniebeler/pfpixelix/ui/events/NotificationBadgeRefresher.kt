package com.daniebeler.pfpixelix.ui.events

import com.daniebeler.pfpixelix.di.AppSingleton
import com.daniebeler.pfpixelix.domain.service.general.NotificationService
import com.daniebeler.pfpixelix.domain.service.general.Session
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
@AppSingleton
class NotificationBadgeRefresher(
    private val session: Session,
    private val notificationService: NotificationService,
    private val badgeState: NotificationBadgeState
) {
    private var started = false
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun start() {
        if (started) return
        started = true
        scope.launch {
            combine(session.credentials, session.backendType) { creds, _ -> creds != null }
                .distinctUntilChanged()
                .collectLatest { loggedIn -> if (loggedIn) refresh() else badgeState.clear() }
        }
    }

    private suspend fun refresh() {
        notificationService.getUnreadCount().collect { resource ->
            if (resource is Resource.Success) badgeState.update(resource.data)
        }
    }
}