package com.example.hw4.repository

import com.example.hw4.DTO.Post
import com.example.hw4.api.PostsApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class PostRepositoryImpl : PostRepository {
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
        if (likedByMe) {
            PostsApi.retrofitService.unlikedByIdAsync(id)
                .enqueue(object : Callback<Post> {
                    override fun onResponse(call: Call<Post>, response: Response<Post>) {
                        if (!response.isSuccessful) {
                            callback.onError(RuntimeException(response.message()))
                            return
                        }
                        callback.onSuccess(
                            response.body() ?: throw RuntimeException("body is null")
                        )
                    }
                    override fun onFailure(call: Call<Post>, t: Throwable) {
                        throw RuntimeException(t)
                    }
                })
        } else {
            PostsApi.retrofitService.likedByIdAsync(id)
                .enqueue(object : Callback<Post> {
                    override fun onResponse(call: Call<Post>, response: Response<Post>) {
                        if (!response.isSuccessful) {
                            callback.onError(RuntimeException(response.message()))
                            return
                        }
                        callback.onSuccess(
                            response.body() ?: throw RuntimeException("body is null")
                        )
                    }
                    override fun onFailure(call: Call<Post>, t: Throwable) {
                        throw RuntimeException(t)
                    }
                })
        }
    }
    override fun saveAsync(post: Post, callback: PostRepository.SaveRemoveCallback) {
        PostsApi.retrofitService.saveAsync(post)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    try {
                        callback.onSuccess()
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
                override fun onFailure(call: Call<Post>, t: Throwable) {
                    callback.onError(Exception(t))
                }
            })
    }
    override fun removeById(id: Long, callback: PostRepository.SaveRemoveCallback) {
        PostsApi.retrofitService.removeById(id)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    try {
                        callback.onSuccess()
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }
                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    callback.onError(Exception(t))
                }
            })
    }
}
