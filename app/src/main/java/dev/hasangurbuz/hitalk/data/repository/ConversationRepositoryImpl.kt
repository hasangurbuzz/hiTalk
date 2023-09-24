package dev.hasangurbuz.hitalk.data.repository

import dev.hasangurbuz.hitalk.data.remote.ConversationApi
import dev.hasangurbuz.hitalk.data.remote.model.ConversationDto
import dev.hasangurbuz.hitalk.data.remote.model.Response
import dev.hasangurbuz.hitalk.domain.model.Conversation
import dev.hasangurbuz.hitalk.domain.model.Resource
import dev.hasangurbuz.hitalk.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationRepositoryImpl
@Inject constructor(private val conversationApi: ConversationApi) : ConversationRepository {

    override suspend fun fetch(userId: String) = conversationApi.listen(userId).map {
        it.map { conversationDto ->
            Conversation(
                id = conversationDto.id!!,
                lastMessageId = conversationDto.lastMessageId!!,
                participants = conversationDto.participants!!
            )

        }
    }

    override suspend fun findByParticipants(participantIdList: List<String>): Resource<Conversation> {
        val result = conversationApi.findByParticipants(participantIdList)

        if (result is Response.Failed) {
            return Resource.Failed
        }

        val conversation = (result as Response.Success).data.toConversation()

        return Resource.Success(conversation)
    }

    override suspend fun findById(conversationId: String): Resource<Conversation> {
        val result = conversationApi.findById(conversationId)

        return when (result) {
            is Response.Failed -> Resource.Failed
            is Response.Success -> Resource.Success(result.data.toConversation())
        }
    }

    override suspend fun update(conversation: Conversation): Resource<Conversation> {
        val dto = ConversationDto(
            id = conversation.id,
            lastMessageId = conversation.lastMessageId,
            participants = conversation.participants
        )

        val result = conversationApi.update(dto)

        return when (result) {
            is Response.Failed -> Resource.Failed
            is Response.Success -> Resource.Success(result.data.toConversation())
        }
    }

    override suspend fun create(conversation: Conversation): Resource<Conversation> {
        val dto = ConversationDto(
            id = conversation.id,
            lastMessageId = conversation.lastMessageId,
            participants = conversation.participants
        )

        val result = conversationApi.create(dto)

        return when (result) {
            is Response.Failed -> Resource.Failed
            is Response.Success -> Resource.Success(result.data.toConversation())
        }
    }

}