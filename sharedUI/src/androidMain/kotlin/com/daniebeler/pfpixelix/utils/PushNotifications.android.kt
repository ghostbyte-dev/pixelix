package com.daniebeler.pfpixelix.utils

import android.content.Context
import co.touchlab.kermit.Logger
import org.unifiedpush.android.connector.UnifiedPush
import org.unifiedpush.android.connector.keys.DefaultKeyManager

actual fun initializePushNotifications(
    context: Any?,
    activeUser: String,
    distributorPreference: String,
    setDistributorPreference: (distributor: String) -> Unit
) {
    val ctx = context as Context
    val keyManager = DefaultKeyManager(ctx)

    val distributors = UnifiedPush.getDistributors(ctx)
    val hasPreferenceDistributor = distributors.firstOrNull { it == distributorPreference }
    Logger.d("pushNotification") {
        "user: $activeUser"
    }
    Logger.d("pushNotification") {
        "distributors found: " + distributors.size
    }
    Logger.d("pushNotification") {
        "distributors: $distributors"
    }
    if (hasPreferenceDistributor != null) {
        Logger.d("pushNotification") {
            "using preference distributor: $distributorPreference"
        }
        UnifiedPush.saveDistributor(ctx, distributorPreference)
    } else {

        Logger.d("pushNotification") {
            "not using preference distributor: $distributorPreference"
        }

        if (distributors.isEmpty()) {
            return
        } else if (distributors.size == 1) {
            UnifiedPush.saveDistributor(ctx, distributors.first())
            setDistributorPreference(distributors.first())
        } else {
            UnifiedPush.saveDistributor(ctx, distributors[1])
            setDistributorPreference(distributors[1])
        }
    }

    UnifiedPush.register(ctx, activeUser, keyManager = keyManager)

}