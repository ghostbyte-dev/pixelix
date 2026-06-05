package com.daniebeler.pfpixelix.domain.service.pixelfed

import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.general.InstanceService
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import me.tatarka.inject.annotations.Inject

@Inject
class PixelfedInstanceService(
    private val api: PixelfedApi
): InstanceService {

    override fun getInstance() = loadResource {
        api.getInstance()
    }

    override fun getNodeInfo(domain: String) = loadResource {
        api.getNodeInfo(domain)
    }

    override fun getSoftwareFromFedisea(slug: String) = loadResource {
        api.getSoftwareFromFedisea(slug)
    }

    override fun getServerFromFedisea(slug: String) = loadResource {
        api.getServerFromFedisea(domain = slug)
    }

    override fun getOpenServers(search: String, limit: Int) = loadResource {
        api.getOpenServers(search, limit)
    }
}