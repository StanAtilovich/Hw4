package com.example.hw4.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.hw4.DTO.Post


@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorId : Long,
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
    val hidden: Boolean = false,
    @Embedded
    var attachment: AttachmentEmbeddable?,

    ) {
    fun toDto() = Post(
        id,
        authorId,
        author,
        content,
        published,
        likedByMe,
        likCount,
        shareByMe,
        shareCount,
        viewByMe,
        countView,
        video,
        authorAvatar,
        hidden,
        attachment?.toDto()
    )

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.author,
                dto.authorId,
                dto.content,
                dto.published,
                dto.likedByMe,
                dto.likes,
                dto.shareByMe,
                dto.shareCount,
                dto.viewByMe,
                dto.countView,
                dto.video,
                dto.authorAvatar,
                dto.hidden,
                AttachmentEmbeddable.fromDto(dto.attachment)
            )


    }
}
    class AttachmentEmbeddable (
        var url: String,
        var type: AttachmentType
            ){
        fun toDto() = Attachment(url, type)
        companion object{
            fun fromDto(dto: Attachment?) = dto?.let {
                AttachmentEmbeddable(it.url, it.type)
            }
        }
    }




fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)
