package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.netology.nmedia.api.PostsApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val dao: PostDao,
    private val service: PostsApiService
) : PostRepository {

    override val data: LiveData<List<Post>> =
        dao.getAll().asLiveData(Dispatchers.Default).map { entities: List<PostEntity> ->
            entities.toDto()
        }

    override suspend fun getAll() = withContext(Dispatchers.IO) {
        try {
            val response = service.getAll()
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(body.toEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post) = withContext(Dispatchers.IO) {
        try {
            val response = service.save(post)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun removeById(id: Long) = withContext(Dispatchers.IO) {
        try {
            dao.removeById(id)
            val response = service.removeById(id)
            if (!response.isSuccessful) {
                getAll()
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            getAll()
            throw NetworkError
        } catch (e: Exception) {
            getAll()
            throw UnknownError
        }
    }

    override suspend fun likeById(id: Long) = withContext(Dispatchers.IO) {
        try {
            val post = dao.getById(id) ?: throw UnknownError
            val updatedPost = post.toDto().copy(
                likedByMe = !post.likedByMe,
                likes = if (post.likedByMe) post.likes - 1 else post.likes + 1
            )
            dao.insert(PostEntity.fromDto(updatedPost))

            val response = if (updatedPost.likedByMe) {
                service.likeById(id)
            } else {
                service.dislikeById(id)
            }

            if (!response.isSuccessful) {
                getAll()
                throw ApiError(response.code(), response.message())
            }
        } catch (e: IOException) {
            getAll()
            throw NetworkError
        } catch (e: Exception) {
            getAll()
            throw UnknownError
        }
    }
}