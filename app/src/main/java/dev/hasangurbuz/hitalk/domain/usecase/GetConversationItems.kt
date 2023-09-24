package dev.hasangurbuz.hitalk.domain.usecase

import dev.hasangurbuz.hitalk.data.local.entity.Contact
import dev.hasangurbuz.hitalk.domain.model.Conversation
import dev.hasangurbuz.hitalk.domain.model.ConversationItem
import dev.hasangurbuz.hitalk.domain.model.Message
import dev.hasangurbuz.hitalk.domain.model.Resource
import dev.hasangurbuz.hitalk.domain.model.User
import dev.hasangurbuz.hitalk.domain.repository.ContactRepository
import dev.hasangurbuz.hitalk.domain.repository.ConversationRepository
import dev.hasangurbuz.hitalk.domain.repository.MessageRepository
import dev.hasangurbuz.hitalk.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetConversationItems
@Inject constructor(
    private val conversationRepo: ConversationRepository,
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository,
    private val contactRepository: ContactRepository
) {


    suspend operator fun invoke(
        userId: String
    ): Flow<List<ConversationItem>> {
        val conversations: Flow<List<Conversation>> = conversationRepo.fetch(userId)
        val localContacts = contactRepository.fetch()

        if (localContacts.isEmpty()) {
            return emptyFlow()
        }

        val contacts = ContactUtility.normalize(localContacts)


        val conversationItems = conversations.map {
            it.mapNotNull { conversation ->
                val userResult = userRepository.findById(conversation.participants)
                var conversationItem: ConversationItem? = null
                val message: Message
                if (userResult is Resource.Success) {
                    val user = userResult.data.first { !User::id.equals(userId) }
                    val messageResult =
                        messageRepository.findById(conversation.lastMessageId!!)
                    if (messageResult is Resource.Success) {
                        message = messageResult.data
                        val contact =
                            contacts.firstOrNull() { contact -> contact.phoneNumber == user.phoneNumber }
                        var itemTitle: String = ""
                        itemTitle = contact?.name ?: user.phoneNumber
                        conversationItem = ConversationItem(
                            conversation = conversation,
                            imageUri = user.imageUri.toString(),
                            title = itemTitle,
                            lastMessage = message.content,
                            timestamp = message.timestamp
                        )

                    }
                }
                conversationItem

            }

        }

        return conversationItems
    }

}