package dev.hasangurbuz.hitalk.data.remote.impl

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import dev.hasangurbuz.hitalk.data.remote.ConversationApi
import dev.hasangurbuz.hitalk.data.remote.model.ConversationDto
import dev.hasangurbuz.hitalk.data.remote.model.Response
import dev.hasangurbuz.hitalk.remote.firebase.FirebaseConstants.COLLECTION_CONVERSATIONS
import dev.hasangurbuz.hitalk.remote.firebase.FirebaseConstants.KEY_ID
import dev.hasangurbuz.hitalk.remote.firebase.FirebaseConstants.KEY_PARTICIPANTS
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationApiImpl
@Inject constructor(private val firestore: FirebaseFirestore) : ConversationApi {

    private val conversationCollection = firestore.collection(COLLECTION_CONVERSATIONS)

    override fun listenLatest(userId: String) =
        conversationCollection.whereArrayContains(KEY_PARTICIPANTS, userId).snapshots().map {
            it.toObjects(ConversationDto::class.java)
        }


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
            val stored = conversationCollection
                .whereEqualTo(KEY_ID, conversation.id)
                .get()
                .await()

            if (stored.isEmpty) {
                return Response.Failed
            }

            conversationCollection
                .document(stored.documents.first().id)
                .set(conversation)
                .await()

            return Response.Success(stored.toObjects(ConversationDto::class.java).first())

        } catch (_: Exception) {
            return Response.Failed
        }
    }

    override suspend fun findByUserId(userId: String): Response<List<ConversationDto>> {
        try {
            val result = conversationCollection
                .whereArrayContains(KEY_PARTICIPANTS, userId)
                .get()
                .await()

            Log.e("FO", result.documents.size.toString())
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
        } catch (_: Exception) {
            return Response.Failed
        }
    }
}