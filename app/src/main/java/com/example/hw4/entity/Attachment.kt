package com.example.hw4.entity


data class Attachment(
    val url: String,
    val type: AttachmentType,

    )
enum class AttachmentType{
    IMAGE
}