package dev.hasangurbuz.hitalk.model

import dev.hasangurbuz.hitalk.data.model.Conversation

data class ConversationItem(
    var conversation: Conversation,
    var imageUri: String? = null,
    var title: String? = null,
    var lastMessage: String? = null,
    var timestamp: String? = null
)