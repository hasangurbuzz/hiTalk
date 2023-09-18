package dev.hasangurbuz.hitalk.model

import java.time.LocalDateTime
import java.time.OffsetDateTime

data class Message(
    var id: String? = null,
    var senderId: String? = null,
    var timestamp: String? = null,
    var conversationId: String? = null,
    var content: String? = null
)