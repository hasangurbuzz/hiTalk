package dev.hasangurbuz.hitalk.data.local

import dev.hasangurbuz.hitalk.data.local.entity.ConversationEntity
import kotlinx.coroutines.flow.Flow


interface ConversationDataSource {

    suspend fun updateAll(conversations: List<ConversationEntity>)

    suspend fun findById(conversationId: String): ConversationEntity

    suspend fun create(conversationId: String)

    fun fetchall(): Flow<List<ConversationEntity>>

}