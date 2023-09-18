package dev.hasangurbuz.hitalk.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Conversation (
    val id :String,
    val lastMessageId: String,
    val participants: List<String>
): Parcelable