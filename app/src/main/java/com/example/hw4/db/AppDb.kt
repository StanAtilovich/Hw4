package com.example.hw4.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.hw4.dao.Converters
import com.example.hw4.dao.PostDao
import com.example.hw4.dao.PostRemoveKeyDao
import com.example.hw4.entity.PostEntity
import com.example.hw4.entity.PostRemoveKeyEntity


@TypeConverters(Converters::class)
@Database(entities = [PostEntity::class, PostRemoveKeyEntity::class], version = 1,exportSchema = false)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao():PostRemoveKeyDao
}
