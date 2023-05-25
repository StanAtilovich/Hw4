package com.example.hw4.repository



import com.example.hw4.DTO.Media
import com.example.hw4.DTO.MediaUpload
import com.example.hw4.DTO.Post
import kotlinx.coroutines.flow.Flow



interface PostRepository {
   val data : Flow<List<Post>>
   suspend fun getAll()
   fun getNewerCount(id: Long): Flow<Int>
   suspend fun save(post: Post)
   suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
   suspend fun removeById(id: Long)
   suspend fun likedById(id: Long)
   suspend fun upload(upload: MediaUpload): Media
   suspend fun unliked(id: Long)



   suspend fun getVisible()
   suspend fun readAll()



}