package dev.hasangurbuz.hitalk.domain.model

data class ConversationItem(
    var conversation: Conversation,
    var imageUri: String? = null,
    var title: String? = null,
    var lastMessage: String? = null,
    var timestamp: String? = null
)