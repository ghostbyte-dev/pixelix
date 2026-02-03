package com.daniebeler.pfpixelix.domain.service.share

import com.daniebeler.pfpixelix.di.AppSingleton
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import me.tatarka.inject.annotations.Inject

@Inject
@AppSingleton
class AccountIntentHandler {
    private val _pendingAccountId = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val pendingAccountId = _pendingAccountId.asSharedFlow()

    fun onAccountOpen(accountId: String) {
        _pendingAccountId.tryEmit(accountId);
    }
}