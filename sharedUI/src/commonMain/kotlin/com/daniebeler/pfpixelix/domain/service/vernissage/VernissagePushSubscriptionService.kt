package com.daniebeler.pfpixelix.domain.service.vernissage

import co.touchlab.kermit.Logger
import com.daniebeler.pfpixelix.domain.model.PushNotification
import com.daniebeler.pfpixelix.domain.repository.vernissage.VernissageApi
import com.daniebeler.pfpixelix.domain.service.general.NotificationService
import com.daniebeler.pfpixelix.domain.service.general.PushSubscriptionService
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import com.daniebeler.pfpixelix.domain.service.vernissage.model.VernissagePushPayloadDto
import com.daniebeler.pfpixelix.domain.service.vernissage.model.request.SubscribePushNotificationRequest
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject

@Inject
class VernissagePushSubscriptionService(
    private val api: VernissageApi
) : PushSubscriptionService {
    override fun subscribe(
        subscriptionDto: SubscribePushNotificationRequest
    ) = loadResource {
        api.subscribePushNotifications(subscriptionDto)
    }
}