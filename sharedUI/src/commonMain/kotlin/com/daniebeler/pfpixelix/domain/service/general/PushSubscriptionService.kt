package com.daniebeler.pfpixelix.domain.service.general

import com.daniebeler.pfpixelix.di.AppSingleton
import com.daniebeler.pfpixelix.domain.model.PushNotification
import com.daniebeler.pfpixelix.domain.service.pixelfed.PixelfedPushSubscriptionService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.vernissage.VernissagePushSubscriptionService
import com.daniebeler.pfpixelix.domain.service.vernissage.model.request.SubscribePushNotificationRequest
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

interface PushSubscriptionService {
    fun subscribe(subscriptionDto: SubscribePushNotificationRequest): Flow<Resource<Unit>>
    fun decodeMessage(message: String): PushNotification?
}

@Inject
@AppSingleton
class PushSubscriptionServiceDelegate(
    private val session: Session,
    private val pixelfed: PixelfedPushSubscriptionService,
    private val vernissage: VernissagePushSubscriptionService
) : PushSubscriptionService {

    private val current: PushSubscriptionService
        get() = when (session.backendType.value) {
            BackendType.VERNISSAGE -> vernissage
            else -> pixelfed
        }

    override fun subscribe(subscriptionDto: SubscribePushNotificationRequest) = current.subscribe(subscriptionDto)
    override fun decodeMessage(message: String): PushNotification? = current.decodeMessage(message)
}
