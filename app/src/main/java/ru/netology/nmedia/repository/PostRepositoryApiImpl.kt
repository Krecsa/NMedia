package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
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
        private const val BASE_URL = "http://10.0.2.2:9999"
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

        val request = Request.Builder()
            .url("$BASE_URL/api/posts/$id/likes")
            .method(method, null)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Like error: ${response.code}")
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

    override fun shareById(id: Long) {
        // TODO: реализовать позже
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
        // TODO: реализовать позже
    }

    override fun updatePost(id: Long, content: String) {
        // TODO: реализовать позже
    }
}