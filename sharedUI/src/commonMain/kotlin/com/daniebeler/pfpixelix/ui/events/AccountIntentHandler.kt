package com.daniebeler.pfpixelix.ui.events

import com.daniebeler.pfpixelix.di.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Open account intent flow
 * Pair<AccountId, String>
 */
@Inject
@SingleIn(AppScope::class)
class AccountIntentHandler {
    private val _pendingAccount = MutableSharedFlow<Pair<String, String>>(extraBufferCapacity = 1)
    val pendingAccount = _pendingAccount.asSharedFlow()

    fun onAccountOpen(accountId: String, username: String) {
        _pendingAccount.tryEmit(Pair(accountId, username))
    }
}