package dev.hasangurbuz.hitalk.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id: String? = null,
    var name: String? = null,
    var phoneNumber: String? = null,
    var imageUri: String? = null
) : Parcelable



