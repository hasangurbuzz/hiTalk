package dev.hasangurbuz.hitalk.remote.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import dev.hasangurbuz.hitalk.data.model.Resource
import dev.hasangurbuz.hitalk.data.remote.model.ConversationDto
import dev.hasangurbuz.hitalk.model.Conversation
import dev.hasangurbuz.hitalk.remote.firebase.FirebaseConstants.COLLECTION_CONVERSATIONS
import dev.hasangurbuz.hitalk.remote.firebase.FirebaseConstants.KEY_PARTICIPANTS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationService @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations = _conversations.asStateFlow()

    suspend fun create(conversation: Conversation): Resource<Conversation> {
        try {
            val reference = firestore.collection(COLLECTION_CONVERSATIONS)
                .add(conversation)
                .await()

            val response = reference.get().await()
            if (!response.exists()) {
                return Resource.Failed
            }
            return Resource.Success(response.toObject(conversation::class.java)!!)
        } catch (_: Exception) {
            return Resource.Failed
        }
    }

    suspend fun update(conversation: Conversation): Resource<Conversation> {
        try {
            val documents = firestore.collection(COLLECTION_CONVERSATIONS)
                .whereEqualTo("id", conversation.id)
                .get().await()

            if (documents.isEmpty) {
                return Resource.Failed
            }

            val foundConversation = documents.first()

            firestore.collection(COLLECTION_CONVERSATIONS)
                .document(foundConversation.id)
                .set(conversation).await()

            return Resource.Success(foundConversation.toObject(Conversation::class.java))

        } catch (_: Exception) {
            return Resource.Failed
        }
    }

    suspend fun findByUserId(userId: String): Resource<List<Conversation>> {
        val result: QuerySnapshot

        try {
            result = firestore.collection(COLLECTION_CONVERSATIONS)
                .whereArrayContains("participants", userId)
                .get().await()

            if (result.isEmpty) {
                return Resource.Failed
            }

            val conversations = result.toObjects(Conversation::class.java)
            return Resource.Success(conversations)

        } catch (_: FirebaseFirestoreException) {
            return Resource.Failed
        }

    }

    fun startListening(userId: String) {
        firestore.collection(COLLECTION_CONVERSATIONS)
            .whereArrayContains("participants", userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("EXC", exception.message.toString())
                    return@addSnapshotListener
                }

                val data = mutableListOf<Conversation>()

                snapshot?.documents?.forEach { document ->
                    val conversation = document.toObject(Conversation::class.java)
                    conversation?.let {
                        data.add(it)
                    }
                }

                CoroutineScope(Dispatchers.Main).launch {
                    _conversations.emit(data)
                }
            }

    }

    fun a(userId: String) : Flow<List<ConversationDto>>{
        val data = mutableListOf<ConversationDto>()

        val listener =  firestore.collection(COLLECTION_CONVERSATIONS)
            .whereArrayContains(KEY_PARTICIPANTS, userId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("EXC", exception.message.toString())
                    return@addSnapshotListener
                }


                snapshot?.documents?.forEach { document ->
                    val conversation = document.toObject(ConversationDto::class.java)
                    conversation?.let {
                        data.add(it)
                    }
                }
            }


        return flowOf(data)
    }
}