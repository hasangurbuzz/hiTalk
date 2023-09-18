package dev.hasangurbuz.hitalk.data.repository

import dev.hasangurbuz.hitalk.data.model.Message
import dev.hasangurbuz.hitalk.data.model.Resource
import dev.hasangurbuz.hitalk.data.remote.MessageApi
import dev.hasangurbuz.hitalk.data.remote.model.Response
import javax.inject.Inject

class MessageRepository
@Inject constructor(private val messageApi: MessageApi){

    suspend fun findById(messageId: String): Resource<Message> {
        val result = messageApi.findById(messageId)
        if (result is Response.Failed) {
            return Resource.Failed
        }

        val messageDto = (result as Response.Success).data

        val message = Message(
            id = messageDto.id,
            content = messageDto.content,
            conversationId = messageDto.conversationId,
            senderId = messageDto.senderId,
            timestamp = messageDto.timestamp
        )

        return Resource.Success(message)
    }
}