package dev.hasangurbuz.hitalk.domain.usecase

import dev.hasangurbuz.hitalk.domain.model.Conversation
import dev.hasangurbuz.hitalk.domain.model.Resource
import dev.hasangurbuz.hitalk.domain.repository.ConversationRepository
import java.util.UUID
import javax.inject.Inject

class GetContactConversations
@Inject constructor(private val conversationRepository: ConversationRepository) {

    suspend operator fun invoke(participants: List<String>): Conversation {
        val result = conversationRepository.findByParticipants(participants)
        when (result) {
            is Resource.Success -> {
                return result.data
            }

            is Resource.Failed -> {
                val conversation = Conversation(
                    id = UUID.randomUUID().toString(),
                    participants = participants,
                    lastMessageId = null
                )
                return conversation
            }
        }
    }
}