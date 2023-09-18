package dev.hasangurbuz.hitalk.remote.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dev.hasangurbuz.hitalk.data.model.Resource
import dev.hasangurbuz.hitalk.model.Message
import dev.hasangurbuz.hitalk.remote.firebase.FirebaseConstants.COLLECTION_MESSAGES
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageService
@Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
) {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    suspend fun findById(messageId: String): Resource<Message> {
        try {
            val snapshot = firebaseFirestore.collection(COLLECTION_MESSAGES)
                .whereEqualTo("id", messageId)
                .get()
                .await()

            if (snapshot.isEmpty) {
                return Resource.Failed
            }

            val message = snapshot.documents.first().toObject(Message::class.java)!!

            return Resource.Success(message)
        } catch (_: Exception) {
            return Resource.Failed
        }
    }

    suspend fun findByConversationId(conversationId: String): Resource<List<Message>> {
        return try {
            val result = firebaseFirestore.collection(COLLECTION_MESSAGES)
                .whereEqualTo("conversationId", conversationId)
                .get()
                .await()

            Resource.Success(result.toObjects(Message::class.java))
        } catch (_: Exception) {
            Resource.Failed
        }
    }

    suspend fun create(message: Message): Resource<Message> {
        try {
            val created = firebaseFirestore.collection(COLLECTION_MESSAGES)
                .add(message)
                .await()
                .get()
                .await()

            if (!created.exists()) {
                return Resource.Failed
            }
            return Resource.Success(created.toObject(Message::class.java)!!)
        } catch (_: Exception) {
            return Resource.Failed
        }
    }


    fun startListening(conversationId: String) {
        firebaseFirestore.collection(COLLECTION_MESSAGES)
            .whereEqualTo("conversationId", conversationId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("EXC", exception.message.toString())
                    return@addSnapshotListener
                }

                val data = mutableListOf<Message>()

                snapshot?.documents?.forEach { document ->
                    val message = document.toObject(Message::class.java)
                    message?.let {
                        data.add(it)
                    }
                }
                CoroutineScope(Dispatchers.Main).launch {
                    _messages.emit(data)
                }

            }
    }
}