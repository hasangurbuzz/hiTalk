package dev.hasangurbuz.hitalk.domain.repository

import dev.hasangurbuz.hitalk.domain.model.Conversation
import dev.hasangurbuz.hitalk.domain.model.Resource
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {

    suspend fun fetch(userId: String): Flow<List<Conversation>>

    suspend fun findByParticipants(participantIdList: List<String>): Resource<Conversation>

    suspend fun findById(conversationId: String): Resource<Conversation>

    suspend fun update(conversation: Conversation): Resource<Conversation>

    suspend fun create(conversation: Conversation): Resource<Conversation>


}