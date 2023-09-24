package dev.hasangurbuz.hitalk.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.hasangurbuz.hitalk.core.AuthContext
import dev.hasangurbuz.hitalk.data.local.ConversationDataSource
import dev.hasangurbuz.hitalk.data.local.entity.ConversationEntity
import dev.hasangurbuz.hitalk.domain.model.Conversation
import dev.hasangurbuz.hitalk.domain.model.ConversationItem
import dev.hasangurbuz.hitalk.domain.usecase.GetConversationItems
import dev.hasangurbuz.hitalk.presentation.events.ConversationEvent
import io.realm.kotlin.ext.realmListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel
@Inject constructor(
    private val getConversationItems: GetConversationItems,
    private val authContext: AuthContext
) : ViewModel() {

    private var _conversationItems = MutableStateFlow<List<ConversationItem>>(emptyList())
    var conversationItems = _conversationItems.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val appUser = authContext.currentUser!!

    init {
        viewModelScope.launch {

            getConversationItems(appUser.id).collect {
                _conversationItems.value = it
            }


        }

    }

    fun onEvent(event: ConversationEvent) {
        when (event) {
            is ConversationEvent.ConversationClick -> {
                viewModelScope.launch {
                    _eventFlow.emit(UIEvent.StartChat(event.conversation))
                }
            }

            is ConversationEvent.FabClick -> {
                viewModelScope.launch {
                    _eventFlow.emit(UIEvent.NavigateContacts)
                }
            }

            else -> {}
        }
    }

    sealed class UIEvent {
        data class StartChat(val conversation: Conversation) : UIEvent()
        data object NavigateContacts : UIEvent()
        data object NotFoundConversation : UIEvent()
    }
}