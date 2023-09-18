package dev.hasangurbuz.hitalk.remote.firebase

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import dev.hasangurbuz.hitalk.data.model.Resource
import dev.hasangurbuz.hitalk.remote.firebase.FirebaseConstants.IMAGE_EXT
import dev.hasangurbuz.hitalk.remote.firebase.FirebaseConstants.PATH_IMAGES
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageService
@Inject constructor(
    private val firebaseStorage: FirebaseStorage
) {

    suspend fun uploadImage(
        collectionName: String,
        imageUri: Uri,
        fileName: String
    ): Resource<Uri> {
        val ref: StorageReference
        val taskSnapshot: UploadTask.TaskSnapshot

        try {
            ref = firebaseStorage.reference
                .child(PATH_IMAGES)
                .child(collectionName)
                .child("$fileName$IMAGE_EXT")

            taskSnapshot = ref.putFile(imageUri).await()
            if (taskSnapshot.task.isSuccessful) {
                val downloadUrl = taskSnapshot.storage.downloadUrl.await()
                return Resource.Success(downloadUrl)
            }


        } catch (_: StorageException) {
            return Resource.Failed
        }

        return Resource.Failed
    }


}