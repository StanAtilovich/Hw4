package com.example.hw4.repository

import com.example.hw4.DTO.User
import com.example.hw4.R
import com.example.hw4.api.PostsApi
import com.example.hw4.error.ApiException
import com.example.hw4.error.NetworkException
import com.example.hw4.error.UnknownException
import java.io.IOException
import kotlin.math.log


class AuthRepository {//In

    suspend fun authUser(login: String, password: String): User {
        try {
            val response = PostsApi.service.updateUser(login, password)
            if (!response.isSuccessful) {
                println(R.string.youDidIt)
                throw ApiException(response.code(), response.message())
            }
            return response.body() ?: throw ApiException(response.code(), response.message())
        } catch (e: ApiException) {
            throw e
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: Exception) {
            throw UnknownException
        }
    }

    suspend fun registration(login: String,password: String,name: String):User{
        try {

            val response = PostsApi.service.registration(login,password,name)
            if (!response.isSuccessful){
                println(R.string.register)
                throw ApiException(response.code(), response.message())
            }
            return response.body() ?: throw ApiException(response.code(), response.message())
        } catch (e: ApiException) {
            throw e
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: Exception) {
            throw UnknownException
        }
    }
}