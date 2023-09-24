package dev.hasangurbuz.hitalk.data.remote.model

import dev.hasangurbuz.hitalk.domain.model.Message

data class MessageDto(
    var id: String? = null,
    var content: String? = null,
    var conversationId: String? = null,
    var senderId: String? = null,
    var timestamp: String? = null
) {
    fun toMessage(): Message {
        return Message(
            id = id!!,
            content = content!!,
            conversationId = conversationId!!,
            senderId = senderId!!,
            timestamp = timestamp!!
        )
    }
}