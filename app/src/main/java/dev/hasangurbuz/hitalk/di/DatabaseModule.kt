package dev.hasangurbuz.hitalk.di

import android.content.ContentResolver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.hasangurbuz.hitalk.data.local.ContactDataSource
import dev.hasangurbuz.hitalk.data.local.ConversationDataSource
import dev.hasangurbuz.hitalk.data.local.entity.ConversationEntity
import dev.hasangurbuz.hitalk.data.local.impl.ContactDataSourceImpl
import dev.hasangurbuz.hitalk.data.local.impl.ConversationDataSourceImpl
import io.realm.kotlin.RealmConfiguration
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideRealm(): RealmConfiguration {
        return RealmConfiguration.Builder(
            schema = setOf(
                ConversationEntity::class
            )
        ).schemaVersion(1)
            .build()
    }

    @Singleton
    @Provides
    fun provideConversationDataSource(realmConfig: RealmConfiguration): ConversationDataSource {
        return ConversationDataSourceImpl(realmConfig)
    }

    @Singleton
    @Provides
    fun provideContactDataSource(contentResolver: ContentResolver): ContactDataSource {
        return ContactDataSourceImpl(contentResolver)
    }


}