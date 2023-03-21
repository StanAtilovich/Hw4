package com.example.hw4.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hw4.DTO.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val content: String,
    val published:String,
    val likedByMe: Boolean,
    val likCount: Int =0,
    val shareByMe: Boolean = false,
    val shareCount: Int,
    val viewByMe: Boolean = false,
    val countView: Int,
    val video: String?,
    val authorAvatar: String
) {
    fun toDto()= Post(id, author, content, published, likedByMe,likCount,shareByMe,shareCount,viewByMe,countView,video , authorAvatar )

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(dto.id, dto.author, dto.content, dto.published, dto.likedByMe, dto.likes,dto.shareByMe,dto.shareCount,dto.viewByMe,dto.countView,dto.video,dto.authorAvatar)


    }
}
