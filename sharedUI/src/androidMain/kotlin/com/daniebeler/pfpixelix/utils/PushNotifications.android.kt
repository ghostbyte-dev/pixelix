package com.daniebeler.pfpixelix.utils

import android.content.Context
import co.touchlab.kermit.Logger
import org.unifiedpush.android.connector.UnifiedPush
import org.unifiedpush.android.connector.keys.DefaultKeyManager

actual fun initializePushNotifications(context: Any?, activeUser: String) {
    val ctx = context as Context
    val keyManager = DefaultKeyManager(ctx)

    val distributors = UnifiedPush.getDistributors(ctx)
    if (distributors.isEmpty()) {
        return
    } else if (distributors.size == 1) {
        Logger.d("pushNotification") {
            "distributors found: 1, fcm"
        }

        UnifiedPush.saveDistributor(ctx, distributors.first())
        UnifiedPush.register(ctx, activeUser, keyManager = keyManager)
    } else {
        Logger.d("pushNotification") {
            "distributors found: " + distributors.size
        }
        Logger.d("pushNotification") {
            "distributors: $distributors"
        }
        UnifiedPush.saveDistributor(ctx, distributors[1])
        UnifiedPush.register(ctx, activeUser, keyManager = keyManager)
    }
}