package com.daniebeler.pfpixelix.domain.service.pixelfed

import com.daniebeler.pfpixelix.domain.model.NewMessage
import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.general.DirectMessagesService
import com.daniebeler.pfpixelix.domain.service.utils.loadListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject

@Inject
class PixelfedDirectMessagesService(
    private val api: PixelfedApi,
    private val json: Json
): DirectMessagesService {
    override fun getConversations() = loadListResources {
        api.getConversations()
    }

    override fun getChat(accountId: String, maxId: String?) = loadResource {
        api.getChat(accountId, maxId)
    }

    override fun sendMessage(createMessageDto: NewMessage) = loadResource {
        api.sendMessage(json.encodeToString(createMessageDto))
    }

    override fun deleteMessage(id: String) = loadResource {
        api.deleteMessage(id)
    }
}