package com.example.hw4.dao



import androidx.room.*
import com.example.hw4.entity.AttachmentType
import com.example.hw4.entity.PostEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity WHERE hidden = 0 ORDER BY id DESC") //("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT * FROM PostEntity WHERE hidden = 0 ORDER BY id DESC") //"SELECT * FROM PostEntity WHERE hidden = 0 ORDER BY id DESC"
    fun getVisible(): Flow<List<PostEntity>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend  fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend  fun insert(posts: List<PostEntity>)


    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("""
        UPDATE PostEntity SET
        likCount = likCount + CASE WHEN likedByMe THEN -1 ELSE 1 END,
        likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
        WHERE id = :id
    """)
    suspend fun likeById(id: Long)


    @Query("UPDATE PostEntity SET hidden = 0")
    suspend fun readAll()

    @Query("SELECT COUNT(*) FROM PostEntity WHERE hidden = 0")
    fun getUnreadCount(): Flow<Int>



}

class Converters {
    @TypeConverter
    fun toAttachmentType(value: String) = enumValueOf<AttachmentType>(value)
    @TypeConverter
    fun fromAttachmentType(value: AttachmentType) = value.name
}