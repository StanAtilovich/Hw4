package com.example.hw4.repository


import androidx.paging.PagingData
import com.example.hw4.DTO.Media
import com.example.hw4.DTO.MediaUpload
import com.example.hw4.DTO.Post
import kotlinx.coroutines.flow.Flow


interface PostRepository {
    val data: Flow<PagingData<Post>>
    suspend fun getAll()
    suspend fun save(post: Post)
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    suspend fun removeById(id: Long)
    suspend fun likedById(id: Long)
    suspend fun upload(upload: MediaUpload): Media
    suspend fun unliked(id: Long)


    suspend fun getVisible()
    suspend fun readAll()
    suspend fun getPostById(id: Long): Post
    suspend fun getMaxId(): Long
    abstract fun getNewerCount(): Flow<Int>

}