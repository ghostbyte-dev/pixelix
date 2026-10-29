package com.daniebeler.pfpixelix.domain.service.general

import com.daniebeler.pfpixelix.di.AppScope
import com.daniebeler.pfpixelix.domain.model.FediseaInstance
import com.daniebeler.pfpixelix.domain.model.FediseaServersResponse
import com.daniebeler.pfpixelix.domain.model.FediseaSoftware
import com.daniebeler.pfpixelix.domain.model.Instance
import com.daniebeler.pfpixelix.domain.model.NodeInfo
import com.daniebeler.pfpixelix.domain.service.pixelfed.PixelfedExploreService
import com.daniebeler.pfpixelix.domain.service.pixelfed.PixelfedInstanceService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.vernissage.VernissageInstanceService
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

interface InstanceService {

    fun getInstance(): Flow<Resource<Instance>>

    fun getNodeInfo(domain: String): Flow<Resource<NodeInfo>>
}

@Inject
@SingleIn(AppScope::class)
class InstanceServiceDelegate(
    private val session: Session,
    private val pixelfed: PixelfedInstanceService,
    private val vernissage: VernissageInstanceService
) : InstanceService {

    private val current: InstanceService
        get() = when (session.backendType.value) {
            BackendType.VERNISSAGE -> vernissage
            else -> pixelfed
        }

    override fun getInstance(): Flow<Resource<Instance>> = current.getInstance()

    override fun getNodeInfo(domain: String): Flow<Resource<NodeInfo>> = current.getNodeInfo(domain)
}