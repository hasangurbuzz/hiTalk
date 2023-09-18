package dev.hasangurbuz.hitalk.data.model

data class Message(
    val id: String,
    val content: String,
    val conversationId: String,
    val senderId : String,
    val timestamp: String
)