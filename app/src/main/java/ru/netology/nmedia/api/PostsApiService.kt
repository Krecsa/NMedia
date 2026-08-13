package ru.netology.nmedia.api

import retrofit2.Call
import retrofit2.http.*
import ru.netology.nmedia.dto.Post

interface PostsApiService {
    @GET("api/posts")
    fun getAll(): Call<List<Post>>

    @GET("api/posts/{id}")
    fun getById(@Path("id") id: Long): Call<Post>

    @POST("api/posts")
    fun save(@Body post: Post): Call<Post>

    @DELETE("api/posts/{id}")
    fun removeById(@Path("id") id: Long): Call<Unit>

    @POST("api/posts/{id}/likes")
    fun likeById(@Path("id") id: Long): Call<Post>

    @DELETE("api/posts/{id}/likes")
    fun unlikeById(@Path("id") id: Long): Call<Post>
}