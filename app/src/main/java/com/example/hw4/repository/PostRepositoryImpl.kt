package com.example.hw4.repository


import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.hw4.DTO.Ad
import com.example.hw4.DTO.FeedItem
import com.example.hw4.DTO.Media
import com.example.hw4.DTO.MediaUpload
import com.example.hw4.DTO.Post
import com.example.hw4.api.ApiService
import com.example.hw4.dao.PostDao
import com.example.hw4.dao.PostRemoveKeyDao
import com.example.hw4.db.AppDb
import com.example.hw4.entity.*
import com.example.hw4.error.ApiException
import com.example.hw4.error.DbError
import com.example.hw4.error.NetworkException
import com.example.hw4.error.UnknownException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.IOException
import javax.inject.Inject
import kotlin.random.Random


class PostRepositoryImpl @Inject constructor(
    private val dao: PostDao,
    private val apiService: ApiService,
    postRemoteKeyDao: PostRemoveKeyDao,
    appDb: AppDb,
) : PostRepository {
    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = { dao.getPagingSource() },
        remoteMediator = PostRemoteMediator(
            apiService = apiService, postDao = dao,
            postRemoveKeyDao = postRemoteKeyDao,
            abbDb = appDb,
        )
    ).flow.map {
        it.map(PostEntity::toDto)
            .insertSeparators { previous, _ ->
                if (previous?.id?.rem(5) == 0L) {
                    Ad(Random.nextLong(), "figma.jpg")
                } else {
                    null
                }
            }
    }


    override suspend fun likedById(id: Long) {
        try {
            dao.likeById(id)
            val response = apiService.likedById(id)
            if (!response.isSuccessful) {
                throw ApiException(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: Exception) {
            throw UnknownException
        }
    }

    override suspend fun unliked(id: Long) {
        try {
            dao.likeById(id)
            val response = apiService.unlikedByIdAsync(id)
            if (!response.isSuccessful) {
                throw ApiException(response.code(), response.message())
            }
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: Exception) {
            throw UnknownException
        }
    }


    override suspend fun getVisible() {
        dao.getVisible()
    }

    override suspend fun readAll() {
        dao.readAll()
    }

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload) {
        try {
            val media = upload(upload)
            val postWthAttachment =
                post.copy(attachment = Attachment(media.id, AttachmentType.IMAGE))
            save(postWthAttachment)
        } catch (e: ApiException) {
            throw e
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: Exception) {
            throw UnknownException
        }
    }

    override suspend fun upload(upload: MediaUpload): Media {
        try {
            val data = MultipartBody.Part.createFormData(
                "file", upload.file.name, upload.file.asRequestBody()
            )
            val response = apiService.upload(data)
            if (!response.isSuccessful) {
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


    override suspend fun save(post: Post) {
        try {
            val response = apiService.save(post)
            if (!response.isSuccessful) {
                throw ApiException(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiException(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: ApiException) {
            throw e
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: Exception) {
            throw UnknownException
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            dao.removeById(id)
            val response = apiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiException(response.code(), response.message())
            }
        } catch (e: ApiException) {
            throw e
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: Exception) {
            throw UnknownException
        }
    }


    override suspend fun getPostById(id: Long) = dao.getPostById(id).toDto()

    override suspend fun getMaxId() = dao.getPostMaxId()?.toDto()?.id ?: throw DbError
}
