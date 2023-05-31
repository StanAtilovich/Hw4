package com.example.hw4.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.hw4.auth.AppAuth
import com.example.hw4.model.AuthModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers

import javax.inject.Inject
@HiltViewModel
class AuthViewModel @Inject constructor(
    appAuth: AppAuth,
): ViewModel() {
    val data: LiveData<AuthModel?> = appAuth
        .data
        .asLiveData(Dispatchers.Default)
    val authorized: Boolean
        get() = data.value != null
}