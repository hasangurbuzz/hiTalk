package dev.hasangurbuz.hitalk.domain.model

data class Message(
    val id: String,
    val content: String,
    val conversationId: String,
    val senderId: String,
    val timestamp: String
)