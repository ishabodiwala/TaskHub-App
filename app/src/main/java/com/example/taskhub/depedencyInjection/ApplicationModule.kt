package com.example.taskhub.depedencyInjection

import android.content.Context
import com.example.taskhub.utils.ReminderManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideReminderManager(@ApplicationContext context: Context): ReminderManager {
        return ReminderManager(context)
    }
}