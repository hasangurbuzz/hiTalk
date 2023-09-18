package dev.hasangurbuz.hitalk.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Conversation(
    var id: String? = null,
    var participants: List<String>? = null,
    var lastMessageId: String? = null
) : Parcelable