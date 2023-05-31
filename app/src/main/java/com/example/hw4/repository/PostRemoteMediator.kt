package com.example.hw4.repository


import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.hw4.api.ApiService
import com.example.hw4.dao.PostDao
import com.example.hw4.dao.PostRemoveKeyDao
import com.example.hw4.db.AppDb
import com.example.hw4.entity.PostEntity
import com.example.hw4.entity.PostRemoveKeyEntity
import com.example.hw4.error.ApiException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val apiService: ApiService,
    private val postDao: PostDao,
    private val postRemoveKeyDao: PostRemoveKeyDao,
    private val abbDb: AppDb,
) : RemoteMediator<Int, PostEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {
            val result = when (loadType) {
                LoadType.REFRESH -> apiService.getLatest(state.config.pageSize)
                LoadType.PREPEND -> {
                   // val id = postRemoveKeyDao.max() ?: return MediatorResult.Success(true)
                    return MediatorResult.Success(false)
                   // apiService.getAfter(id, state.config.pageSize)
                }

                LoadType.APPEND -> {
                    val id = postRemoveKeyDao.min() ?: return MediatorResult.Success(false)
                    apiService.getAfter(id, state.config.pageSize)
                }
            }
            if (!result.isSuccessful) {
                throw ApiException(result.code(), result.message())
            }
            val body = result.body() ?: throw ApiException(
                result.code(),
                result.message(),
            )
            abbDb.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        postDao.clear()
                        postRemoveKeyDao.insert(
                            listOf(
                                PostRemoveKeyEntity(
                                    PostRemoveKeyEntity.KeyType.AFTER,
                                    body.first().id
                                ),
                                PostRemoveKeyEntity(
                                    PostRemoveKeyEntity.KeyType.BEFORE,
                                    body.last().id
                                ),
                            )
                        )
                    }

                    LoadType.PREPEND -> {
                        postRemoveKeyDao.insert(
                            PostRemoveKeyEntity(
                                PostRemoveKeyEntity.KeyType.AFTER,
                                body.first().id
                            ),
                        )
                    }

                    LoadType.APPEND -> {
                        postRemoveKeyDao.insert(
                            PostRemoveKeyEntity(
                                PostRemoveKeyEntity.KeyType.BEFORE,
                                body.last().id
                            ),
                        )
                    }

                }

                postDao.insert(body.map(PostEntity::fromDto))
            }
            return MediatorResult.Success(body.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }
}