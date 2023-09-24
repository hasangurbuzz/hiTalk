package dev.hasangurbuz.hitalk.di

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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideUserApi(firestore: FirebaseFirestore): UserApi {
        return UserApiImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideMessageApi(firestore: FirebaseFirestore): MessageApi {
        return MessageApiImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideImageApi(firebaseStorage: FirebaseStorage): ImageApi {
        return ImageApiImpl(firebaseStorage)
    }

    @Provides
    @Singleton
    fun provideConversationApi(firestore: FirebaseFirestore): ConversationApi {
        return ConversationApiImpl(firestore)
    }

}