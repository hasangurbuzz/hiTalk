package dev.hasangurbuz.hitalk.data.remote.model

data class ConversationDto(
    var id : String? = null,
    var lastMessageId : String? = null,
    var participants: List<String>? = null
)