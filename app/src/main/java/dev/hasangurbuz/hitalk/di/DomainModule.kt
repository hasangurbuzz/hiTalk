package dev.hasangurbuz.hitalk.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.hasangurbuz.hitalk.domain.repository.ContactRepository
import dev.hasangurbuz.hitalk.domain.repository.ConversationRepository
import dev.hasangurbuz.hitalk.domain.repository.MessageRepository
import dev.hasangurbuz.hitalk.domain.repository.UserRepository
import dev.hasangurbuz.hitalk.domain.usecase.GetContactConversations
import dev.hasangurbuz.hitalk.domain.usecase.GetConversationItems
import dev.hasangurbuz.hitalk.domain.usecase.GetMessages
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    @Singleton
    fun getConversationItemUseCase(
        conversationRepo: ConversationRepository,
        userRepo: UserRepository,
        messageRepo: MessageRepository,
        contactRepo: ContactRepository
    ): GetConversationItems {
        return GetConversationItems(
            conversationRepo, userRepo, messageRepo, contactRepo
        )
    }

    @Provides
    @Singleton
    fun provideGetContactConversationUseCase(conversationRepo: ConversationRepository): GetContactConversations {
        return GetContactConversations(conversationRepo)
    }

    @Provides
    @Singleton
    fun provideGetMessages(messageRepo: MessageRepository): GetMessages {
        return GetMessages(messageRepo)
    }
}