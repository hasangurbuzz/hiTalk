package dev.hasangurbuz.hitalk.data.remote.model

import android.net.Uri
import dev.hasangurbuz.hitalk.domain.model.User

data class UserDto(
    var id: String? = null,
    var imageUri: String? = null,
    var name: String? = null,
    var phoneNumber: String? = null
) {
    fun toUser(): User {
        return User(
            id = id!!,
            imageUri = Uri.parse(imageUri),
            name = name!!,
            phoneNumber = phoneNumber!!
        )
    }
}