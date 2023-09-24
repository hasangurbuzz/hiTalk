package dev.hasangurbuz.hitalk.presentation.viewstates

import android.net.Uri

data class RegisterInputState(
    val username: String = "",
    val phoneNumber: String = "",
    val imageUri: Uri = Uri.EMPTY
)