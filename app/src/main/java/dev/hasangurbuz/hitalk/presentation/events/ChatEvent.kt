package dev.hasangurbuz.hitalk.presentation.events

import dev.hasangurbuz.hitalk.domain.model.Conversation

sealed class ChatEvent {
    data object ClickSend : ChatEvent()
    data class MessageChanged(val value: String) : ChatEvent()
}