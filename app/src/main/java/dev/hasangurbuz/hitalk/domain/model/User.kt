package dev.hasangurbuz.hitalk.domain.model

import android.net.Uri

data class User(
    var id: String,
    val imageUri: Uri,
    val name: String,
    val phoneNumber: String
)