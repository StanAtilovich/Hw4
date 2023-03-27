package com.example.hw4.repository


import com.example.hw4.DTO.Post
import com.example.hw4.api.PostsApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {
//  private val client = OkHttpClient.Builder()
        //      .connectTimeout(30, TimeUnit.SECONDS)
//      .build()
//  private val gson = Gson()
        //  private val typeToken = object : TypeToken<List<Post>>() {}

        //  companion object {
//      private const val BASE_URL = "http://10.0.2.2:9999"
            //      private val jsonType = "application/json".toMediaType()
            //  }

    override fun getAllAsync(callback: PostRepository.PostCallBack<List<Post>>) {
        PostsApi.retrofitService.getAll()
            .enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    try {
                        if (!response.isSuccessful) {
                            callback.onError(java.lang.RuntimeException(response.message()))
                            return
                        }
                        callback.onSuccess(
                            response.body() ?: throw RuntimeException("body is null")
                        )
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                    callback.onError(Exception(t))
                }
            }
            )
    }

    override fun likedByIdAsync(
        id: Long,
        likedByMe: Boolean,
        callback: PostRepository.Callback<Post>
    ) {
        TODO("Not yet implemented")
    }

//   override fun likedByIdAsync(
//       id: Long,
//       likedByMe: Boolean,
//       callback: PostRepository.Callback<Post>
//   ) {
//       val isLiked: Boolean = !likedByMe
//       val likesUrl = "${BASE_URL}/api/posts/${id}/likes"
//       val request: Request = if (isLiked) {
//           Request.Builder()
//               .post(gson.toJson(id).toRequestBody(jsonType))
//               .url(likesUrl)
//               .build()
//       } else {
//           Request.Builder()
//               .delete(gson.toJson(id).toRequestBody(jsonType))
//               .url(likesUrl)
//               .build()
//       }
//       client.newCall(request)
//           .enqueue(object : Callback<List<Post>> {


//            //   override fun onResponse(call: Call, response: Response) {
//            //       callback.onSuccess(gson.fromJson(response.body?.string(), Post::class.java))
//            //   }
///
//            //   override fun onFailure(call: Call, e: IOException) {
//            //       callback.onError(e)
//            //   }
//           })
//   }


    override fun saveAsync(post: Post, callback: PostRepository.SaveRemoveCallback) {
        PostsApi.retrofitService.saveAsync(post)
            .execute()
    } //, callback: PostRepository.SaveRemoveCallback) {

    override fun removeById(id: Long, callback: PostRepository.SaveRemoveCallback) {
        TODO("Not yet implemented")
    }
    //     val request: Request = Request.Builder()
  //         .post(gson.toJson(post).toRequestBody(jsonType))
  //         .url("${BASE_URL}/api/posts")
  //         .build()

  //     client.newCall(request)
  //         .enqueue(object : Callback {
  //             override fun onFailure(call: Call, e: IOException) {
  //                 callback.onError(e)
  //             }

  //             override fun onResponse(call: Call, response: Response) {
  //                 try {
  //                     callback.onSuccess()
  //                 } catch (e: Exception) {
  //                     callback.onError(e)
  //                 }
  //             }
  //         })
  // }
}
//   override fun removeById(id: Long, callback: PostRepository.SaveRemoveCallback) {
//       val request: Request = Request.Builder()
//           .delete()
//           .url("${BASE_URL}/api/posts/$id")
//           .build()

//       client.newCall(request)
//           .enqueue(object : Callback {
//               override fun onFailure(call: Call, e: IOException) {
//                   callback.onError(e)
//               }

//               override fun onResponse(call: Call, response: Response) {
//                   try {
//                       callback.onSuccess()
//                   } catch (e: Exception) {
//                       callback.onError(e)
//                   }
//               }
//           })
//   }
//