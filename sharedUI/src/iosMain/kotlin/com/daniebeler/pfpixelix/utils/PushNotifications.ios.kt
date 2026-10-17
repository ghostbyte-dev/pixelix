package com.daniebeler.pfpixelix.utils

import androidx.compose.runtime.key
import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.domain.service.general.PushSubscriptionService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.vernissage.model.request.SubscribePushNotificationRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import platform.UIKit.UIApplication
import platform.UIKit.registerForRemoteNotifications
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration.Companion.milliseconds

actual fun initializePushNotifications(
    context: Any?,
    activeUser: String,
    distributorPreference: String,
    setDistributorPreference: (distributor: String) -> Unit
) {
}

object ApnsTokenHolder {
    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token
    fun onToken(hex: String) {
        _token.value = hex
    }
}

private suspend fun requestPushPermission(): Boolean = suspendCoroutine { cont ->
    UNUserNotificationCenter.currentNotificationCenter()
        .requestAuthorizationWithOptions(
            UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        ) { granted, _ -> cont.resume(granted) }
}

actual suspend fun registerPushAfterLogin(
    pushSubscriptionService: PushSubscriptionService,
    activeUser: String
) {
    val keys = WebPushKeyProviderHolder.provider.loadOrCreate(activeUser)
    if (keys == null) {
        Logger.d("pushNotifications") { "push notifications key generation failed" }
        return
    }
    if (!requestPushPermission()) {
        Logger.d("pushNotifications") { "push permission denied" }
        return
    }

    withContext(Dispatchers.Main) {
        UIApplication.sharedApplication.registerForRemoteNotifications()
    }

    val apnsToken = withTimeoutOrNull(10_000.milliseconds) {
        ApnsTokenHolder.token.first { it != null }
    }

    Logger.d("pushNotifications") {
        apnsToken.toString()
    }

    pushSubscriptionService.subscribe(
        SubscribePushNotificationRequest(
            endpoint = "https://push.pixelix.social/push/sandbox/$apnsToken",
            userAgentPublicKey = keys.publicKey,
            auth = keys.auth
        )
    ).collect { resource ->
        when (resource) {
            is Resource.Loading -> Logger.d(tag = "PixelixPush") { "Subscribe: loading" }
            is Resource.Success -> Logger.d(tag = "PixelixPush") { "Subscribe: success" }
            is Resource.Error -> Logger.e(tag = "PixelixPush") { "Subscribe: error - ${resource.message}" }
        }
    }
}