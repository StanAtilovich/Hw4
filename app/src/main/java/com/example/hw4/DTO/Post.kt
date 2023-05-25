package com.example.hw4.DTO


import com.example.hw4.entity.Attachment
import java.io.File


data class Post(
    val id: Long,
    val authorId: Long,
    val author: String,
    val content: String,
    val published: String,
    var likedByMe: Boolean = false,
    val likes: Int,
    val shareByMe: Boolean = false,
    val shareCount: Int,
    val viewByMe: Boolean = false,
    val countView: Int,
    val video: String?,
    val authorAvatar: String,
    val hidden: Boolean,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
)
data class MediaUpload(val file: File)
data class Media(val id: String)






