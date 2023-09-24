package dev.hasangurbuz.hitalk.di

import android.content.ContentResolver
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.hasangurbuz.hitalk.data.local.ContactDataSource
import dev.hasangurbuz.hitalk.data.local.impl.ContactDataSourceImpl
import dev.hasangurbuz.hitalk.data.remote.ConversationApi
import dev.hasangurbuz.hitalk.data.remote.ImageApi
import dev.hasangurbuz.hitalk.data.remote.MessageApi
import dev.hasangurbuz.hitalk.data.remote.UserApi
import dev.hasangurbuz.hitalk.data.remote.impl.ConversationApiImpl
import dev.hasangurbuz.hitalk.data.remote.impl.ImageApiImpl
import dev.hasangurbuz.hitalk.data.remote.impl.MessageApiImpl
import dev.hasangurbuz.hitalk.data.remote.impl.UserApiImpl
import dev.hasangurbuz.hitalk.data.repository.ContactRepositoryImpl
import dev.hasangurbuz.hitalk.data.repository.ConversationRepositoryImpl
import dev.hasangurbuz.hitalk.data.repository.MessageRepositoryImpl
import dev.hasangurbuz.hitalk.data.repository.UserRepositoryImpl
import dev.hasangurbuz.hitalk.domain.repository.ContactRepository
import dev.hasangurbuz.hitalk.domain.repository.ConversationRepository
import dev.hasangurbuz.hitalk.domain.repository.MessageRepository
import dev.hasangurbuz.hitalk.domain.repository.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideUserRepository(userApi: UserApi, imageApi: ImageApi): UserRepository {
        return UserRepositoryImpl(userApi, imageApi)
    }

    @Provides
    @Singleton
    fun conversationRepository(conversationApi: ConversationApi): ConversationRepository {
        return ConversationRepositoryImpl(conversationApi)
    }

    @Provides
    @Singleton
    fun provideMessageRepository(messageApi: MessageApi): MessageRepository {
        return MessageRepositoryImpl(messageApi)
    }

    @Provides
    @Singleton
    fun provideContactRepository(contactDataSource: ContactDataSource): ContactRepository {
        return ContactRepositoryImpl(contactDataSource)
    }



}