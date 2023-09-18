package dev.hasangurbuz.hitalk.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.hasangurbuz.hitalk.data.model.Resource
import dev.hasangurbuz.hitalk.data.usecase.GetConversationItemUseCase
import dev.hasangurbuz.hitalk.model.Conversation
import dev.hasangurbuz.hitalk.model.ConversationItem
import dev.hasangurbuz.hitalk.model.User
import dev.hasangurbuz.hitalk.remote.firebase.ConversationService
import dev.hasangurbuz.hitalk.remote.firebase.MessageService
import dev.hasangurbuz.hitalk.remote.firebase.UserService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel
@Inject constructor(
    private val conversationService: ConversationService,
    private val messageService: MessageService,
    private val userService: UserService,
    private val getConversationItemUseCase: GetConversationItemUseCase

) : ViewModel() {
    private val _appUser = MutableStateFlow<User?>(null)
    val appUser = _appUser.asStateFlow()
    val conversations = conversationService.conversations

    private val _conversationItems = MutableStateFlow<List<ConversationItem>>(emptyList())
    var conversationItems = _conversationItems.asStateFlow()

    init {
        viewModelScope.launch {
            appUser.value?.let {
                conversationItems = getConversationItemUseCase(appUser.value?.id!!).stateIn(
                    viewModelScope, SharingStarted.WhileSubscribed(), emptyList()
                )
            }
        }
    }

    fun load(){
        viewModelScope.launch {
            appUser.value?.let {
                getConversationItemUseCase(appUser.value?.id!!).collect{
                    _conversationItems.emit(it)
                }
            }
        }
    }
//    val conversationItems = conversations.map { conversations ->
//        val data = mutableListOf<ConversationItem>()
//        for (conversation in conversations) {
//            val conversationItem = ConversationItem()
//            conversationItem.conversation = conversation
//            val part = getParticipants(conversation.participants!!)
//
//            conversation.lastMessageId?.let {
//                val message = messageService.findById(it)
//                conversationItem.lastMessage = (message as Resource.Success).data.content
//                conversationItem.timestamp = (message as Resource.Success).data.timestamp
//
//
//                val user = (userService.findById(part[0]) as Resource.Success).data
//
//                conversationItem.imageUri = user.imageUri
//                conversationItem.title = user.name
//                data.add(conversationItem)
//            }
//        }
//        data
//    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val mapOfTwo = mutableMapOf<Conversation, String>()

    fun setAppUser(user: User) {
        viewModelScope.launch {
            _appUser.emit(user)
            conversationService.startListening(user.id!!)
        }
    }


    fun loadConversations() {
//        viewModelScope.launch {
//            delay(500L)
//            val result = conversationService.findByUserId(appUser.value!!.id!!)
//            if (result is Resource.Failed) {
//                Log.e("CON", "Fail")
//                return@launch
//            }
//            val conversations = (result as Resource.Success).data
//            Log.e("CON", "Conversation found ${conversations.size}")
//
//            val items = mutableListOf<ConversationItem>()
//            for (conversation in conversations) {
//                val participants = conversation.participants
//                mapOfTwo.put(conversation, participants!!.get(0))
//                val lastMessageId = conversation.lastMessageId
//                if (lastMessageId == null) {
//                    continue
//                }
//                val messageResponse = messageService.findById(lastMessageId)
//                if (messageResponse is Resource.Failed) {
//                    Log.e("CON", "Message not found")
//                    continue
//                }
//                val message = (messageResponse as Resource.Success).data
//                Log.e("CON", "message found")
//
//                val userResponse = userService.findById(message.senderId!!)
//                if (userResponse is Resource.Failed) {
//                    Log.e("CON", "user not found")
//                    continue
//                }
//                val user = (userResponse as Resource.Success).data
//                Log.e("CON", "user found")
//
//                val conversationItem = ConversationItem(
//                    title = user.name.toString(),
//                    timestamp = message.timestamp!!,
//                    lastMessage = message.content.toString(),
//                    imageUri = user.imageUri.toString()
//                )
//                items.add(conversationItem)
//
//            }
//            Log.e("CON", "emitting ${items.size} items")
//
//
//            _conversationItems.emit(items)
//            //conversationları aldın
//            // loop over conversation
//            //      lastmessageId al
//            //      id ile mesaj çek
//            //      mesaj içindeki userId ile user çek
//            //      recyclerviewa (conversation-lastmessage-user) dön
//        }
    }

    private fun getParticipants(participants: List<String>): MutableList<String> {
        val list = mutableListOf<String>()
        for (id in participants) {
            if (appUser.value!!.id!! == id) {
                continue
            }
            list.add(id)
        }
        return list
    }


}