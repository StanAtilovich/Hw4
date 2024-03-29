package com.example.hw4.db

import android.content.Context
import androidx.room.Room
import com.example.hw4.dao.PostDao
import com.example.hw4.dao.PostRemoveKeyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class DbModule {

    @Singleton
    @Provides
    fun provideDb(
        @ApplicationContext
        context: Context
    ): AppDb = Room.databaseBuilder(context, AppDb::class.java, "app7.db")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun providePostDao(
        appDb: AppDb
    ): PostDao = appDb.postDao()
    @Provides
    fun providePostRemoteKeyDao(db: AppDb): PostRemoveKeyDao = db.postRemoteKeyDao()
}



