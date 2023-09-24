package dev.hasangurbuz.hitalk.data.remote.impl

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import dev.hasangurbuz.hitalk.data.remote.ConversationApi
import dev.hasangurbuz.hitalk.data.remote.impl.FirebaseConstants.COLLECTION_CONVERSATIONS
import dev.hasangurbuz.hitalk.data.remote.impl.FirebaseConstants.KEY_ID
import dev.hasangurbuz.hitalk.data.remote.impl.FirebaseConstants.KEY_PARTICIPANTS
import dev.hasangurbuz.hitalk.data.remote.model.ConversationDto
import dev.hasangurbuz.hitalk.data.remote.model.Response
import dev.hasangurbuz.hitalk.data.remote.snapshotFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationApiImpl
@Inject constructor(private val firestore: FirebaseFirestore) : ConversationApi {

    private val conversationCollection = firestore.collection(COLLECTION_CONVERSATIONS)

    override suspend fun listen(userId: String): Flow<List<ConversationDto>> {
        return conversationCollection
            .whereArrayContains(KEY_PARTICIPANTS, userId)
            .snapshotFlow()
            .map { value: QuerySnapshot ->
                val dtoList = value.toObjects(ConversationDto::class.java)

                dtoList
            }
    }

    override suspend fun findByParticipants(participantIdList: List<String>): Response<ConversationDto> {
        try {
            var result =
                conversationCollection.whereEqualTo(KEY_PARTICIPANTS, participantIdList)
                    .get()
                    .await()

            if (!result.isEmpty) {
                return Response.Success(result.toObjects(ConversationDto::class.java).first())
            }

            result = conversationCollection.whereEqualTo(
                KEY_PARTICIPANTS,
                participantIdList.asReversed()
            )
                .get()
                .await()

            if (result.isEmpty) {
                return Response.Failed
            }

            return Response.Success(result.toObjects(ConversationDto::class.java).first())
        } catch (_: Exception) {
            return Response.Failed
        }
    }


//    private fun Query.snapshotFlow(): Flow<QuerySnapshot> = callbackFlow {
//        val listenerRegistration = addSnapshotListener { value, error ->
//            if (error != null) {
//                close()
//                return@addSnapshotListener
//            }
//            if (value != null)
//                trySend(value)
//        }
//        awaitClose {
//            listenerRegistration.remove()
//        }
//    }

    override suspend fun create(conversation: ConversationDto): Response<ConversationDto> {
        try {
            val result = conversationCollection
                .add(conversation)
                .await()
                .get()
                .await()

            if (!result.exists()) {
                return Response.Failed
            }

            return Response.Success(result.toObject(ConversationDto::class.java)!!)
        } catch (_: Exception) {
            return Response.Failed
        }
    }

    override suspend fun update(conversation: ConversationDto): Response<ConversationDto> {
        try {
            val oldConversationId = conversationCollection
                .whereEqualTo(KEY_ID, conversation.id)
                .get()
                .await()
                .first()
                .id



            conversationCollection
                .document(oldConversationId)
                .set(conversation)
                .await()

            val updated = conversationCollection
                .whereEqualTo(KEY_ID, conversation.id)
                .get()
                .await()

            if (updated.isEmpty) {
                return Response.Failed
            }


            return Response.Success(updated.toObjects(ConversationDto::class.java).first())

        } catch (_: Exception) {
            return Response.Failed
        }
    }

    override suspend fun findByUserId(userId: String): Response<List<ConversationDto>> {
        try {
            val result = conversationCollection
                .whereArrayContains(KEY_PARTICIPANTS, userId)
                .whereNotEqualTo("lastMessageId", null)
                .get()
                .await()

            return Response.Success(result.toObjects(ConversationDto::class.java))
        } catch (_: Exception) {
            return Response.Failed
        }
    }

    override suspend fun findById(conversationId: String): Response<ConversationDto> {
        try {
            val result = conversationCollection
                .whereEqualTo(KEY_ID, conversationId)
                .get()
                .await()

            if (result.isEmpty) {
                return Response.Failed
            }

            return Response.Success(result.toObjects(ConversationDto::class.java).first())
        } catch (ex: Exception) {
            return Response.Failed
        }
    }
}