package dev.hasangurbuz.hitalk.data.remote.model

data class MessageDto(
    val id : String,
    val content: String,
    val conversationId: String,
    val senderId: String,
    val timestamp: String
)