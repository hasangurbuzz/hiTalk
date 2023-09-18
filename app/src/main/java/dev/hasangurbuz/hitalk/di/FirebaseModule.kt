package dev.hasangurbuz.hitalk.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.hasangurbuz.hitalk.data.remote.ConversationApi
import dev.hasangurbuz.hitalk.data.remote.ImageApi
import dev.hasangurbuz.hitalk.data.remote.MessageApi
import dev.hasangurbuz.hitalk.data.remote.UserApi
import dev.hasangurbuz.hitalk.data.remote.impl.ConversationApiImpl
import dev.hasangurbuz.hitalk.data.remote.impl.ImageApiImpl
import dev.hasangurbuz.hitalk.data.remote.impl.MessageApiImpl
import dev.hasangurbuz.hitalk.data.remote.impl.UserApiImpl
import dev.hasangurbuz.hitalk.data.repository.AuthRepository
import dev.hasangurbuz.hitalk.data.repository.ConversationRepository
import dev.hasangurbuz.hitalk.data.repository.MessageRepository
import dev.hasangurbuz.hitalk.data.repository.UserRepository
import dev.hasangurbuz.hitalk.data.usecase.GetConversationItemUseCase
import dev.hasangurbuz.hitalk.remote.firebase.ConversationService
import dev.hasangurbuz.hitalk.remote.firebase.ImageService
import dev.hasangurbuz.hitalk.remote.firebase.MessageService
import dev.hasangurbuz.hitalk.remote.firebase.UserService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Singleton
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Singleton
    @Provides
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Singleton
    @Provides
    fun provideUserService(): UserService {
        return UserService(provideFirebaseFirestore())
    }

    @Singleton
    @Provides
    fun provideConversationService(): ConversationService {
        return ConversationService(provideFirebaseFirestore())
    }

    @Singleton
    @Provides
    fun provideImageService(): ImageService {
        return ImageService(provideFirebaseStorage())
    }


    @Provides
    @Singleton
    fun provideMessageService(): MessageService {
        return MessageService(provideFirebaseFirestore())
    }


    @Provides
    @Singleton
    fun provideUserApi(): UserApi {
        return UserApiImpl(provideFirebaseFirestore())
    }

    @Provides
    @Singleton
    fun provideMessageApi(): MessageApi {
        return MessageApiImpl(provideFirebaseFirestore())
    }

    @Provides
    @Singleton
    fun provideImageApi(): ImageApi {
        return ImageApiImpl(provideFirebaseStorage())
    }

    @Provides
    @Singleton
    fun provideConversationApi(): ConversationApi {
        return ConversationApiImpl(provideFirebaseFirestore())
    }

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository {
        return UserRepository(provideUserApi(), provideImageApi())
    }

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        return AuthRepository()
    }

    @Provides
    @Singleton
    fun conversationRepository(): ConversationRepository {
        return ConversationRepository(provideConversationApi())
    }

    @Provides
    @Singleton
    fun provideMessageRepository(): MessageRepository {
        return MessageRepository(provideMessageApi())
    }

    @Provides
    @Singleton
    fun getConversationItemUseCase(): GetConversationItemUseCase {
        return GetConversationItemUseCase(
            conversationRepository(), provideUserRepository(),
            provideMessageRepository()
        )
    }

}