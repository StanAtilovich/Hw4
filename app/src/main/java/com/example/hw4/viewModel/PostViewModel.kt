package com.example.hw4.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

import com.example.hw4.DTO.Post
import com.example.hw4.repository.PostRepository
import com.example.hw4.repository.PostRepositoryFileImpl
import com.example.hw4.repository.PostRepositorySharedPrefsImpl


private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    published = "",
    likCount = 0,
    shareByMe = false,
    shareCount = 0,
    viewByMe = false,
    countView = 0,
    video = null
)

class PostViewModel (application: Application):AndroidViewModel(application){
    private val repository: PostRepository = PostRepositoryFileImpl(application)
    val data = repository.get()
   // : ViewModel() {
   // private val repository: PostRepository = PostRepositoryInMemoryImpl()
   // val data = repository.get()
    val edited = MutableLiveData(empty)

    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun clear() {
        edited.value = empty
    }

    fun likeById(id: Long) = repository.likedById(id)
    fun sharing(id: Long) = repository.sharing(id)

    fun removeById(id: Long) = repository.removeById(id)

}




