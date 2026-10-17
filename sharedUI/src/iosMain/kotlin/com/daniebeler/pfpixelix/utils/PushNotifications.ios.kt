package com.daniebeler.pfpixelix.utils

import com.daniebeler.pfpixelix.domain.service.general.PushSubscriptionService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

actual fun initializePushNotifications(context: Any?, activeUser: String, distributorPreference: String, setDistributorPreference: (distributor: String) -> Unit) {
}

object ApnsTokenHolder {
    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token
    fun onToken(hex: String) { _token.value = hex }
}

actual suspend fun registerPushAfterLogin(
    pushSubscriptionService: PushSubscriptionService,
    activeUser: String
) {

}