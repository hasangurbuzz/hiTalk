package dev.hasangurbuz.hitalk.presentation.events

sealed class LoginEvent {
    data object LoginClick : LoginEvent()

    data class PhoneNumberChanged(val phoneNumber: String) : LoginEvent()
}