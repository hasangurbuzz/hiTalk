package dev.hasangurbuz.hitalk.di

import android.app.Application
import android.content.ContentResolver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.hasangurbuz.hitalk.core.AuthContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {
    @Provides
    @Singleton
    fun provideAuthContext(): AuthContext {
        return AuthContext()
    }

    @Provides
    fun provideContentResolver(application: Application): ContentResolver {
        return application.contentResolver
    }
}