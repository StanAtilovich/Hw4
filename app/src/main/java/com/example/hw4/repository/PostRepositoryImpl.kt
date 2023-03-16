package com.example.hw4.repository


import com.example.hw4.DTO.Post
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.EMPTY_REQUEST
import java.io.IOException
import java.lang.Exception
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

    override fun getAll(callback: PostRepository.PostCallBack){
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/slow/posts")
            .build()

       client.newCall(request)
            .enqueue(object : Callback{
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                     callback.onError(Exception(response.message))
                        return
                    }

                    val data: List<Post>? = response.body?.string()?.let {
                        gson.fromJson(it, typeToken.type)
                    }
                    data ?: kotlin.run {
                        callback.onError(Exception("body is null"))
                        return
                    }
                    callback.onSuccess(data)
                }

            })
    }


    override fun sharing(id: Long) {
        val request: Request = Request.Builder()
            .post(EMPTY_REQUEST)
            .url("${BASE_URL}/api/posts/$id/sharing")
            .build()

       // client.newCall(request)
       //     .execute()
       //     .close()
        return client.newCall(request)
            .execute()
            .let { it.body?.string() ?: throw RuntimeException("body is null") }
            .let { gson.fromJson(it, Post::class.java)
            }
    }

  override fun likedById(id: Long):Post {
      val request: Request = Request.Builder()
          .url("${BASE_URL}/api/posts/${id}/likes")
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
            .url("${BASE_URL}/api/posts/${id}/likes")
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
            .url("${BASE_URL}/api/posts")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }
}