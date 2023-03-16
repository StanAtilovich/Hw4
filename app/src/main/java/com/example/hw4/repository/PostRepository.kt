package com.example.hw4.repository


import com.example.hw4.DTO.Post


interface PostRepository {
    fun getAll(callBack: PostCallBack)
    fun likedById(id: Long):Post
    fun unlikedById(id: Long):Post
    fun sharing(id: Long)
    fun removeById(id: Long)
    fun save(post: Post)

    interface PostCallBack {
        fun onSuccess(post: List<Post>)
        fun onError(e: java.lang.Exception)
    }
}