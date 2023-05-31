package com.example.hw4.api


import com.example.hw4.DTO.Media
import com.example.hw4.DTO.Post
import com.example.hw4.DTO.PushToken
import com.example.hw4.DTO.User
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


interface ApiService {

    @GET("posts/latest")
    suspend fun getLatest(@Query("count") count: Int): Response<List<Post>>

    @GET("posts/{postId}/before")
    suspend fun getBefore(
        @Path("postId") id: Long, @Query("count") count: Int
    ): Response<List<Post>>

    @GET("posts/{postId}/after")
    suspend fun getAfter(@Path("postId") id: Long, @Query("count") count: Int
    ): Response<List<Post>>


    @POST("posts/{postId}/likes")
    suspend fun likedById(@Path("postId") id: Long): Response<Post>

    @DELETE("posts/{postId}/likes")
    suspend fun unlikedByIdAsync(@Path("postId") id: Long): Response<Post>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>

    @DELETE("posts/{postId}")
    suspend fun removeById(@Path("postId") id: Long): Response<Unit>

    @Multipart
    @POST("media")
    suspend fun upload(@Part file: MultipartBody.Part): Response<Media>

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun updateUser(
        @Field("login") login: String,
        @Field("pass") pass: String
    ): Response<User>

    @FormUrlEncoded
    @POST("users/registration")
    suspend fun registration(
        @Field("login") login: String,
        @Field("pass") pass: String,
        @Field("name") name: String
    ): Response<User>

    @POST("users/push-tokens")
    suspend fun saveToken(@Body token: PushToken): Response<Unit>

}