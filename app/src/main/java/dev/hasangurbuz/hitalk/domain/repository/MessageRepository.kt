package dev.hasangurbuz.hitalk.domain.repository

import dev.hasangurbuz.hitalk.domain.model.Message
import dev.hasangurbuz.hitalk.domain.model.Resource
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    fun listen(conversationId: String): Flow<List<Message>>

    suspend fun findById(messageId: String): Resource<Message>

    suspend fun create(message: Message): Resource<Message>
}