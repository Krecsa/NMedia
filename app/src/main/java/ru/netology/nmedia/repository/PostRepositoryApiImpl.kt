package ru.netology.nmedia.repository

import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Post
import java.io.IOException

class PostRepositoryApiImpl : PostRepository {

    private val service = PostsApi.service

    override fun getAll(): List<Post> {
        return service.getAll().execute().let { response ->
            if (!response.isSuccessful) {
                throw IOException("Network error: ${response.code()}")
            }
            response.body() ?: throw IOException("Empty response")
        }
    }

    override fun likeById(id: Long) {
        service.likeById(id).enqueue(object : Callback<Post> {
            override fun onResponse(call: retrofit2.Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    println("Like error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                println("Like failure: ${t.message}")
            }
        })
    }

    override fun shareById(id: Long) {
    }

    override fun removeById(id: Long) {
        service.removeById(id).enqueue(object : Callback<Unit> {
            override fun onResponse(call: retrofit2.Call<Unit>, response: Response<Unit>) {
                if (!response.isSuccessful) {
                    println("Delete error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<Unit>, t: Throwable) {
                println("Delete failure: ${t.message}")
            }
        })
    }

    override fun save(post: Post) {
        service.save(post).enqueue(object : Callback<Post> {
            override fun onResponse(call: retrofit2.Call<Post>, response: Response<Post>) {
                if (!response.isSuccessful) {
                    println("Save error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                println("Save failure: ${t.message}")
            }
        })
    }

    override fun updatePost(id: Long, content: String) {
        service.getById(id).enqueue(object : Callback<Post> {
            override fun onResponse(call: retrofit2.Call<Post>, response: Response<Post>) {
                if (response.isSuccessful) {
                    val post = response.body()
                    if (post != null) {
                        val updatedPost = post.copy(content = content)
                        service.save(updatedPost).enqueue(object : Callback<Post> {
                            override fun onResponse(call: retrofit2.Call<Post>, response: Response<Post>) {
                                if (!response.isSuccessful) {
                                    println("Update error: ${response.code()}")
                                }
                            }

                            override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                                println("Update failure: ${t.message}")
                            }
                        })
                    }
                } else {
                    println("Update error: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<Post>, t: Throwable) {
                println("Get post failure: ${t.message}")
            }
        })
    }

    private fun getPostById(id: Long): Post {
        return service.getById(id).execute().let { response ->
            if (!response.isSuccessful) {
                throw IOException("Post not found: ${response.code()}")
            }
            response.body() ?: throw IOException("Empty response")
        }
    }
}