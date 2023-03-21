package com.example.hw4.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
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
    val authorAvatar: String,
    @TypeConverters(AttachmentConverter::class)
    val attachment: Attachment?


) {
    fun toDto()= Post(id, author, content, published, likedByMe,likCount,shareByMe,shareCount,viewByMe,countView,video , authorAvatar ,attachment)

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(dto.id, dto.author, dto.content, dto.published, dto.likedByMe, dto.likes,dto.shareByMe,dto.shareCount,dto.viewByMe,dto.countView,dto.video,dto.authorAvatar, dto.attachment)


    }

    class AttachmentConverter {
        @TypeConverter
        fun fromAttachment(attachment: Attachment?): String? {
            if (attachment != null) {
                return attachment.url
            } else return ""

        }

        @TypeConverter
        fun toAttachment(value: String): Attachment{
            return Attachment(value, "", "")
        }}
}


