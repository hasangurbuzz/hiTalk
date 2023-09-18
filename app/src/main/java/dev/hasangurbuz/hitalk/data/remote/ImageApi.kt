package dev.hasangurbuz.hitalk.data.remote

import android.net.Uri
import dev.hasangurbuz.hitalk.data.remote.model.Response

interface ImageApi {
    suspend fun upload(
        collectionName: String,
        imageUri: Uri,
        fileName: String
    ): Response<Uri>
}