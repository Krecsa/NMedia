package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post
import java.io.File

class PostRepositoryFileImpl(context: Context) : PostRepository {

    private val file = File(context.filesDir, "posts.json")
    private val gson = Gson()
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type

    private var nextId = 1L
    private var posts = emptyList<Post>()
        set(value) {
            field = value
            sync()
        }

    private val data = MutableLiveData(posts)

    init {
        if (file.exists()) {
            file.bufferedReader().use {
                posts = gson.fromJson(it, type)
                nextId = (posts.maxOfOrNull { it.id } ?: 0) + 1
                data.value = posts
            }
        }
    }

    private fun sync() {
        file.bufferedWriter().use {
            gson.toJson(posts, it)
        }
    }

    override fun getAll(): LiveData<List<Post>> = data

    override fun likeById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(
                likedByMe = !it.likedByMe,
                likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
            )
        }
        data.value = posts
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(shares = it.shares + 1)
        }
        data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
    }

    override fun save(post: Post) {
        if (post.id == 0L) {
            posts = listOf(post.copy(id = nextId++)) + posts
        } else {
            posts = posts.map { if (it.id == post.id) post else it }
        }
        data.value = posts
    }

    override fun updatePost(id: Long, content: String) {
        posts = posts.map {
            if (it.id == id) it.copy(content = content) else it
        }
        data.value = posts
    }
}