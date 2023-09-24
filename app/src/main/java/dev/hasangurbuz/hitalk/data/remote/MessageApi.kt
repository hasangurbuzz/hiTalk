package dev.hasangurbuz.hitalk.data.remote

import dev.hasangurbuz.hitalk.data.remote.model.MessageDto
import dev.hasangurbuz.hitalk.data.remote.model.Response
import kotlinx.coroutines.flow.Flow

interface MessageApi {

    suspend fun create(messageDto: MessageDto): Response<MessageDto>

    suspend fun findById(messageId: String): Response<MessageDto>

    suspend fun findByConversation(conversationId: String): Response<List<MessageDto>>

    fun listen(conversationId: String): Flow<List<MessageDto>>
}