package com.daniebeler.pfpixelix.utils

import com.daniebeler.pfpixelix.domain.service.general.PushSubscriptionService

actual fun initializePushNotifications(context: Any?, activeUser: String, distributorPreference: String, setDistributorPreference: (distributor: String) -> Unit) {
}

actual suspend fun registerPushAfterLogin(
    pushSubscriptionService: PushSubscriptionService,
    activeUser: String
) {
}