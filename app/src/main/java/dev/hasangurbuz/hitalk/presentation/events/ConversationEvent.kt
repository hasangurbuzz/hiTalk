package dev.hasangurbuz.hitalk.presentation.events

import dev.hasangurbuz.hitalk.domain.model.Conversation

sealed class ConversationEvent {
    data class ConversationClick(val conversation: Conversation) : ConversationEvent()
    data object FabClick : ConversationEvent()
}