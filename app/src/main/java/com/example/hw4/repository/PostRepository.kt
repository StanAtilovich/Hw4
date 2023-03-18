package com.example.hw4.repository


import com.example.hw4.DTO.Post


interface PostRepository {
    fun likedByIdAsync(id: Long,likedByMe:Boolean,callback: Callback<Post>)
  //  fun unlikedByIdAsync(id: Long, callback: PostCallBack<Post>)
    fun saveAsync(post: Post, callback: SaveRemoveCallback)
    fun removeById(id: Long,callback: SaveRemoveCallback)
    fun getAllAsync(callback: PostCallBack<List<Post>>)

    interface PostCallBack<T> {
        fun onSuccess(value: T){}
        fun onError(e: java.lang.Exception){}
    }

    interface SaveRemoveCallback {
        fun onSuccess() {}
        fun onError(e: Exception) {}
    }

    interface Callback<T> {
        fun onSuccess(posts: T) {}
        fun onError(e: Exception) {}
    }


}