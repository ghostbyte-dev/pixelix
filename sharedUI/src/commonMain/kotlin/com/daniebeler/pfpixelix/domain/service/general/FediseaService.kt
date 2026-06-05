package com.daniebeler.pfpixelix.domain.service.general

import com.daniebeler.pfpixelix.domain.model.FediseaInstance
import com.daniebeler.pfpixelix.domain.model.FediseaServersResponse
import com.daniebeler.pfpixelix.domain.model.FediseaSoftware
import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class FediseaService(
    private val api: PixelfedApi
) {

    fun getSoftwareFromFedisea(slug: String): Flow<Resource<FediseaSoftware>> = loadResource {
        api.getSoftwareFromFedisea(slug)
    }

    fun getServerFromFedisea(slug: String): Flow<Resource<FediseaInstance>> = loadResource {
        api.getServerFromFedisea(domain = slug)
    }

    fun getOpenServers(
        search: String, backendType: BackendType, limit: Int
    ): Flow<Resource<FediseaServersResponse>> = loadResource {
        val softwareName = if (backendType == BackendType.PIXELFED) "pixelfed" else "Vernissage"
        api.getOpenServers(search, software = softwareName, size = limit)
    }
}