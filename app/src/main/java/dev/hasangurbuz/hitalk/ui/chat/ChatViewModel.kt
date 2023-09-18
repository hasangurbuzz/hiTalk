package dev.hasangurbuz.hitalk.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.hasangurbuz.hitalk.data.model.Conversation
import dev.hasangurbuz.hitalk.data.model.Resource.Success
import dev.hasangurbuz.hitalk.model.Message
import dev.hasangurbuz.hitalk.remote.firebase.ConversationService
import dev.hasangurbuz.hitalk.remote.firebase.MessageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import dev.hasangurbuz.hitalk.data.model.Conversation as Conversation1

@HiltViewModel
class ChatViewModel
@Inject constructor(
    private val messageService: MessageService,
    private val conversationService: ConversationService
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = messageService.messages

    private val _conversation = MutableStateFlow<Conversation?>(null)
    val conversation = _conversation.asStateFlow()

    fun setConversation(conversation: Conversation1) {
        viewModelScope.launch {
            _conversation.emit(conversation)
            messageService.startListening(conversation.id!!)
        }
    }

    fun loadMessages(conversationId: String) {
        viewModelScope.launch {
            val result = messageService.findByConversationId(conversationId)
            if (result is Success) {
                _messages.emit(result.data)
            }
        }
    }

    fun sendMessage(message: Message) {
//        viewModelScope.launch {
//            val result = messageService.create(message)
//            if (result is Success) {
//                val updatedConversation = conversation.value!!
//                updatedConversation.lastMessageId = result.data.id
//                conversationService.update(updatedConversation)
//            }
//        }
    }
}