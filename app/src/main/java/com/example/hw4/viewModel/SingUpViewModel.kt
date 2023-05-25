package com.example.hw4.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hw4.DTO.User
import com.example.hw4.model.FeedModelState
import com.example.hw4.repository.AuthRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class SingUpViewModel: ViewModel(){
    private val repository = AuthRepository()

    private val _data = MutableLiveData<User>()
    val data: LiveData<User>
        get() = _data

    private val _state = MutableLiveData<FeedModelState>()
    val state: LiveData<FeedModelState>
        get() = _state

    fun registration(login: String, password: String, name: String){
        viewModelScope.launch {
            try {
                val user = repository.registration(login,password,name)
                _data.value = user
            }catch (e: Exception){
                _state.postValue(FeedModelState(registrationError =  true))
            }
        }
    }
}