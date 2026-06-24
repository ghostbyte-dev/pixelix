package com.daniebeler.pfpixelix.domain.service.vernissage

import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.repository.vernissage.VernissageApi
import com.daniebeler.pfpixelix.domain.service.general.InstanceService
import com.daniebeler.pfpixelix.domain.service.pixelfed.model.toDomain
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import me.tatarka.inject.annotations.Inject

@Inject
class VernissageInstanceService(
    private val api: VernissageApi
): InstanceService {

    override fun getInstance() = loadResource {
        api.getInstance().toDomain()
    }

    override fun getNodeInfo(domain: String) = loadResource {
        api.getNodeInfo(domain).toDomain()
    }
}