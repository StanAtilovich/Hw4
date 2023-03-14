package com.example.hw4.repository


import com.example.hw4.DTO.Post
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.EMPTY_REQUEST
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val typeToken = object : TypeToken<List<Post>>() {}

    companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAll(): List<Post> {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let { gson.fromJson(it, typeToken.type)
            }
    }


    override fun sharing(id: Long) {
        val request: Request = Request.Builder()
            .post(gson.toJson(id).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts/$id/sharing")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }
  // override fun likedById(id: Long) {
  //     var request: Request = Request.Builder()
  //         .url("${BASE_URL}/api/slow/posts/$id")
  //         .build()

  //     val post: Post = client.newCall(request)
  //         .execute()
  //         .let { it.body?.string() ?: throw RuntimeException("body is null") }
  //         .let {
  //             gson.fromJson(it, typeToken.type)
  //         }

  //     request = if (!post.likedByMe) {
  //         Request.Builder()
  //             .url("${BASE_URL}/api/slow/posts/$id/likes")
  //             .post(EMPTY_REQUEST)
  //             .build()
  //     } else {
  //         Request.Builder()
  //             .url("${BASE_URL}/api/slow/posts/$id/likes")
  //             .delete()
  //             .build()
  //     }

  //     return client.newCall(request)
  //         .execute()
  //         .let { it.body?.string() ?: throw RuntimeException("body is null") }
  //         .let {
  //             gson.fromJson(it, typeToken.type)
  //         }
  // }
  override fun likedById(id: Long):Post {
      val request: Request = Request.Builder()
          .url("${BASE_URL}/api/slow/posts/${id}/likes")
          .post(EMPTY_REQUEST)
          .build()

     return client.newCall(request)
          .execute()
          .let { it.body?.string() ?: throw RuntimeException("body is null") }
          .let { gson.fromJson(it, Post::class.java)
          }
  }
    override fun unlikedById(id: Long):Post {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts/${id}/likes")
            .delete()
            .build()

        client.newCall(request)
            .execute()
        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let { gson.fromJson(it, Post::class.java)
            }
    }

    override fun save(post: Post) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url("${BASE_URL}/api/slow/posts")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/slow/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }
}