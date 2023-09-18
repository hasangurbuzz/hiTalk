package dev.hasangurbuz.hitalk.ui.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.hasangurbuz.hitalk.model.Conversation
import dev.hasangurbuz.hitalk.model.User
import dev.hasangurbuz.hitalk.remote.firebase.ConversationService
import dev.hasangurbuz.hitalk.data.model.Resource
import dev.hasangurbuz.hitalk.remote.firebase.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ContactViewModel
@Inject constructor(
    private val userService: UserService,
    private val conversationService: ConversationService
) :
    ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()

    private val _conversation = MutableStateFlow<Conversation?>(null)
    val conversation = _conversation.asStateFlow()


    fun loadUsers() {
        viewModelScope.launch {
            val result = userService.findAll()
            if (result is Resource.Success) {
                val users = result.data
                _users.emit(users)
            }
        }
    }

    fun createConversation(participants: List<User>) {
        viewModelScope.launch {
            val userIdList = participants.map { user -> user.id!! }
            val conversation = Conversation(
                id = UUID.randomUUID().toString(),
                participants = userIdList,
                lastMessageId = null
            )
            val result = conversationService.create(conversation)
            if (result is Resource.Success) {
                _conversation.emit(result.data)
            }
        }
    }

}

