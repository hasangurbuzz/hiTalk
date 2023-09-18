package dev.hasangurbuz.hitalk.data.repository

import android.util.Log
import dev.hasangurbuz.hitalk.data.model.Conversation
import dev.hasangurbuz.hitalk.data.remote.ConversationApi
import dev.hasangurbuz.hitalk.data.remote.model.Response
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversationRepository
@Inject constructor(private val conversationApi: ConversationApi) {


    suspend fun loadFoundConversations(userId: String): Flow<List<Conversation>> = flow {
        val result = conversationApi.findByUserId(userId)
        when (result) {
            is Response.Failed -> {
                Log.e("FOUND", "no data")
                emit(emptyList<Conversation>())
            }

            is Response.Success -> {
                Log.e("FOUND", "${result.data.size}")
                val conversations = result.data.map { conversationDto ->
                    Conversation(
                        id = conversationDto.id!!,
                        lastMessageId = conversationDto.lastMessageId!!,
                        participants = conversationDto.participants!!
                    )
                }
                emit(conversations)
            }
        }
    }

//    fun fetchConversations(userId: String): Flow<List<Conversation>> =
//        loadFoundConversations(userId).combine(currentConversations(userId)) { loaded, incoming ->
//            loaded + incoming
//        }

    fun currentConversations(userId: String) = listenCurrentConversations(userId).map {
        it.map { conversationDto ->
            Conversation(
                id = conversationDto.id!!,
                lastMessageId = conversationDto.lastMessageId!!,
                participants = conversationDto.participants!!
            )
        }

    }

    private fun listenCurrentConversations(userId: String) = conversationApi.listenLatest(userId)

//    suspend fun fetchConversations(userId: String): Resource<List<Conversation>> {
//        val result = conversationApi.findByUserId(userId)
//        return when (result) {
//            is Response.Failed -> Resource.Failed
//            is Response.Success -> {
//                Resource.Success(
//                    result.data.map { conversationDto ->
//                        Conversation(
//                            id = conversationDto.id,
//                            participants = conversationDto.participants,
//                            lastMessageId = conversationDto.lastMessageId
//                        )
//                    }
//                )
//            }
//        }
//    }


//    suspend fun fetchConversations(userId: String): Flow<List<ConversationItem>> {
//        val result = conversationApi.findByUserId(userId)
//        val foundConversations: List<Conversation>
//
//        if (result is Response.Failed){
//            return flowOf(emptyList())
//        }
//
//        val conversationDtos = (result as Response.Success).data
//
//        for (dto in conversationDtos){
//            dto.
//        }
//
//
//        when (result) {
//            is Response.Failed -> foundConversations = mutableListOf()
//            is Response.Success -> {
//                foundConversations = result.data.map { dto ->
//                    val userResults = userRepository.findById(dto.participantIdList)
//                    var conversation: Conversation? = null
//                    if (userResults is Resource.Success) {
//                        val users = userResults.data
//                        val messageResult = messageRepository.findById(dto.lastMessageId)
//                        if (messageResult is Resource.Success) {
//                            val lastMessage = messageResult.data
//                            conversation = Conversation(
//                                id = dto.id,
//                                lastMessage = lastMessage,
//                                participants = users
//                            )
//                        }
//                    }
//                    conversation!!
//                }
//            }
//        }
//        return flowOf(foundConversations)
//    }
//
//    suspend fun findById(conversationId: String): Resource<Conversation> {
//        val result = conversationApi.findById(conversationId)
//
////        when (result) {
////            is Response.Failed -> return Resource.Failed
////            is Response.Success -> {
////                return Conversation(
////
////                )
////            }
////        }
//        return Resource.Failed
//    }

}