package com.daniebeler.pfpixelix.ui.events

import androidx.compose.ui.platform.UriHandler
import com.daniebeler.pfpixelix.di.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

@Inject
@SingleIn(AppScope::class)
class SystemUrlHandler {
    private val redirectsFlow = MutableSharedFlow<String>()
    val redirects = redirectsFlow.asSharedFlow()

    var uriHandler: UriHandler? = null

    var isAuthInProgress: Boolean = false

    fun openBrowser(url: String) {
        uriHandler?.openUri(url)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun onRedirect(url: String) {
        isAuthInProgress = false
        GlobalScope.launch { redirectsFlow.emit(url) }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun cancelWaiting() {
        if (isAuthInProgress) {
            isAuthInProgress = false
            GlobalScope.launch { redirectsFlow.emit("CANCELLED") }
        }
    }
}