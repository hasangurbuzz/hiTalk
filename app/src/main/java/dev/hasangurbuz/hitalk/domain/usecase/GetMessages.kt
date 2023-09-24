package dev.hasangurbuz.hitalk.domain.usecase

import dev.hasangurbuz.hitalk.domain.model.Message
import dev.hasangurbuz.hitalk.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessages @Inject constructor(
    private val messageRepo: MessageRepository
) {

    operator fun invoke(conversationId: String): Flow<List<Message>> {
        return messageRepo.listen(conversationId)
    }
}