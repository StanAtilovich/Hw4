package com.example.hw4.repository

import com.example.hw4.DTO.User

interface AuthInterfaseRepository {
    suspend fun authUser(login: String, pass: String): User
    suspend fun registrationUser(login: String, password: String, name: String): User
}