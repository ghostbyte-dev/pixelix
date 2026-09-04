package com.daniebeler.pfpixelix.domain.service.vernissage

import com.daniebeler.pfpixelix.domain.repository.vernissage.VernissageApi
import com.daniebeler.pfpixelix.domain.service.general.PushSubscriptionService
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import com.daniebeler.pfpixelix.domain.service.vernissage.model.request.SubscribePushNotificationRequest
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