package dev.hasangurbuz.hitalk.data.remote.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dev.hasangurbuz.hitalk.data.remote.MessageApi
import dev.hasangurbuz.hitalk.data.remote.impl.FirebaseConstants.COLLECTION_MESSAGES
import dev.hasangurbuz.hitalk.data.remote.impl.FirebaseConstants.KEY_CONVERSATION_ID
import dev.hasangurbuz.hitalk.data.remote.impl.FirebaseConstants.KEY_ID
import dev.hasangurbuz.hitalk.data.remote.model.MessageDto
import dev.hasangurbuz.hitalk.data.remote.model.Response
import dev.hasangurbuz.hitalk.data.remote.snapshotFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageApiImpl
@Inject constructor(private val firestore: FirebaseFirestore) : MessageApi {

    private val messageCollection = firestore.collection(COLLECTION_MESSAGES)

    override suspend fun create(messageDto: MessageDto): Response<MessageDto> {
        try {
            val result = messageCollection
                .add(messageDto)
                .await()
                .get()
                .await()

            if (!result.exists()) {
                return Response.Failed
            }

            return Response.Success(result.toObject(MessageDto::class.java)!!)
        } catch (_: Exception) {
            return Response.Failed
        }
    }

    override suspend fun findById(messageId: String): Response<MessageDto> {
        try {
            val result = messageCollection
                .whereEqualTo(KEY_ID, messageId)
                .get()
                .await()

            if (result.isEmpty) {
                return Response.Failed
            }

            return Response.Success(result.toObjects(MessageDto::class.java).first())
        } catch (_: Exception) {
            return Response.Failed
        }
    }

    override suspend fun findByConversation(conversationId: String): Response<List<MessageDto>> {
        try {
            val result = messageCollection
                .whereEqualTo(KEY_CONVERSATION_ID, conversationId)
                .get()
                .await()


            return Response.Success(result.toObjects(MessageDto::class.java))
        } catch (_: Exception) {
            return Response.Failed
        }
    }

    override fun listen(conversationId: String): Flow<List<MessageDto>> {
        return messageCollection
            .whereEqualTo(KEY_CONVERSATION_ID, conversationId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .snapshotFlow()
            .map { value ->
                value.toObjects(MessageDto::class.java)
            }

    }


}