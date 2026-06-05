package com.daniebeler.pfpixelix.domain.service.general

import com.daniebeler.pfpixelix.di.AppSingleton
import com.daniebeler.pfpixelix.domain.model.FediseaInstance
import com.daniebeler.pfpixelix.domain.model.FediseaServersResponse
import com.daniebeler.pfpixelix.domain.model.FediseaSoftware
import com.daniebeler.pfpixelix.domain.model.Instance
import com.daniebeler.pfpixelix.domain.model.NodeInfo
import com.daniebeler.pfpixelix.domain.service.pixelfed.PixelfedExploreService
import com.daniebeler.pfpixelix.domain.service.pixelfed.PixelfedInstanceService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

interface InstanceService {

    fun getInstance(): Flow<Resource<Instance>>

    fun getNodeInfo(domain: String): Flow<Resource<NodeInfo>>

    fun getSoftwareFromFedisea(slug: String): Flow<Resource<FediseaSoftware>>

    fun getServerFromFedisea(slug: String): Flow<Resource<FediseaInstance>>

    fun getOpenServers(search: String, limit: Int): Flow<Resource<FediseaServersResponse>>
}

@Inject
@AppSingleton
class InstanceServiceDelegate(
    private val session: Session,
    private val pixelfed: PixelfedInstanceService,
    //private val vernissage: VernissageTimelineService
) : InstanceService {

    private val current: InstanceService
        get() = when (session.backendType) {
            // BackendType.VERNISSAGE -> vernissage
            else -> pixelfed
        }

    override fun getInstance(): Flow<Resource<Instance>> = current.getInstance()

    override fun getNodeInfo(domain: String): Flow<Resource<NodeInfo>> = current.getNodeInfo(domain)

    override fun getSoftwareFromFedisea(slug: String): Flow<Resource<FediseaSoftware>> =
        current.getSoftwareFromFedisea(slug)

    override fun getServerFromFedisea(slug: String): Flow<Resource<FediseaInstance>> =
        current.getServerFromFedisea(slug)

    override fun getOpenServers(
        search: String, limit: Int
    ): Flow<Resource<FediseaServersResponse>> = current.getOpenServers(search, limit)

}