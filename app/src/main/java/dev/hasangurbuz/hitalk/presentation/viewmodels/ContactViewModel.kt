package dev.hasangurbuz.hitalk.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.hasangurbuz.hitalk.core.AuthContext
import dev.hasangurbuz.hitalk.domain.exception.ResourceException
import dev.hasangurbuz.hitalk.domain.model.Conversation
import dev.hasangurbuz.hitalk.domain.model.User
import dev.hasangurbuz.hitalk.domain.usecase.GetContactConversations
import dev.hasangurbuz.hitalk.domain.usecase.GetContacts
import dev.hasangurbuz.hitalk.presentation.events.ContactEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel
@Inject constructor(
    private val getContacts: GetContacts,
    private val getContactConversations: GetContactConversations,
    private val authContext: AuthContext

) :
    ViewModel() {

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _contacts = MutableStateFlow<List<User>>(emptyList())
    val contacts = _contacts.asStateFlow()

//    val contacts = flow {
//        emit(getContacts())
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000L),
//        initialValue = emptyList()
//    )


    init {
        viewModelScope.launch {
            _contacts.emit(getContacts())
        }
    }


    fun onEvent(event: ContactEvent) {
        when (event) {
            is ContactEvent.ContactClick -> {
                val participants = listOf(authContext.currentUser!!.id, event.user.id)
                viewModelScope.launch {
                    try {
                        val conversation = getContactConversations(participants)
                        _eventFlow.emit(UIEvent.NavigateChat(conversation))
                    } catch (e: ResourceException) {
                        _eventFlow.emit(UIEvent.ShowSnackBar(e.message))
                    }

                }
            }
        }
    }


    sealed class UIEvent() {
        data class NavigateChat(val conversation: Conversation) :
            UIEvent()

        data class ShowSnackBar(val content: String) : UIEvent()
    }


}

