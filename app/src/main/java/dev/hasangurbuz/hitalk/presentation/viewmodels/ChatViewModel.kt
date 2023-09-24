package dev.hasangurbuz.hitalk.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.hasangurbuz.hitalk.core.AuthContext
import dev.hasangurbuz.hitalk.data.local.entity.ConversationEntity
import dev.hasangurbuz.hitalk.domain.exception.NotFoundException
import dev.hasangurbuz.hitalk.domain.exception.ResourceException
import dev.hasangurbuz.hitalk.domain.model.Conversation
import dev.hasangurbuz.hitalk.domain.model.Message
import dev.hasangurbuz.hitalk.domain.usecase.GetMessages
import dev.hasangurbuz.hitalk.domain.usecase.SendMessage
import dev.hasangurbuz.hitalk.presentation.events.ChatEvent
import dev.hasangurbuz.hitalk.presentation.util.UIUtils
import dev.hasangurbuz.hitalk.presentation.viewstates.ChatMessageState
import dev.hasangurbuz.hitalk.presentation.viewstates.ChatTextInputState
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel
@Inject constructor(
    private val authContext: AuthContext,
    private val sendMessage: SendMessage,
    private val getMessages: GetMessages,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _chatState = MutableStateFlow(ChatMessageState())
    val chatState = _chatState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _chatInputState = MutableStateFlow(ChatTextInputState())
    val chatInputState = _chatInputState.asStateFlow()

    private val currentUser = authContext.currentUser!!

    private var getMessagesJob: Job? = null

    private var conversation = savedStateHandle.get<Conversation>("Conversation")
    init {
        val conversation = savedStateHandle.get<Conversation>("Conversation")
        conversation?.let {
            loadMessages(it.id)
        }
    }

    private fun loadMessages(conversationId: String) {
        getMessagesJob?.cancel()
        getMessagesJob =
            getMessages(conversationId)
                .onEach { newMessages ->
                    _chatState.value = chatState.value.copy(
                        messages = newMessages
                    )
                }.launchIn(viewModelScope)
    }

    fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.ClickSend -> {
                if (_chatInputState.value.text.trim().isEmpty()) {
                    viewModelScope.launch {
                        _eventFlow.emit(UIEvent.ShowSnackBar("Enter text to send"))
                    }
                } else {

                    val message = Message(
                        id = UUID.randomUUID().toString(),
                        content = _chatInputState.value.text,
                        timestamp = UIUtils.toString(LocalDateTime.now()),
                        senderId = currentUser.id,
                        conversationId = conversation!!.id
                    )

                    viewModelScope.launch {
                        try {
                            _chatInputState.value = chatInputState.value.copy("")
                            conversation = sendMessage(message, conversation!!)

                        } catch (e: NotFoundException) {
                            _eventFlow.emit(UIEvent.ShowSnackBar(e.message))
                        } catch (e: ResourceException) {
                            _eventFlow.emit(UIEvent.ShowSnackBar(e.message))
                        }
                    }
                }
            }

            is ChatEvent.MessageChanged -> {
                _chatInputState.value = _chatInputState.value.copy(
                    text = event.value
                )
            }
        }
    }

    sealed class UIEvent() {
        data class ShowSnackBar(val text: String) : UIEvent()
    }
}