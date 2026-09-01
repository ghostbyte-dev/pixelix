package com.daniebeler.pfpixelix.utils

import android.content.Context
import co.touchlab.kermit.Logger
import org.unifiedpush.android.connector.UnifiedPush
import org.unifiedpush.android.connector.keys.DefaultKeyManager

actual fun initializePushNotifications(context: Any?, activeUser: String) {
    val ctx = context as Context
    val keyManager = DefaultKeyManager(ctx)

    val distributors = UnifiedPush.getDistributors(ctx)
    if (distributors.isNotEmpty()) {
        Logger.d("pushNotification") {
            "distributors found: " + distributors.size
        }
        UnifiedPush.saveDistributor(ctx, distributors.first())
        UnifiedPush.register(ctx, activeUser, keyManager = keyManager)
    } else {
        Logger.d("pushNotification") { "no distributor found" }
    }
}