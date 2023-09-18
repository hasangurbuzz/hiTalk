package dev.hasangurbuz.hitalk.data.usecase

import android.util.Log
import dev.hasangurbuz.hitalk.data.model.Message
import dev.hasangurbuz.hitalk.data.model.Resource
import dev.hasangurbuz.hitalk.data.model.User
import dev.hasangurbuz.hitalk.data.repository.ConversationRepository
import dev.hasangurbuz.hitalk.data.repository.MessageRepository
import dev.hasangurbuz.hitalk.data.repository.UserRepository
import dev.hasangurbuz.hitalk.model.ConversationItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetConversationItemUseCase
@Inject constructor(
    private val conversationRepo: ConversationRepository,
    private val userRepository: UserRepository,
    private val messageRepository: MessageRepository
) {


    suspend operator fun invoke(userId: String): Flow<List<ConversationItem>> {
        val conversations = conversationRepo.loadFoundConversations(userId)
        Log.e("SUB", "usecase run ${conversations.count()}")

//        return conversations.map {
//            it.map {
//                ConversationItem(
//                    conversation = it,
//                    timestamp = "2023-09-18T04:15:14.913",
//                    lastMessage = "message",
//                    title = "title",
//                    imageUri = "image uri"
//                )
//            }
//        }
        val conversationItems = conversations.map {
            it.map { conversation ->
                val userResult = userRepository.findById(conversation.participants)
                var conversationItem: ConversationItem? = null
                var message: Message? = null
                Log.e("USE", "called users")
                if (userResult is Resource.Success) {
                    Log.e("USE", "found users")
                    val user = userResult.data.filter { !User::id.equals(userId) }.first()
                    val messageResult =
                        messageRepository.findById(conversation.lastMessageId)
                    if (messageResult is Resource.Success) {
                        message = messageResult.data
                        conversationItem = ConversationItem(
                            conversation = conversation,
                            imageUri = user.imageUri.toString(),
                            title = user.name,
                            lastMessage = message.content,
                            timestamp = message.timestamp
                        )

                    }
                }
                if (message != null){
                    conversationItem!!
                }else{
                    
                }

            }

        }

        return conversationItems
    }

}