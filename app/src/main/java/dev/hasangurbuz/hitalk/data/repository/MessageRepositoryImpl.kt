package dev.hasangurbuz.hitalk.data.repository

import dev.hasangurbuz.hitalk.data.remote.MessageApi
import dev.hasangurbuz.hitalk.data.remote.model.MessageDto
import dev.hasangurbuz.hitalk.data.remote.model.Response
import dev.hasangurbuz.hitalk.domain.model.Message
import dev.hasangurbuz.hitalk.domain.model.Resource
import dev.hasangurbuz.hitalk.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MessageRepositoryImpl
@Inject constructor(private val messageApi: MessageApi) : MessageRepository {

    override fun listen(conversationId: String): Flow<List<Message>> =
        messageApi.listen(conversationId)
            .map { list ->
                list.map {
                    Message(
                        id = it.id!!,
                        conversationId = it.conversationId!!,
                        content = it.content!!,
                        timestamp = it.timestamp!!,
                        senderId = it.senderId!!
                    )
                }
            }

    override suspend fun findById(messageId: String): Resource<Message> {
        val result = messageApi.findById(messageId)
        if (result is Response.Failed) {
            return Resource.Failed
        }

        val messageDto = (result as Response.Success).data

        val message = Message(
            id = messageDto.id!!,
            content = messageDto.content!!,
            conversationId = messageDto.conversationId!!,
            senderId = messageDto.senderId!!,
            timestamp = messageDto.timestamp!!
        )

        return Resource.Success(message)
    }

    override suspend fun create(message: Message): Resource<Message> {
        val dto = MessageDto(
            id = message.id,
            content = message.content,
            conversationId = message.conversationId,
            senderId = message.senderId,
            timestamp = message.timestamp
        )

        val result = messageApi.create(dto)

        return when (result) {
            is Response.Failed -> Resource.Failed
            is Response.Success -> Resource.Success(result.data.toMessage())
        }
    }
}