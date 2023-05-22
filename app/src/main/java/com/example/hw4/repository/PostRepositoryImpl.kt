package com.example.hw4.repository


import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.hw4.DTO.Media
import com.example.hw4.DTO.MediaUpload
import com.example.hw4.DTO.Post
import com.example.hw4.api.ApiService
import com.example.hw4.dao.PostDao
import com.example.hw4.entity.*
import com.example.hw4.error.ApiException
import com.example.hw4.error.AppError
import com.example.hw4.error.DbError
import com.example.hw4.error.NetworkException
import com.example.hw4.error.UnknownException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.IOException
import javax.inject.Inject


class PostRepositoryImpl @Inject constructor(
    private val dao: PostDao,
private val apiService: ApiService,
    ) : PostRepository {
    override val data = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = {
            PostPagingSource(
                apiService
            )
        }
    ).flow


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

    override fun getNewerCount(): Flow<Int> = flow {
        while (true) {
            delay(10000L)
            val response = apiService.getNewer(getMaxId())
            if (!response.isSuccessful) {
                throw ApiException(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiException(response.code(), response.message())
            dao.insert(body.toEntity().map {
                it.copy(hidden = true)
            })
            emit(body.size)
        }
    }
        .catch { e -> throw  AppError.from(e) }
        .flowOn(Dispatchers.Default)



    override suspend fun getVisible() {
        dao.getVisible()
    }

    override suspend fun readAll() {
        dao.readAll()
    }

    override suspend fun saveWithAttachment(post: Post, upload: MediaUpload) {
        try {
            val  media = upload(upload)
            val postWthAttachment = post.copy(attachment = Attachment(media.id,AttachmentType.IMAGE ))
            save(postWthAttachment)
        } catch (e: ApiException) {
            throw  e
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
            throw  e
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
            throw  e
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

    override suspend fun getAll() {
        try {
            val response = apiService.getAll()
            if (!response.isSuccessful) {
                throw ApiException(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiException(response.code(), response.message())
            dao.insert(body.toEntity())
        } catch (e: ApiException) {
            throw  e
        } catch (e: IOException) {
            throw NetworkException
        } catch (e: Exception) {
            throw UnknownException
        }
    }




    override suspend fun getPostById(id: Long) = dao.getPostById(id).toDto()

    override suspend fun getMaxId() = dao.getPostMaxId()?.toDto()?.id ?: throw DbError
}
