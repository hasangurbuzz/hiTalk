package dev.hasangurbuz.hitalk.presentation.viewstates

import dev.hasangurbuz.hitalk.domain.model.Message

data class ChatMessageState(
    val messages: List<Message> = emptyList()
)