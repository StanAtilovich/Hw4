package com.example.hw4.model

import com.example.hw4.DTO.Post

data class FeedModel(
    val posts: List<Post> = emptyList(),
    val empty: Boolean = false
)
data class FeedModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val refreshing: Boolean = false,
    val Shadow: Boolean = false,
    val loginError: Boolean = false,
    val registrationError: Boolean = false,
)