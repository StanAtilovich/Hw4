package com.example.hw4.repository

import com.example.hw4.DTO.User
import com.example.hw4.R
import com.example.hw4.api.ApiService
import com.example.hw4.dao.PostDao
import com.example.hw4.error.ApiException
import com.example.hw4.error.NetworkException
import com.example.hw4.error.UnknownException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val postDao: PostDao,
    private val apiService: ApiService
): AuthInterfaseRepository {

   override suspend fun authUser(login: String, pass: String): User {
        try {
            val response = apiService.updateUser(login, pass)
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




   override suspend fun registrationUser(login: String,password: String,name: String):User{
        try {

            val response = apiService.registration(login,password,name)
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