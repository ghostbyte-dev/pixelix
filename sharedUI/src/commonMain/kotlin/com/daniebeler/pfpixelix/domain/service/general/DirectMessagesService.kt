package com.daniebeler.pfpixelix.domain.service.general

import com.daniebeler.pfpixelix.di.AppScope
import com.daniebeler.pfpixelix.domain.model.Chat
import com.daniebeler.pfpixelix.domain.model.Conversation
import com.daniebeler.pfpixelix.domain.model.Message
import com.daniebeler.pfpixelix.domain.model.NewMessage
import com.daniebeler.pfpixelix.domain.repository.pixelfed.PixelfedApi
import com.daniebeler.pfpixelix.domain.service.pixelfed.PixelfedCollectionService
import com.daniebeler.pfpixelix.domain.service.pixelfed.PixelfedDirectMessagesService
import com.daniebeler.pfpixelix.domain.service.utils.Resource
import com.daniebeler.pfpixelix.domain.service.utils.loadListResources
import com.daniebeler.pfpixelix.domain.service.utils.loadResource
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json

interface DirectMessagesService {
    fun getConversations(): Flow<Resource<List<Conversation>>>

    fun getChat(accountId: String, maxId: String? = null): Flow<Resource<Chat>>

    fun sendMessage(createMessageDto: NewMessage): Flow<Resource<Message>>

    fun deleteMessage(id: String): Flow<Resource<List<Int>>>
}

@Inject
@SingleIn(AppScope::class)
class DirectMessagesServiceDelegate(
    private val session: Session,
    private val pixelfed: PixelfedDirectMessagesService,
    //private val vernissage: VernissageTimelineService
) : DirectMessagesService {

    private val current: DirectMessagesService
        get() = when (session.backendType) {
            // BackendType.VERNISSAGE -> vernissage
            else -> pixelfed
        }

    override fun getConversations(): Flow<Resource<List<Conversation>>> = current.getConversations()

    override fun getChat(
        accountId: String,
        maxId: String?
    ): Flow<Resource<Chat>> = current.getChat(accountId, maxId)

    override fun sendMessage(createMessageDto: NewMessage): Flow<Resource<Message>> = current.sendMessage(createMessageDto)

    override fun deleteMessage(id: String): Flow<Resource<List<Int>>> = current.deleteMessage(id)
}