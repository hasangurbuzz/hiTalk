package dev.hasangurbuz.hitalk.domain.usecase

import dev.hasangurbuz.hitalk.domain.exception.NotFoundException
import dev.hasangurbuz.hitalk.domain.exception.ResourceException
import dev.hasangurbuz.hitalk.domain.model.Conversation
import dev.hasangurbuz.hitalk.domain.model.Message
import dev.hasangurbuz.hitalk.domain.model.Resource
import dev.hasangurbuz.hitalk.domain.repository.ConversationRepository
import dev.hasangurbuz.hitalk.domain.repository.MessageRepository
import javax.inject.Inject

class SendMessage
@Inject constructor(
    private val messageRepository: MessageRepository,
    private val conversationRepository: ConversationRepository
) {

    @Throws(NotFoundException::class, ResourceException::class)
    suspend operator fun invoke(message: Message, conversation: Conversation): Conversation {
        val result = messageRepository.create(message)
        if (result is Resource.Failed) {
            throw ResourceException("Failed to send")
        }

        val sentMessage = (result as Resource.Success).data

        conversation.lastMessageId = sentMessage.id

        val existingConversation = conversationRepository.findById(conversation.id)

        if (existingConversation is Resource.Failed) {
            val createResult = conversationRepository.create(conversation)
            if (createResult is Resource.Failed) {
                throw ResourceException("conversation could not created")
            }
            val createdConversation = (createResult as Resource.Success).data
            return createdConversation
        }

        val updateResult = conversationRepository.update(conversation)

        if (updateResult is Resource.Failed) {
            throw ResourceException("Failed to send message")
        }

        val updatedConversation = (updateResult as Resource.Success).data

        return updatedConversation
    }

}