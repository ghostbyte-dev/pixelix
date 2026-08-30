package com.daniebeler.pfpixelix.utils

import android.content.Context
import co.touchlab.kermit.Logger
import org.unifiedpush.android.connector.UnifiedPush
import org.unifiedpush.android.connector.keys.DefaultKeyManager

actual fun initializePushNotifications(context: Any?) {
    val ctx = context as Context
    val keyManager = DefaultKeyManager(ctx)
    val instance = "default"

    val distributors = UnifiedPush.getDistributors(ctx)
    if (distributors.isNotEmpty()) {
        Logger.d("pushNotification") {
            "distributors found: " + distributors.size
        }
        UnifiedPush.saveDistributor(ctx, distributors.first())
        UnifiedPush.register(ctx, instance, keyManager = keyManager)
        // key generation happens internally as part of this call —
        // don't call keyManager.generate() yourself beforehand
    } else {
        // no distributor — embedded FCM fallback or prompt to install one
        Logger.d("pushNotification") { "no distributor found" }
    }
}