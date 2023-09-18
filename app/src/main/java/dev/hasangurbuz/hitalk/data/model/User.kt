package dev.hasangurbuz.hitalk.data.model

import android.net.Uri

data class User(
    val id: String,
    val imageUri: Uri,
    val name: String,
    val phoneNumber: String
)