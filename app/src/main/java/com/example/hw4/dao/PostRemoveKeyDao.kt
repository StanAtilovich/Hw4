package com.example.hw4.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hw4.entity.PostRemoveKeyEntity

@Dao
interface PostRemoveKeyDao {
    @Query("SELECT max(`key`) FROM PostRemoveKeyEntity")
    suspend fun max(): Long?

    @Query("SELECT min(`key`) FROM PostRemoveKeyEntity")
    suspend fun min(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(postRemoveKeyEntity: PostRemoveKeyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(postRemoveKeyEntity: List<PostRemoveKeyEntity>)

    @Query("DELETE FROM PostEntity")
    suspend fun clear()
}