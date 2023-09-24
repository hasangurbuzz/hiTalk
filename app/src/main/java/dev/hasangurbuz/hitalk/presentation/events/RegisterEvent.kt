package dev.hasangurbuz.hitalk.presentation.events

import android.net.Uri

sealed class RegisterEvent {
    data object RegisterClick : RegisterEvent()
    data class UsernameChanged(val username: String) : RegisterEvent()

    data class PhoneNumberChanged(val phoneNumber: String) : RegisterEvent()

    data class ImageChanged(val imageUri: Uri) : RegisterEvent()
}