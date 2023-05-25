package com.example.hw4.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import auth.AppAuth
import com.example.hw4.model.AuthModel
import kotlinx.coroutines.Dispatchers
import okhttp3.Dispatcher

class AuthViewModel: ViewModel() {
    val data: LiveData<AuthModel?> = AppAuth.getInstance()
        .data
        .asLiveData(Dispatchers.Default)
    val authorized: Boolean
        get() = data.value != null
}