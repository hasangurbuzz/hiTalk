package dev.hasangurbuz.hitalk.data.remote.impl

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import dev.hasangurbuz.hitalk.data.remote.UserApi
import dev.hasangurbuz.hitalk.data.remote.impl.FirebaseConstants.COLLECTION_USERS
import dev.hasangurbuz.hitalk.data.remote.impl.FirebaseConstants.KEY_ID
import dev.hasangurbuz.hitalk.data.remote.impl.FirebaseConstants.KEY_USER_PHONE
import dev.hasangurbuz.hitalk.data.remote.model.Response
import dev.hasangurbuz.hitalk.data.remote.model.UserDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserApiImpl
@Inject constructor(private val firestore: FirebaseFirestore) : UserApi {

    private val userCollection = firestore.collection(COLLECTION_USERS)

    override suspend fun create(user: UserDto): Response<UserDto> {
        try {

            val result = userCollection
                .add(user)
                .await()
                .get()
                .await()

            if (!result.exists()) {
                return Response.Failed
            }
            return Response.Success(result.toObject(UserDto::class.java)!!)

        } catch (_: Exception) {
            return Response.Failed
        }
    }

    override suspend fun findByPhone(phoneNumber: String): Response<UserDto> {
        try {
            val result = userCollection
                .whereEqualTo(KEY_USER_PHONE, phoneNumber)
                .get()
                .await()

            if (result.isEmpty) {
                return Response.Failed
            }
            return Response.Success(result.toObjects(UserDto::class.java).first())
        } catch (_: Exception) {
            return Response.Failed
        }
    }

    // number list splitted to sublists that each list has 10 capacity
    // because of firebase does not support more than 10 "where in" query parameters
    override suspend fun findByPhone(phoneNumberList: List<String>): Response<List<UserDto>> {
        val users = mutableListOf<UserDto>()
        try {
            var index = 0
            while (index < phoneNumberList.size) {
                val chunk = phoneNumberList.subList(
                    index,
                    kotlin.math.min(index + 10, phoneNumberList.size)
                )
                index += chunk.size


                val result = userCollection.whereIn(KEY_USER_PHONE, chunk)
                    .get()
                    .await()


                if (result.isEmpty) {
                    continue
                }
                users.addAll(result.toObjects(UserDto::class.java))
            }
        } catch (ex: Exception) {
            return Response.Failed
        }
        return Response.Success(users)
    }

    override suspend fun findById(userId: String): Response<UserDto> {
        try {
            val result = userCollection
                .whereEqualTo(KEY_ID, userId)
                .get()
                .await()

            if (result.isEmpty) {
                return Response.Failed
            }

            return Response.Success(result.toObjects(UserDto::class.java).first())
        } catch (_: Exception) {
            return Response.Failed
        }
    }

    override suspend fun findById(userIdList: List<String>): Response<List<UserDto>> {
        try {
            val result = userCollection
                .whereIn(KEY_ID, userIdList)
                .get()
                .await()

            if (result.isEmpty) {
                return Response.Failed
            }

            return Response.Success(result.toObjects(UserDto::class.java))
        } catch (ex: Exception) {
            return Response.Failed
        }
    }


}