package dev.hasangurbuz.hitalk.data.remote

import dev.hasangurbuz.hitalk.data.remote.model.ConversationDto
import dev.hasangurbuz.hitalk.data.remote.model.Response
import kotlinx.coroutines.flow.Flow

interface ConversationApi {

    suspend fun create(conversation: ConversationDto): Response<ConversationDto>

    suspend fun update(conversation: ConversationDto): Response<ConversationDto>

    suspend fun findByUserId(userId: String): Response<List<ConversationDto>>

    suspend fun findById(conversationId: String): Response<ConversationDto>

    fun listenLatest(userId: String) : Flow<List<ConversationDto>>


}