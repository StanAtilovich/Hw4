package com.example.hw4.api

import com.example.hw4.BuildConfig
import com.example.hw4.DTO.Post
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.*

private const val BASE_URL = "${BuildConfig.BASE_URL}/api/slow/"

interface PostsApiService {
    @GET("posts")
    fun getAll(): Call<List<Post>>

    @POST("posts/{postId}/likes")
    fun likedByIdAsync(@Path("postId") id: Long): Call<Post>

    @DELETE("posts/{postId}/likes")
    fun unlikedByIdAsync(@Path("postId") id: Long): Call<Post>

    @POST("posts")
    fun saveAsync(@Body post: Post): Call<Post>

    @DELETE("posts/{postId}")
    fun removeById(@Path("postId") id: Long): Call<Unit>
}

private val logging = HttpLoggingInterceptor().apply {
    if (BuildConfig.DEBUG) {
        level = HttpLoggingInterceptor.Level.BODY
    }
}
private val client = OkHttpClient.Builder()
    .addInterceptor(logging)
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .client(client)
    .baseUrl(BASE_URL)
    .build()

object PostsApi {
    val retrofitService by lazy {
        retrofit.create<PostsApiService>()
    }
}