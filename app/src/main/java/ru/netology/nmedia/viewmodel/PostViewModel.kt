package ru.netology.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import android.app.Application
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryFileImpl
import androidx.lifecycle.AndroidViewModel

private val empty = Post(id = 0L, author = "", content = "", published = "", likes = 0, shares = 0, likedByMe = false)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryFileImpl(application)
    val data = repository.getAll()
    val edited = MutableLiveData(empty)

    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) = repository.removeById(id)
    fun edit(post: Post) { edited.value = post }
    fun cancelEdit() { edited.value = empty }
    fun updatePost(id: Long, content: String) {
        repository.updatePost(id, content)
    }

    fun savePost(content: String) {
        val post = edited.value
        if (post != null && post.id != 0L) {
            val trimmed = content.trim()
            if (post.content != trimmed) {
                repository.save(post.copy(content = trimmed))
            }
            edited.value = empty
        } else {
            val newPost = Post(
                id = 0L,
                author = "Нетология. Университет интернет-профессий будущего",
                content = content,
                published = "только что",
                likes = 0,
                likedByMe = false,
                shares = 0
            )
            repository.save(newPost)
        }
    }
}