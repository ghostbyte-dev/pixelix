package com.daniebeler.pfpixelix.domain.service.pushNotifications

import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.MyApplication.Companion.appComponent
import com.daniebeler.pfpixelix.domain.model.PushNotification
import com.daniebeler.pfpixelix.domain.service.general.PushSubscriptionService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissagePushPayloadDto
import com.daniebeler.pfpixelix.domain.service.vernissage.model.request.SubscribePushNotificationRequest
import com.daniebeler.pfpixelix.utils.Notifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.unifiedpush.android.connector.FailedReason
import org.unifiedpush.android.connector.PushService
import org.unifiedpush.android.connector.data.PushEndpoint
import org.unifiedpush.android.connector.data.PushMessage
import org.unifiedpush.android.connector.keys.DefaultKeyManager


class PixelixPushService : PushService() {
    private val context = this

    private val pushSubscriptionRepository: PushSubscriptionService
        get() = appComponent.pushSubscriptionService

    override fun onNewEndpoint(endpoint: PushEndpoint, instance: String) {
        Logger.d(tag="PixelixPush") { "new endpoint" + endpoint.url }
        val keyManager = DefaultKeyManager(context)
        val publicKeySet = keyManager.getPublicKeySet(instance) ?: return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Logger.d(tag = "PixelixPush") { "About to subscribe" }
                pushSubscriptionRepository.subscribe(
                    SubscribePushNotificationRequest(
                        endpoint = endpoint.url,
                        userAgentPublicKey = publicKeySet.pubKey,
                        auth = publicKeySet.auth
                    )
                ).collect { resource ->
                    when (resource) {
                        is Resource.Loading -> Logger.d(tag = "PixelixPush") { "Subscribe: loading" }
                        is Resource.Success -> Logger.d(tag = "PixelixPush") { "Subscribe: success" }
                        is Resource.Error -> Logger.e(tag = "PixelixPush") { "Subscribe: error - ${resource.message}" }
                    }
                }
            } catch (e: Exception) {
                Logger.e(tag = "PixelixPush", throwable = e) { "Subscribe threw" }
            }
        }
    }

    override fun onMessage(message: PushMessage, instance: String) {
        Logger.d(tag="PixelixPush") { "message" }
        Logger.d(tag="PixelixPush") { message.content.toString(Charsets.UTF_8) }
        val message = message.content.toString(Charsets.UTF_8)
        val notification = decodeMessage(message)
        if (notification != null) {
            Notifier(context).showNotification(notification)
        }
    }

    override fun onRegistrationFailed(
        reason: FailedReason,
        instance: String
    ) {
        Logger.d(tag="PixelixPush") {
            "registration failed: " + reason.name
        }
    }

    override fun onUnregistered(instance: String) {
    }

    private fun decodeMessage(message: String): PushNotification? {
        val payload = try {
            Json.decodeFromString<VernissagePushPayloadDto>(message)
        } catch (e: Exception) {
            Logger.e(tag = "PixelixPush", throwable = e) { "Failed to parse push payload" }
            return null
        }
        return payload.toDomain()
    }
}