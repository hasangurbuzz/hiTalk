package dev.hasangurbuz.hitalk.data.remote.model

import dev.hasangurbuz.hitalk.domain.model.Conversation

data class ConversationDto(
    var id: String? = null,
    var lastMessageId: String? = null,
    var participants: List<String>? = null


) {
    fun toConversation(): Conversation {
        return Conversation(
            id = id!!,
            lastMessageId = lastMessageId,
            participants = participants!!
        )
    }
}
