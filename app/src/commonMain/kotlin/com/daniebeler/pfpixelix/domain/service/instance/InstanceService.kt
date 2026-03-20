package com.daniebeler.pfpixelix.domain.service.instance

import com.daniebeler.pfpixelix.domain.repository.PixelfedApi
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

    fun getSoftwareFromFediDB(slug: String) = loadResource {
        api.getSoftwareFromFediDB(slug)
    }

    fun getServerFromFediDB(slug: String) = loadResource {
        api.getServerFromFediDB(domain = slug).data
    }

    fun getOpenServers(search: String, limit: Int) = loadResource {
        api.getOpenServers(search, limit)
    }
}