package com.daniebeler.pfpixelix.domain.service.instance

import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import me.tatarka.inject.annotations.Inject

@Inject
class InstanceService(
    private val api: PixelfedApi
) {

    fun getInstance() = loadResource {
        api.getInstance()
    }

    fun getNodeInfo(domain: String) = loadResource {
        api.getNodeInfo(domain)
    }

    fun getSoftwareFromFedisea(slug: String) = loadResource {
        api.getSoftwareFromFedisea(slug)
    }

    fun getServerFromFedisea(slug: String) = loadResource {
        api.getServerFromFedisea(domain = slug)
    }

    fun getOpenServers(search: String, limit: Int) = loadResource {
        api.getOpenServers(search, limit)
    }
}