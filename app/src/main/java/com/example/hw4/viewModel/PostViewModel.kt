package com.example.hw4.viewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import com.example.hw4.DTO.MediaUpload
import com.example.hw4.DTO.Post
import com.example.hw4.auth.AppAuth
import com.example.hw4.model.FeedModel
import com.example.hw4.model.FeedModelState
import com.example.hw4.model.PhotoModel
import com.example.hw4.repository.PostRepository
import com.example.hw4.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.switchMap
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


private val empty = Post(
    id = 0,
    authorId = 0L,
    content = "",
    author = "",
    likedByMe = false,
    published = "",
    likes = 0,
    shareByMe = false,
    shareCount = 0,
    viewByMe = false,
    countView = 0,
    video = null,
    authorAvatar = "",
    attachment = null,
    hidden = false,
    ownedByMe = false,

    )
private val noPhoto = PhotoModel()
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
    appAuth: AppAuth,
) : ViewModel() {
    private val _dataState = MutableLiveData(FeedModelState())


    val data: Flow<PagingData<Post>> = appAuth
        .data
        .flatMapLatest { authState ->
            repository.data
                .map { posts ->
                    posts.map {
                        it.copy(ownedByMe = authState?.id == it.authorId)
                    }
                }
        }
        .flowOn(Dispatchers.Default)

val newerCount: LiveData<Int> = repository.getNewerCount()
    .catch { e -> e.printStackTrace()}
    .asLiveData(Dispatchers.Default)
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _error = SingleLiveEvent<String>()
    val error: LiveData<String>
        get() = _error

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo


    init {
        loadPosts()
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }


    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            _dataState.value = FeedModelState()
            _dataState.value = FeedModelState(Shadow = true)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            repository.likedById(id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            repository.removeById(id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }


    fun save() {
        edited.value?.let {
            _postCreated.postValue(Unit)
            viewModelScope.launch {
                try {
                    when (_photo.value) {
                        noPhoto -> repository.save(it)
                        else -> _photo.value?.file?.let { file ->
                            repository.saveWithAttachment(it, MediaUpload(file))
                        }
                    }
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
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



    fun changePhoto(uri: Uri?, file: File?) {
        _photo.value = PhotoModel(uri, file)
    }

    fun deletePhoto() {
        _photo.value = noPhoto
    }

    fun newPostView() = viewModelScope.launch {
        try {
            repository.getVisible()
            _dataState.value = FeedModelState(Shadow = true)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun readAll() = viewModelScope.launch {
        try {
            repository.readAll()
            _dataState.value = FeedModelState(Shadow = true)
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }


}