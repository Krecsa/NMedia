package ru.netology.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl

private val empty = Post(id = 0L, author = "", content = "", published = "", likes = 0, shares = 0, likedByMe = false)

class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()

    val edited = MutableLiveData(empty)

    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) = repository.removeById(id)

    fun savePost(content: String) {
        val post = Post(
            id = 0L,
            author = "Me",
            content = content,
            published = "Now",
            likes = 0,
            shares = 0,
            likedByMe = false
        )
        repository.save(post)
        edited.value = empty
    }
}