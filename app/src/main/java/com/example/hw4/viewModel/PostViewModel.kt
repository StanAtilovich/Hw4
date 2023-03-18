package com.example.hw4.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.hw4.DTO.Post
import com.example.hw4.model.FeedModel
import com.example.hw4.repository.PostRepository
import com.example.hw4.repository.PostRepositoryImpl
import com.example.hw4.util.SingleLiveEvent


private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    published = "",
    likes = 0,
    shareByMe = false,
    shareCount = 0,
    viewByMe = false,
    countView = 0,
    video = null
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _error = SingleLiveEvent<String>()
    val error: LiveData<String>
        get() = _error

    init {
        loadPosts()
    }

    fun refreshPosts() {
        _data.value = FeedModel(refreshing = true)
        repository.getAllAsync(object : PostRepository.PostCallBack<List<Post>> {
            override fun onSuccess(value: List<Post>) {
                _data.postValue(FeedModel(posts = value, empty = value.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
                _error.postValue(e.message)
            }
        })
    }


    fun reload(isInitial: Boolean) {
        _data.value = FeedModel(loading = isInitial, refreshing = !isInitial)
        repository.getAllAsync(object : PostRepository.PostCallBack<List<Post>> {
            override fun onSuccess(value: List<Post>) {
                _data.postValue(FeedModel(posts = value, empty = value.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
                _error.postValue(e.message)// остановился тут надо создать еррор как наверху типа _data и потом дальше.
            }
        })
    }

    fun loadPosts() {
        reload(true)
    }


    fun save() {
        edited.value?.let {
            repository.saveAsync(it, object : PostRepository.SaveRemoveCallback {
                override fun onSuccess() {
                    _postCreated.postValue(Unit)
                }
            })
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



        fun likeById(id: Long, likedByMe: Boolean) {
            repository.likedByIdAsync(id, likedByMe, object : PostRepository.Callback<Post> {
                override fun onSuccess(posts: Post) {
                    _postCreated.postValue(Unit)
                }

                override fun onError(e: Exception) {
                    _data.postValue(FeedModel(error = true))
                }
            })
        }


    fun removeById(id: Long) {
        val old = _data.value?.posts.orEmpty()
        _data.postValue(
            _data.value?.copy(posts = _data.value?.posts.orEmpty()
                .filter { it.id != id }
            )
        )
        repository.removeById(id, object : PostRepository.SaveRemoveCallback {
            override fun onError(e: Exception) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        })
    }


}


