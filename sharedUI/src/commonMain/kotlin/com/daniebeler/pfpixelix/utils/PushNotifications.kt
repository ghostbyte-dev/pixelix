package com.daniebeler.pfpixelix.utils

import com.daniebeler.pfpixelix.domain.service.general.PushSubscriptionService


expect fun initializePushNotifications(context: Any?, activeUser: String, distributorPreference: String, setDistributorPreference: (distributor: String) -> Unit)
expect suspend fun registerPushAfterLogin(
    pushSubscriptionService: PushSubscriptionService,
    activeUser: String,
)