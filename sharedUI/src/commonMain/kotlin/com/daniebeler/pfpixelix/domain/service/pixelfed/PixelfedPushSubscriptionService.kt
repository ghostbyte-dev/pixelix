package com.daniebeler.pfpixelix.domain.service.pixelfed

import com.daniebeler.pfpixelix.domain.model.PushNotification
import com.daniebeler.pfpixelix.domain.service.general.PushSubscriptionService
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import com.daniebeler.pfpixelix.domain.service.vernissage.model.request.SubscribePushNotificationRequest
import me.tatarka.inject.annotations.Inject

@Inject
class PixelfedPushSubscriptionService: PushSubscriptionService {
    override fun subscribe(
        subscriptionDto: SubscribePushNotificationRequest
    ) = loadResource {
    }

    override fun decodeMessage(message: String): PushNotification? {
        return PushNotification(
            title = "Pixelfed",
            body = "none",
            icon = "hola"
        )
    }

}