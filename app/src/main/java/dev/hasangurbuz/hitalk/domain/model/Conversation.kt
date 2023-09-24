package dev.hasangurbuz.hitalk.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Conversation(
    val id: String,
    var lastMessageId: String?,
    val participants: List<String>
) : Parcelable