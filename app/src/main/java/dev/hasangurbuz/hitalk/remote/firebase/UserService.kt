package dev.hasangurbuz.hitalk.remote.firebase

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import dev.hasangurbuz.hitalk.data.model.Resource
import dev.hasangurbuz.hitalk.model.User
import dev.hasangurbuz.hitalk.remote.firebase.FirebaseConstants.COLLECTION_USERS
import dev.hasangurbuz.hitalk.remote.firebase.FirebaseConstants.KEY_ID
import dev.hasangurbuz.hitalk.remote.firebase.FirebaseConstants.KEY_USER_PHONE
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserService
@Inject constructor(private val firestore: FirebaseFirestore){


    @Inject
    lateinit var imageService: ImageService
    private val users = firestore.collection(COLLECTION_USERS)

    suspend fun findByPhoneNumber(phoneNumber: String): Resource<User> {

        val result: QuerySnapshot
        try {
            result = users.whereEqualTo(KEY_USER_PHONE, phoneNumber)
                .get()
                .await()

        } catch (_: FirebaseFirestoreException) {
            return Resource.Failed
        }

        if (result.isEmpty) {
            return Resource.Failed
        }

        val document = result.documents.first()

        val user = document.toObject(User::class.java)!!

        return Resource.Success(user)

    }

    suspend fun create(user: User): Resource<User> {
        try {
            val uploadImageResult =
                imageService.uploadImage(user.id!!, Uri.parse(user.imageUri), "profile")

            if (uploadImageResult is Resource.Failed) {
                return uploadImageResult
            }

            val imageUri = (uploadImageResult as Resource.Success).data.toString()


            user.imageUri = imageUri

            val reference = firestore.collection(COLLECTION_USERS)
                .add(user)
                .await()


            val result = reference.get().await()
            if (!result.exists()) {
                return Resource.Failed
            }

            val createdUser = result.toObject(User::class.java)!!

            return Resource.Success(createdUser)
        } catch (_: FirebaseFirestoreException) {
            return Resource.Failed

        }

    }

    suspend fun findById(userId: String): Resource<User> {

        try {
            val result: QuerySnapshot
            result = firestore.collection(COLLECTION_USERS)
                .whereEqualTo(KEY_ID, userId)
                .get().await()

            if (result.isEmpty) {
                return Resource.Failed
            }


            val document = result.documents.first()

            val user = document.toObject(User::class.java)


            return Resource.Success(user!!)

        } catch (_: FirebaseFirestoreException) {
            return Resource.Failed
        }
    }

    suspend fun findAll(): Resource<List<User>> {
        try {
            val result = firestore.collection(COLLECTION_USERS)
                .get().await()

            if (result.isEmpty) {
                return Resource.Failed
            }

            val users = result.toObjects(User::class.java)

            return Resource.Success(users)

        } catch (_: Exception) {
            return Resource.Failed
        }
    }


}
