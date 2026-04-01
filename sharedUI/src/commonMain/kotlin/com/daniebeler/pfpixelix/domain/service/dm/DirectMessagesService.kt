package com.daniebeler.pfpixelix.domain.service.dm

import com.daniebeler.pfpixelix.domain.model.NewMessage
import com.daniebeler.pfpixelix.domain.repository.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.utils.loadListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject

@Inject
class DirectMessagesService(
    private val api: PixelfedApi,
    private val json: Json
) {
    fun getConversations() = loadListResources {
        api.getConversations()
    }

    fun getChat(accountId: String, maxId: String? = null) = loadResource {
        api.getChat(accountId, maxId)
    }

    fun sendMessage(createMessageDto: NewMessage) = loadResource {
        api.sendMessage(json.encodeToString(createMessageDto))
    }

    fun deleteMessage(id: String) = loadResource {
        api.deleteMessage(id)
    }
}