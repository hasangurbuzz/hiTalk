package dev.hasangurbuz.hitalk.data.remote.impl

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import dev.hasangurbuz.hitalk.data.remote.ImageApi
import dev.hasangurbuz.hitalk.data.remote.model.Response
import dev.hasangurbuz.hitalk.remote.firebase.FirebaseConstants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageApiImpl
    @Inject constructor(private val firebaseStorage: FirebaseStorage): ImageApi {


    override suspend fun upload(
        collectionName: String,
        imageUri: Uri,
        fileName: String
    ): Response<Uri> {

        try {
            val ref = firebaseStorage.reference
                .child(FirebaseConstants.PATH_IMAGES)
                .child(collectionName)
                .child("$fileName${FirebaseConstants.IMAGE_EXT}")

            val taskSnapshot = ref.putFile(imageUri).await()
            if (!taskSnapshot.task.isSuccessful) {
                return Response.Failed
            }
            val downloadUrl = taskSnapshot.storage.downloadUrl.await()
            return Response.Success(downloadUrl)
        } catch (_: Exception) {
            return Response.Failed
        }

    }

}