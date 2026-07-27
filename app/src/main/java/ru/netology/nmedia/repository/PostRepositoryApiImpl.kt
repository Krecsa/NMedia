package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit

class PostRepositoryApiImpl : PostRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val postType = object : TypeToken<Post>() {}.type
    private val listType = object : TypeToken<List<Post>>() {}.type

    companion object {
        private const val BASE_URL = "http://192.168.56.1:9999"
    }

    override fun getAll(): List<Post> {
        val request = Request.Builder()
            .url("$BASE_URL/api/posts")
            .build()
        return client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Network error: ${response.code}")
            val body = response.body?.string() ?: throw IOException("Empty response")
            gson.fromJson(body, listType)
        }
    }

    override fun likeById(id: Long) {
        val post = getPostById(id)
        val method = if (post.likedByMe) "DELETE" else "POST"
        val body = "".toRequestBody()  // ← пустое тело вместо null
        val request = Request.Builder()
            .url("$BASE_URL/api/posts/$id/likes")
            .method(method, body)
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Like error: ${response.code}")
        }
    }

    override fun shareById(id: Long) {
        // TODO: реализовать, если сервер поддерживает
    }

    override fun removeById(id: Long) {
        val request = Request.Builder()
            .url("$BASE_URL/api/posts/$id")
            .delete()
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Delete error: ${response.code}")
        }
    }

    override fun save(post: Post) {
        val json = gson.toJson(post)
        val body = json.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$BASE_URL/api/posts")
            .post(body)
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Save error: ${response.code}")
        }
    }

    override fun updatePost(id: Long, content: String) {
        val post = getPostById(id)
        val updatedPost = post.copy(content = content)
        val json = gson.toJson(updatedPost)
        val body = json.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$BASE_URL/api/posts")
            .post(body)
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Update error: ${response.code}")
        }
    }

    private fun getPostById(id: Long): Post {
        val request = Request.Builder()
            .url("$BASE_URL/api/posts/$id")
            .build()
        return client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Post not found: ${response.code}")
            val body = response.body?.string() ?: throw IOException("Empty response")
            gson.fromJson(body, postType)
        }
    }
}