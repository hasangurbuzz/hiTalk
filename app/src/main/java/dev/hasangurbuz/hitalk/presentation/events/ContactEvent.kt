package dev.hasangurbuz.hitalk.presentation.events

import dev.hasangurbuz.hitalk.domain.model.User

sealed class ContactEvent {
    data class ContactClick(val user: User) : ContactEvent()
}